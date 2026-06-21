# Tài liệu đặc tả API — FMS Unitel Backend

> Phiên bản: v1.0 — Dựa trên `BaseServiceImpl<T, ID>`, `RedisService`, `AuthorizationAspect`, `RequireAuth`, schema PostgreSQL `fms_schema.sql`.

---

## 1. Quy ước chung áp dụng cho MỌI entity

### 1.1. Cấu trúc URL (bắt buộc theo đúng pattern)

Với mỗi entity số ít (ví dụ `user`, `vehicle`), base path là **số ít**, riêng endpoint liệt kê dùng **số nhiều**:

| Hành động | Method | URL Pattern | Ví dụ (User) |
|---|---|---|---|
| Tạo mới | POST | `/api/v1/{entity}/create` | `/api/v1/user/create` |
| Cập nhật toàn bộ | PUT | `/api/v1/{entity}/update/{id}` | `/api/v1/user/update/{id}` |
| Cập nhật 1 phần (PATCH map) | PATCH | `/api/v1/{entity}/update-partial/{id}` | `/api/v1/user/update-partial/{id}` |
| Lấy theo ID | GET | `/api/v1/{entity}/get-by-id/{id}` | `/api/v1/user/get-by-id/{id}` |
| Lấy toàn bộ (không phân trang) | GET | `/api/v1/{entities}/get-all` | `/api/v1/users/get-all` |
| Lọc/phân trang/sắp xếp | POST | `/api/v1/{entities}/filter` | `/api/v1/users/filter` |
| Đổi trạng thái | PATCH | `/api/v1/{entity}/change-status/{id}` | `/api/v1/user/change-status/{id}` |
| Xoá mềm | DELETE | `/api/v1/{entity}/soft-delete/{id}` | `/api/v1/user/soft-delete/{id}` |
| Xoá cứng | DELETE | `/api/v1/{entity}/hard-delete/{id}` | `/api/v1/user/hard-delete/{id}` |

**Quy tắc dùng `/get-all`:** CHỈ áp dụng cho bảng danh mục ít dữ liệu (≤ vài trăm dòng, không tăng trưởng theo thời gian thực): `roles`, `vehicle_types`, `organizations`, `geofences`. KHÔNG áp dụng cho bảng giao dịch lớn (`trips`, `dispatch_requests`, `gps_positions`, `audit_logs`...) — các bảng này CHỈ có `/filter`.

### 1.2. Response Envelope — dùng `ResponseData<T>` cho mọi response

```java
ResponseData.<T>builder()
    .status(200)
    .data(result)
    .message("Thành công")
    .build();
```

Lỗi nghiệp vụ → `ResponseData` với `status` tương ứng (400/403/404/409) + `error` chứa mã lỗi, `message` chứa mô tả tiếng Việt. Xử lý tập trung qua `@RestControllerAdvice` (không lặp lại try-catch ở từng controller).

### 1.3. Cơ chế Cache Redis — CHỈ áp dụng cho nhóm bảng danh mục tĩnh

**Nhóm có cache** (danh mục ít thay đổi, đọc nhiều hơn ghi rất nhiều lần):
`organizations`, `roles`, `vehicle_types`, `geofences`, `system_configs`

**Nhóm KHÔNG cache** (bảng giao dịch / dữ liệu thay đổi liên tục):
`users`, `user_roles`, `audit_logs`, `notifications`, `vehicles`, `vehicle_documents`, `vehicle_healths`, `maintenance_orders`, `maintenance_items`, `drivers`, `driver_documents`, `customers`, `dispatch_requests`, `dispatch_assignments`, `trips`, `trip_stops`, `trip_incidents`, `fuel_logs`, `gps_positions`, `gps_alerts`, `partners`, `partner_vehicles`, `partner_drivers`, `partner_performances`, `file_attachments`

**Lý do `vehicles`/`drivers` KHÔNG nằm trong nhóm cache** dù số lượng hữu hạn: trạng thái (`status`, `current_odometer`, `is_dispatch_eligible`) thay đổi liên tục theo vận hành thực tế (mỗi lần điều xe, mỗi lần GPS cập nhật health) — cache get-by-id cho các bảng này tạo rủi ro đọc dữ liệu cũ trong nghiệp vụ chặn điều xe (FR-DP-04), hậu quả nghiêm trọng hơn lợi ích hiệu năng mang lại.

#### Pattern cache áp dụng cho GET-BY-ID của nhóm có cache (cache-aside)

```java
@Override
@Transactional(readOnly = true)
public Organization getByIdCached(UUID id) {
    String key = redisService.buildKey("organization", id.toString());

    Organization cached = redisService.get(key, Organization.class);
    if (cached != null) {
        return cached;
    }

    Organization entity = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Organization không tồn tại: " + id));

    redisService.set(key, entity, 3600); // TTL 1 giờ
    return entity;
}
```

#### Pattern invalidate cache — BẮT BUỘC gọi sau mọi lệnh ghi (create/update/changeStatus/delete) trên entity thuộc nhóm có cache

```java
@Override
@Transactional
public Organization update(UUID id, Organization entity) {
    Organization updated = super.update(id, entity);
    redisService.delete(redisService.buildKey("organization", id.toString()));
    return updated;
}
```

> **Quy tắc bắt buộc:** Mọi service thuộc nhóm có cache phải override `create`, `update`, `update(ID, T)`, `updateFromMap`, `changeStatus`, `delete(ID)` để gọi `redisService.delete(key)` tương ứng — tránh cache stale. Không cache kết quả của `/filter` hay `/get-all` (vì tham số filter biến thiên vô hạn, build key cho mọi tổ hợp filter là không khả thi và dễ phình bộ nhớ Redis).

### 1.4. Soft-delete / Hard-delete

- **Soft-delete**: gọi `changeStatus(id, "deleted")` có sẵn trong `BaseServiceImpl`. Dữ liệu vẫn còn trong DB, chỉ đổi `status`. Các query mặc định (`/filter`, `/get-all`) PHẢI tự động loại trừ `status = 'deleted'` trừ khi người gọi có quyền `SYSTEM_ADMIN` và truyền cờ `includeDeleted=true`.
- **Hard-delete**: gọi `repository.deleteById(id)` thật sự — XOÁ VĨNH VIỄN. Chỉ `SYSTEM_ADMIN` được phép, và bắt buộc ghi `audit_logs` trước khi xoá (vì sau khi xoá không còn dữ liệu để truy vết).

### 1.5. Phân quyền — áp dụng `@RequireAuth` theo mẫu

```java
@RequireAuth(roles = {RoleType.SYSTEM_ADMIN}) // chỉ định 1 role cụ thể
@RequireAuth(roles = {RoleType.DISPATCHER, RoleType.OPS_MANAGER}, rolesLogic = RequireAuth.LogicType.OR) // 1 trong 2
@RequireAuth(roles = {RoleType.DISPATCHER, RoleType.OPS_MANAGER}, rolesLogic = RequireAuth.LogicType.AND) // phải có cả 2
@RequireAuth(roles = {RoleType.ALL}, inWorkspace = true) // bất kỳ role nào, miễn thuộc đúng workspace
```

Cột "Thuộc workspace" trong các bảng bên dưới = `Có` nghĩa là `inWorkspace = true` BẮT BUỘC truyền header `orgId`, và dữ liệu trả về/thao tác giới hạn trong phạm vi `org_id` đó (trừ `SYSTEM_ADMIN` bypass toàn bộ theo cơ chế đã code trong `AuthorizationAspect`).

### 1.6. Tầng Service kế thừa `BaseServiceImpl`

```java
@Service
public class OrganizationServiceImpl extends BaseServiceImpl<Organization, UUID> {

    private final RedisService redisService;

    public OrganizationServiceImpl(OrganizationRepository repository, RedisService redisService) {
        super(repository); // statusFieldName mặc định = "status"
        this.redisService = redisService;
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }
}
```

---

## 2. Mục lục chi tiết theo module

| File                    | Module | Số entity |
|-------------------------|---|---|
| `01_admin_module.md`    | Quản trị hệ thống & phân quyền | 7 |
| `02_vehicle_module.md`  | Phương tiện & bảo trì | 6 |
| `03_driver_module.md`   | Tài xế | 2 |
| `04_customer_module.md` | Khách hàng | 1 |
| `05_dispatch_module.md` | Điều xe & chuyến | 6 |
| `06_gps_module.md`      | GPS & Telemetry | 3 |
| `07_partner_module.md`  | Đối tác vận tải | 4 |

Mỗi file con trình bày từng entity theo cấu trúc: **(a)** Entity & Repository liên quan, **(b)** Bảng API đầy đủ (URL, method, quyền, workspace, cache), **(c)** Chi tiết triển khai từng API kèm logic nghiệp vụ đặc thù (nếu có).
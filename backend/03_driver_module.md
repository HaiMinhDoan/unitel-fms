# Module 1 — Quản trị hệ thống & Phân quyền

Entity: `Organization`, `Role`, `User`, `UserRole`, `AuditLog`, `SystemConfig`, `Notification`

---

## 1.1. Organization (`organizations`)

**Nhóm cache**: CÓ (danh mục tĩnh) — TTL 3600s, key `organization:{id}`

| # | API | Method | URL | Quyền (role) | Logic | Workspace | Cache |
|---|---|---|---|---|---|---|---|
| 1 | Tạo tổ chức | POST | `/api/v1/organization/create` | `SYSTEM_ADMIN` | — | Không | Invalidate: không có (tạo mới) |
| 2 | Cập nhật toàn bộ | PUT | `/api/v1/organization/update/{id}` | `SYSTEM_ADMIN` | — | Không | Invalidate `organization:{id}` |
| 3 | Cập nhật 1 phần | PATCH | `/api/v1/organization/update-partial/{id}` | `SYSTEM_ADMIN` | — | Không | Invalidate `organization:{id}` |
| 4 | Lấy theo ID | GET | `/api/v1/organization/get-by-id/{id}` | `ALL` | OR | Có | Đọc cache trước, fallback DB |
| 5 | Lấy toàn bộ | GET | `/api/v1/organizations/get-all` | `ALL` | OR | Không | Không cache (danh sách động theo filter ẩn `status != deleted`) |
| 6 | Lọc/phân trang | POST | `/api/v1/organizations/filter` | `ALL` | OR | Không | Không cache |
| 7 | Đổi trạng thái | PATCH | `/api/v1/organization/change-status/{id}` | `SYSTEM_ADMIN` | — | Không | Invalidate `organization:{id}` |
| 8 | Xoá mềm | DELETE | `/api/v1/organization/soft-delete/{id}` | `SYSTEM_ADMIN` | — | Không | Invalidate `organization:{id}` |
| 9 | Xoá cứng | DELETE | `/api/v1/organization/hard-delete/{id}` | `SYSTEM_ADMIN` | — | Không | Invalidate `organization:{id}` |

### Chi tiết logic nghiệp vụ đặc thù

**Tạo tổ chức (`create`)**: validate `code` UNIQUE trước khi insert (bắt sớm ở service, trả lỗi 409 thay vì để DB ném `DataIntegrityViolationException` khó đọc). Nếu `parent_id` được truyền, validate `parent_id` tồn tại và đang `status = active`.

```java
@Override
@Transactional
public Organization create(Organization entity) {
    if (repository.existsByCode(entity.getCode())) {
        throw new ConflictException("Mã tổ chức '" + entity.getCode() + "' đã tồn tại");
    }
    if (entity.getParentId() != null) {
        organizationRepository.findById(entity.getParentId())
                .filter(p -> "active".equals(p.getStatus()))
                .orElseThrow(() -> new InvalidFieldException("Tổ chức cha không tồn tại hoặc không active"));
    }
    return super.create(entity);
}
```

**Hard-delete**: PHẢI kiểm tra ràng buộc tham chiếu trước khi xoá (vì `users.org_id`, `vehicles.org_id`, `dispatch_requests.org_id`... đều FK tới bảng này). Nếu còn bản ghi con tham chiếu → trả lỗi 409, hướng dẫn dùng soft-delete thay thế.

```java
@Override
@Transactional
public void hardDelete(UUID id) {
    if (userRepository.existsByOrgId(id) || vehicleRepository.existsByOrgId(id)) {
        throw new ConflictException("Không thể xoá cứng: tổ chức vẫn còn dữ liệu liên quan. Dùng soft-delete.");
    }
    repository.deleteById(id);
    redisService.delete(redisService.buildKey("organization", id.toString()));
}
```

---

## 1.2. Role (`roles`)

**Nhóm cache**: CÓ — TTL 3600s, key `role:{id}`. Riêng `/get-all` của Role NÊN cache thêm 1 key tổng `role:all-list` vì đây là danh mục cực tĩnh, được gọi liên tục bởi mọi màn hình gán quyền.

| # | API | Method | URL | Quyền | Logic | Workspace | Cache |
|---|---|---|---|---|---|---|---|
| 1 | Tạo role | POST | `/api/v1/role/create` | `SYSTEM_ADMIN` | — | Không | Invalidate `role:all-list` |
| 2 | Cập nhật toàn bộ | PUT | `/api/v1/role/update/{id}` | `SYSTEM_ADMIN` | — | Không | Invalidate `role:{id}`, `role:all-list` |
| 3 | Cập nhật 1 phần | PATCH | `/api/v1/role/update-partial/{id}` | `SYSTEM_ADMIN` | — | Không | Invalidate `role:{id}`, `role:all-list` |
| 4 | Lấy theo ID | GET | `/api/v1/role/get-by-id/{id}` | `ALL` | OR | Không | Đọc cache trước |
| 5 | Lấy toàn bộ | GET | `/api/v1/roles/get-all` | `ALL` | OR | Không | **Cache đặc biệt** `role:all-list`, TTL 3600s |
| 6 | Lọc/phân trang | POST | `/api/v1/roles/filter` | `SYSTEM_ADMIN` | — | Không | Không cache |
| 7 | Đổi trạng thái | PATCH | `/api/v1/role/change-status/{id}` | `SYSTEM_ADMIN` | — | Không | Invalidate cả 2 key |
| 8 | Xoá mềm | DELETE | `/api/v1/role/soft-delete/{id}` | `SYSTEM_ADMIN` | — | Không | Invalidate cả 2 key |
| 9 | Xoá cứng | DELETE | `/api/v1/role/hard-delete/{id}` | `SYSTEM_ADMIN` | — | Không | Invalidate cả 2 key |

### Chi tiết logic nghiệp vụ đặc thù

**`getAllCached()`** — ví dụ cache danh sách (khác với cache từng bản ghi đơn lẻ):

```java
@Transactional(readOnly = true)
public List<Role> getAllCached() {
    String key = "role:all-list";
    List<Role> cached = redisService.get(key, ArrayList.class); // deserialize generic list, xem lưu ý bên dưới
    if (cached != null) return cached;

    List<Role> roles = repository.findAllByStatusNot("deleted");
    redisService.set(key, roles, 3600);
    return roles;
}
```

> **Lưu ý kỹ thuật quan trọng:** `RedisServiceImpl.get(key, clazz)` hiện dùng `objectMapper.convertValue(obj, clazz)` — với generic `List<Role>`, truyền `ArrayList.class` sẽ mất type parameter (Jackson type erasure), dễ lỗi khi deserialize entity phức tạp có quan hệ lồng nhau. Khuyến nghị bổ sung overload `get(String key, TypeReference<T> typeRef)` trong `RedisService` cho riêng các trường hợp cache List, hoặc serialize thủ công qua `ObjectMapper.writeValueAsString` + lưu String thay vì lưu Object trực tiếp.

**Xoá role**: trước khi soft/hard-delete, kiểm tra `user_roles` còn tham chiếu role này không — nếu còn, chặn xoá cứng (tương tự Organization).

---

## 1.3. User (`users`)

**Nhóm cache**: KHÔNG (dữ liệu người dùng thay đổi thường xuyên: `last_login_at`, `status`...)

| # | API | Method | URL | Quyền | Logic | Workspace |
|---|---|---|---|---|---|---|
| 1 | Tạo user | POST | `/api/v1/user/create` | `SYSTEM_ADMIN` | — | Không |
| 2 | Cập nhật toàn bộ | PUT | `/api/v1/user/update/{id}` | `SYSTEM_ADMIN`, `ALL` (tự sửa chính mình) | xem logic | Có (trừ SYSTEM_ADMIN) |
| 3 | Cập nhật 1 phần | PATCH | `/api/v1/user/update-partial/{id}` | tương tự #2 | — | Có |
| 4 | Lấy theo ID | GET | `/api/v1/user/get-by-id/{id}` | `ALL` | OR | Có |
| 5 | Lấy toàn bộ | GET | — KHÔNG có endpoint này (bảng tăng trưởng) | — | — | — |
| 6 | Lọc/phân trang | POST | `/api/v1/users/filter` | `SYSTEM_ADMIN`, `OPS_MANAGER`, `HR_LEGAL` | OR | Có |
| 7 | Đổi trạng thái (khoá/mở user) | PATCH | `/api/v1/user/change-status/{id}` | `SYSTEM_ADMIN` | — | Không |
| 8 | Xoá mềm | DELETE | `/api/v1/user/soft-delete/{id}` | `SYSTEM_ADMIN` | — | Không |
| 9 | Xoá cứng | DELETE | `/api/v1/user/hard-delete/{id}` | `SYSTEM_ADMIN` | — | Không |
| 10 | Lấy thông tin tài khoản hiện tại | GET | `/api/v1/user/me` | `ALL` | OR | Không (lấy theo token, không cần orgId) |

### Chi tiết logic nghiệp vụ đặc thù

**`/user/me`** — không dùng `getOne(id)` thông thường, mà lấy `userId` trực tiếp từ `SecurityContextHolder.getAuthInfo()`:

```java
@GetMapping("/me")
@RequireAuth(roles = {RoleType.ALL})
public ResponseData<User> getMe() {
    UUID userId = SecurityContextHolder.getAuthInfo().getUserId();
    User user = userService.getOne(userId)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy thông tin tài khoản"));
    return ResponseData.<User>builder().status(200).data(user).message("Thành công").build();
}
```

**Update user — quyền tự-sửa-chính-mình**: `AuthorizationAspect` hiện tại chỉ check role/workspace, KHÔNG check "có phải chính chủ tài khoản không" (ownership). Đây là field-level/row-level permission nằm ngoài phạm vi annotation hiện có — phải tự check thêm trong Controller/Service:

```java
@PutMapping("/update/{id}")
@RequireAuth(roles = {RoleType.ALL}, inWorkspace = true)
public ResponseData<User> update(@PathVariable UUID id, @RequestBody User payload) {
    AuthInfo authInfo = SecurityContextHolder.getAuthInfo();
    boolean isSelf = authInfo.getUserId().equals(id);
    boolean isAdmin = authInfo.hasAnyRole(RoleType.SYSTEM_ADMIN);

    if (!isSelf && !isAdmin) {
        throw new AccessDeniedException("Chỉ được sửa thông tin chính mình hoặc cần quyền SYSTEM_ADMIN");
    }
    // Nếu không phải admin, cấm đổi org_id / status qua endpoint này (tự sửa field nhạy cảm)
    if (!isAdmin) {
        payload.setOrgId(null); // giữ nguyên org cũ, không cho user tự đổi org
        payload.setStatus(null);
    }
    return ResponseData.<User>builder().status(200).data(userService.update(id, payload)).message("Cập nhật thành công").build();
}
```

**Tạo user**: validate `username`/`email` UNIQUE, hash password nếu có field password riêng (schema hiện tại không có cột `password_hash` trực tiếp trong `users` — xác nhận lại với team có dùng SSO hoàn toàn hay cần local login; nếu cần, bổ sung cột và xử lý bcrypt tại đây).

---

## 1.4. UserRole (`user_roles`)

**Nhóm cache**: KHÔNG. Đặc biệt — **PHẢI invalidate cache JWT/quyền của user liên quan** dù bảng này không nằm trong nhóm cache (vì `AuthInfo` được nhúng quyền vào JWT lúc đăng nhập; đổi `user_roles` không tự động cập nhật JWT cũ đang lưu ở client cho tới khi hết hạn — đây là rủi ro bảo mật cần lưu ý, xem mục cảnh báo bên dưới).

| # | API | Method | URL | Quyền | Logic | Workspace |
|---|---|---|---|---|---|---|
| 1 | Gán role cho user | POST | `/api/v1/user-role/create` | `SYSTEM_ADMIN` | — | Có (gán role theo org cụ thể) |
| 2 | Cập nhật | PUT | `/api/v1/user-role/update/{id}` | `SYSTEM_ADMIN` | — | Có |
| 3 | Cập nhật 1 phần | PATCH | `/api/v1/user-role/update-partial/{id}` | `SYSTEM_ADMIN` | — | Có |
| 4 | Lấy theo ID | GET | `/api/v1/user-role/get-by-id/{id}` | `SYSTEM_ADMIN`, `OPS_MANAGER` | OR | Có |
| 5 | Lấy toàn bộ role của 1 user | GET | `/api/v1/user-roles/get-by-user/{userId}` | `ALL` | OR | Có |
| 6 | Lọc/phân trang | POST | `/api/v1/user-roles/filter` | `SYSTEM_ADMIN` | — | Có |
| 7 | Đổi trạng thái (thu hồi quyền tạm) | PATCH | `/api/v1/user-role/change-status/{id}` | `SYSTEM_ADMIN` | — | Có |
| 8 | Xoá mềm | DELETE | `/api/v1/user-role/soft-delete/{id}` | `SYSTEM_ADMIN` | — | Có |
| 9 | Xoá cứng | DELETE | `/api/v1/user-role/hard-delete/{id}` | `SYSTEM_ADMIN` | — | Có |

### Chi tiết logic nghiệp vụ đặc thù

**Gán role (`create`)**: bắt buộc validate UNIQUE `(user_id, role_id, org_id)` ở tầng service trước khi insert — kể cả khi DB đã có constraint, validate sớm để trả message rõ ràng thay vì lỗi SQL thô:

```java
@Override
@Transactional
public UserRole create(UserRole entity) {
    boolean exists = entity.getOrgId() == null
            ? repository.existsByUserIdAndRoleIdAndOrgIdIsNull(entity.getUserId(), entity.getRoleId())
            : repository.existsByUserIdAndRoleIdAndOrgId(entity.getUserId(), entity.getRoleId(), entity.getOrgId());
    if (exists) {
        throw new ConflictException("User đã có role này trong workspace tương ứng");
    }
    entity.setGrantedBy(SecurityContextHolder.getAuthInfo().getUserId());
    UserRole saved = super.create(entity);

    auditLogService.record("GRANT_ROLE", EntityType.USER_ROLE, saved.getId(), null, saved);
    return saved;
}
```

> **⚠️ Cảnh báo bảo mật cần lưu ý khi triển khai `JwtService`:** Nếu `AuthInfo` (chứa danh sách role) được nhúng trực tiếp vào JWT payload lúc đăng nhập, thì việc **thu hồi/đổi role qua API này KHÔNG có hiệu lực ngay lập tức** — user vẫn dùng token cũ với quyền cũ cho tới khi token hết hạn. Hai hướng xử lý: (1) đặt JWT access-token TTL ngắn (5-15 phút) + refresh token, hoặc (2) `AuthInterceptor`/`AuthorizationAspect` luôn truy vấn lại `user_roles` mới nhất từ DB/Redis thay vì tin hoàn toàn vào claims trong JWT (tốn thêm 1 query/Redis lookup mỗi request, đổi lại tính nhất quán quyền theo thời gian thực — phù hợp với mức độ nhạy cảm của FMS khi 1 dispatcher bị thu hồi quyền cần có hiệu lực ngay).

---

## 1.5. AuditLog (`audit_logs`)

**Nhóm cache**: KHÔNG. Bảng append-only (ghi nhiều, gần như không update/delete).

| # | API | Method | URL | Quyền | Logic | Workspace |
|---|---|---|---|---|---|---|
| 1 | Tạo (nội bộ, không expose) | — | — | — (gọi nội bộ qua `auditLogService.record(...)`, không có Controller endpoint POST công khai) | — | — |
| 2 | Lấy theo ID | GET | `/api/v1/audit-log/get-by-id/{id}` | `SYSTEM_ADMIN`, `OPS_MANAGER` | OR | Có (OPS_MANAGER chỉ xem log trong org mình) |
| 3 | Lọc/phân trang | POST | `/api/v1/audit-logs/filter` | `SYSTEM_ADMIN`, `OPS_MANAGER` | OR | Có |
| 4 | Xoá cứng (dọn log cũ, định kỳ) | DELETE | `/api/v1/audit-log/hard-delete/{id}` | `SYSTEM_ADMIN` | — | Không |

> Không có `update`, không có `soft-delete`, không có `change-status` — log kiểm toán theo nguyên tắc **immutable**, sửa được thì mất giá trị truy vết. Không có `/create` công khai vì ghi log là tác dụng phụ tự động của các API khác, không phải hành động người dùng chủ động gọi.

### Chi tiết logic nghiệp vụ đặc thù

**Helper method dùng nội bộ** (gọi từ mọi service khác khi cần ghi audit):

```java
@Service
public class AuditLogServiceImpl extends BaseServiceImpl<AuditLog, UUID> {

    public void record(String action, String entityType, UUID entityId, Object oldValues, Object newValues) {
        AuthInfo authInfo = SecurityContextHolder.getAuthInfo();
        AuditLog log = AuditLog.builder()
                .userId(authInfo != null ? authInfo.getUserId() : null)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .oldValues(oldValues != null ? objectMapper.valueToTree(oldValues) : null)
                .newValues(newValues != null ? objectMapper.valueToTree(newValues) : null)
                .status("recorded")
                .build();
        repository.save(log); // KHÔNG dùng super.create() để tránh vòng lặp gọi audit-log-cho-chính-audit-log
    }
}
```

> Lưu ý: `record()` chạy trong cùng transaction với hành động nghiệp vụ gốc (ví dụ override điều xe — FR-DP-06). Nếu transaction nghiệp vụ rollback, log audit cũng rollback theo — đây là hành vi ĐÚNG vì không muốn ghi log cho hành động chưa thực sự xảy ra. Nếu sau này cần audit log "sống sót" độc lập kể cả khi nghiệp vụ rollback (để điều tra cả những thao tác lỗi), cần tách `record()` sang `REQUIRES_NEW` transaction propagation.

---

## 1.6. SystemConfig (`system_configs`)

**Nhóm cache**: CÓ — TTL 3600s, key `system-config:{configKey}` (dùng `config_key` làm key thay vì UUID `id`, vì cách truy vấn thực tế luôn theo `config_key`, ví dụ `"dispatch.sla.threshold_minutes"`).

| # | API | Method | URL | Quyền | Logic | Workspace |
|---|---|---|---|---|---|---|
| 1 | Tạo config | POST | `/api/v1/system-config/create` | `SYSTEM_ADMIN` | — | Không |
| 2 | Cập nhật | PUT | `/api/v1/system-config/update/{id}` | `SYSTEM_ADMIN` | — | Không |
| 3 | Lấy theo ID | GET | `/api/v1/system-config/get-by-id/{id}` | `SYSTEM_ADMIN` | — | Không |
| 4 | Lấy theo key (dùng nội bộ + UI cấu hình) | GET | `/api/v1/system-config/get-by-key/{configKey}` | `ALL` | OR | Không |
| 5 | Lấy toàn bộ | GET | `/api/v1/system-configs/get-all` | `SYSTEM_ADMIN` | — | Không |
| 6 | Lọc/phân trang | POST | `/api/v1/system-configs/filter` | `SYSTEM_ADMIN` | — | Không |
| 7 | Xoá mềm | DELETE | `/api/v1/system-config/soft-delete/{id}` | `SYSTEM_ADMIN` | — | Không |
| 8 | Xoá cứng | DELETE | `/api/v1/system-config/hard-delete/{id}` | `SYSTEM_ADMIN` | — | Không |

### Chi tiết logic nghiệp vụ đặc thù

**`get-by-key`** — endpoint quan trọng nhất của entity này vì được các module khác gọi liên tục (ví dụ `FleetManager` cần đọc ngưỡng "sắp hết hạn giấy tờ" = 30 ngày theo FR-CP-02):

```java
@Transactional(readOnly = true)
public SystemConfig getByKeyCached(String configKey) {
    String redisKey = redisService.buildKey("system-config", configKey);
    SystemConfig cached = redisService.get(redisKey, SystemConfig.class);
    if (cached != null) return cached;

    SystemConfig config = repository.findByConfigKey(configKey)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy cấu hình: " + configKey));
    redisService.set(redisKey, config, 3600);
    return config;
}
```

Mọi `update`/`create` PHẢI invalidate đúng key theo `config_key` (không phải theo UUID `id`):

```java
@Override
@Transactional
public SystemConfig update(UUID id, SystemConfig entity) {
    SystemConfig updated = super.update(id, entity);
    redisService.delete(redisService.buildKey("system-config", updated.getConfigKey()));
    return updated;
}
```

---

## 1.7. Notification (`notifications`)

**Nhóm cache**: KHÔNG (dữ liệu cá nhân hoá theo user, thay đổi liên tục bởi `is_read`).

| # | API | Method | URL | Quyền | Logic | Workspace |
|---|---|---|---|---|---|---|
| 1 | Tạo (nội bộ, qua service khác gọi) | — | — | — | — | — |
| 2 | Lấy theo ID | GET | `/api/v1/notification/get-by-id/{id}` | `ALL` | OR | Không (check ownership thủ công) |
| 3 | Lấy danh sách của tôi | GET | `/api/v1/notifications/get-my-notifications` | `ALL` | OR | Không |
| 4 | Lọc/phân trang | POST | `/api/v1/notifications/filter` | `SYSTEM_ADMIN` (xem toàn bộ), `ALL` (tự filter theo `user_id` của mình) | OR | Không |
| 5 | Đánh dấu đã đọc | PATCH | `/api/v1/notification/mark-as-read/{id}` | `ALL` | OR | Không (check ownership) |
| 6 | Đánh dấu tất cả đã đọc | PATCH | `/api/v1/notifications/mark-all-as-read` | `ALL` | OR | Không |
| 7 | Xoá mềm | DELETE | `/api/v1/notification/soft-delete/{id}` | `ALL` (chỉ xoá của chính mình) | OR | Không |
| 8 | Xoá cứng | DELETE | `/api/v1/notification/hard-delete/{id}` | `SYSTEM_ADMIN` | — | Không |

### Chi tiết logic nghiệp vụ đặc thù

**Check ownership cho `get-by-id`, `mark-as-read`, `soft-delete`** — tương tự vấn đề ở `User`, `AuthorizationAspect` không tự check được "thông báo này có phải của user đang gọi không", cần check thủ công trong Service:

```java
@Transactional
public Notification markAsRead(UUID id) {
    Notification noti = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy thông báo"));

    UUID currentUserId = SecurityContextHolder.getAuthInfo().getUserId();
    if (!noti.getUserId().equals(currentUserId)) {
        throw new AccessDeniedException("Không có quyền với thông báo này");
    }

    noti.setIsRead(true);
    return repository.save(noti);
}
```

**`mark-all-as-read`** — dùng bulk update qua `@Modifying @Query` thay vì load từng entity rồi save (tránh N+1 update khi user có hàng trăm thông báo chưa đọc):

```java
@Modifying
@Query("UPDATE Notification n SET n.isRead = true WHERE n.userId = :userId AND n.isRead = false")
int markAllAsRead(@Param("userId") UUID userId);
```
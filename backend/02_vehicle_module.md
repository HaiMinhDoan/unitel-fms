# Module 2 — Phương tiện & Bảo trì

Entity: `VehicleType`, `Vehicle`, `VehicleDocument`, `VehicleHealth`, `MaintenanceOrder`, `MaintenanceItem`

---

## 2.1. VehicleType (`vehicle_types`)

**Nhóm cache**: CÓ — TTL 3600s, key `vehicle-type:{id}` + `vehicle-type:all-list`

| #   | API             | Method | URL                                        | Quyền                           | Logic | Workspace | Cache                                                          |
| --- | --------------- | ------ | ------------------------------------------ | ------------------------------- | ----- | --------- | -------------------------------------------------------------- |
| 1   | Tạo             | POST   | `/api/v1/vehicle-type/create`              | `SYSTEM_ADMIN`, `FLEET_MANAGER` | OR    | Không     | Invalidate `all-list`                                          |
| 2   | Cập nhật        | PUT    | `/api/v1/vehicle-type/update/{id}`         | `SYSTEM_ADMIN`, `FLEET_MANAGER` | OR    | Không     | Invalidate cả 2 key                                            |
| 3   | Cập nhật 1 phần | PATCH  | `/api/v1/vehicle-type/update-partial/{id}` | `SYSTEM_ADMIN`, `FLEET_MANAGER` | OR    | Không     | Invalidate cả 2 key                                            |
| 4   | Lấy theo ID     | GET    | `/api/v1/vehicle-type/get-by-id/{id}`      | `ALL`                           | OR    | Không     | Đọc cache trước                                                |
| 5   | Lấy toàn bộ     | GET    | `/api/v1/vehicle-types/get-all`            | `ALL`                           | OR    | Không     | Cache `all-list`, TTL 3600s                                    |
| 6   | Lọc/phân trang  | POST   | `/api/v1/vehicle-types/filter`             | `SYSTEM_ADMIN`, `FLEET_MANAGER` | OR    | Không     | Không cache                                                    |
| 7   | Đổi trạng thái  | PATCH  | `/api/v1/vehicle-type/change-status/{id}`  | `SYSTEM_ADMIN`                  | —     | Không     | Invalidate cả 2 key                                            |
| 8   | Xoá mềm         | DELETE | `/api/v1/vehicle-type/soft-delete/{id}`    | `SYSTEM_ADMIN`                  | —     | Không     | Invalidate cả 2 key                                            |
| 9   | Xoá cứng        | DELETE | `/api/v1/vehicle-type/hard-delete/{id}`    | `SYSTEM_ADMIN`                  | —     | Không     | Invalidate cả 2 key, check FK `vehicles.vehicle_type_id` trước |

---

## 2.2. Vehicle (`vehicles`)

**Nhóm cache**: KHÔNG (trạng thái thay đổi liên tục: `status`, `current_odometer`, `status_changed_at`).

| #   | API                                  | Method | URL                                               | Quyền           | Logic | Workspace                         |
| --- | ------------------------------------ | ------ | ------------------------------------------------- | --------------- | ----- | --------------------------------- |
| 1   | Tạo                                  | POST   | `/api/v1/vehicle/create`                          | `FLEET_MANAGER` | —     | Có                                |
| 2   | Cập nhật toàn bộ                     | PUT    | `/api/v1/vehicle/update/{id}`                     | `FLEET_MANAGER` | —     | Có                                |
| 3   | Cập nhật 1 phần                      | PATCH  | `/api/v1/vehicle/update-partial/{id}`             | `FLEET_MANAGER` | —     | Có                                |
| 4   | Lấy theo ID                          | GET    | `/api/v1/vehicle/get-by-id/{id}`                  | `ALL`           | OR    | Có                                |
| 5   | Lấy toàn bộ                          | GET    | — KHÔNG có (3.000+ xe, bắt buộc phân trang)       | —               | —     | —                                 |
| 6   | Lọc/phân trang                       | POST   | `/api/v1/vehicles/filter`                         | `ALL`           | OR    | Có                                |
| 7   | Đổi trạng thái                       | PATCH  | `/api/v1/vehicle/change-status/{id}`              | `FLEET_MANAGER` | —     | Có                                |
| 8   | Xoá mềm                              | DELETE | `/api/v1/vehicle/soft-delete/{id}`                | `FLEET_MANAGER` | —     | Có                                |
| 9   | Xoá cứng                             | DELETE | `/api/v1/vehicle/hard-delete/{id}`                | `SYSTEM_ADMIN`  | —     | Không (admin xoá xuyên workspace) |
| 10  | **Lấy xe sẵn sàng điều phối**        | GET    | `/api/v1/vehicles/get-available-for-dispatch`     | `DISPATCHER`    | —     | Có                                |
| 11  | **Kiểm tra điều kiện trước điều xe** | GET    | `/api/v1/vehicle/{id}/check-dispatch-eligibility` | `DISPATCHER`    | —     | Có                                |

### Chi tiết logic nghiệp vụ đặc thù

**`get-available-for-dispatch` (FR-DP-02)** — query tổng hợp nhiều bảng, KHÔNG dùng `filter` generic vì cần JOIN điều kiện phức tạp (xe đang `active`, không bị block bởi `vehicle_healths.is_dispatch_blocked`, không đang có `dispatch_assignments` active):

```java
@Query("""
    SELECT v FROM Vehicle v
    WHERE v.orgId = :orgId
      AND v.status = 'active'
      AND NOT EXISTS (
          SELECT 1 FROM DispatchAssignment da
          WHERE da.vehicleId = v.id AND da.status = 'active'
      )
      AND NOT EXISTS (
          SELECT 1 FROM VehicleHealth vh
          WHERE vh.vehicleId = v.id AND vh.isDispatchBlocked = true
          AND vh.createdAt = (SELECT MAX(vh2.createdAt) FROM VehicleHealth vh2 WHERE vh2.vehicleId = v.id)
      )
    """)
List<Vehicle> findAvailableForDispatch(@Param("orgId") UUID orgId);
```

**`check-dispatch-eligibility` (FR-DP-04 — cổng chặn điều xe)** — đây là API lõi của toàn hệ thống, tổng hợp 4 điều kiện đồng thời mô tả trong tài liệu đặc tả mục 3.10:

```java
public DispatchEligibilityResult checkDispatchEligibility(UUID vehicleId) {
    Vehicle vehicle = getOne(vehicleId).orElseThrow(() -> new NotFoundException("Xe không tồn tại"));

    boolean loadOk = true; // so khớp với cargo_weight_ton của dispatch_request tương ứng, check ở bước gán cụ thể
    boolean vehicleDocsOk = vehicleDocumentService.hasNoExpiredOrMissingDocs(vehicleId);
    boolean healthOk = vehicleHealthService.getLatestHealth(vehicleId)
            .map(h -> !h.getIsDispatchBlocked())
            .orElse(false); // chưa từng được chấm sức khỏe -> coi như KHÔNG đạt, ép phải đánh giá trước
    boolean docsOk = vehicleDocsOk;

    return DispatchEligibilityResult.builder()
            .vehicleDocsOk(vehicleDocsOk)
            .healthOk(healthOk)
            .eligible(vehicleDocsOk && healthOk)
            .build();
}
```

> Kết quả `DispatchEligibilityResult` chính là nguồn dữ liệu đổ vào cột `dispatch_assignments.pre_check_details` (JSONB) khi `DispatchAssignmentService.create()` được gọi — xem chi tiết ở Module 5.

**Update odometer** — không cho phép giảm số km (chặn nhập sai/gian lận), validate trong `update-partial`:

```java
if (updates.containsKey("currentOdometer")) {
    BigDecimal newOdo = new BigDecimal(updates.get("currentOdometer").toString());
    if (newOdo.compareTo(vehicle.getCurrentOdometer()) < 0) {
        throw new InvalidFieldException("Số km mới không được nhỏ hơn số km hiện tại");
    }
}
```

---

## 2.3. VehicleDocument (`vehicle_documents`)

**Nhóm cache**: KHÔNG

| #   | API                               | Method | URL                                                    | Quyền                       | Logic | Workspace |
| --- | --------------------------------- | ------ | ------------------------------------------------------ | --------------------------- | ----- | --------- |
| 1   | Tạo                               | POST   | `/api/v1/vehicle-document/create`                      | `FLEET_MANAGER`, `HR_LEGAL` | OR    | Có        |
| 2   | Cập nhật                          | PUT    | `/api/v1/vehicle-document/update/{id}`                 | `FLEET_MANAGER`, `HR_LEGAL` | OR    | Có        |
| 3   | Cập nhật 1 phần                   | PATCH  | `/api/v1/vehicle-document/update-partial/{id}`         | `FLEET_MANAGER`, `HR_LEGAL` | OR    | Có        |
| 4   | Lấy theo ID                       | GET    | `/api/v1/vehicle-document/get-by-id/{id}`              | `ALL`                       | OR    | Có        |
| 5   | Lấy theo xe                       | GET    | `/api/v1/vehicle-documents/get-by-vehicle/{vehicleId}` | `ALL`                       | OR    | Có        |
| 6   | Lọc/phân trang                    | POST   | `/api/v1/vehicle-documents/filter`                     | `FLEET_MANAGER`, `HR_LEGAL` | OR    | Có        |
| 7   | Đổi trạng thái                    | PATCH  | `/api/v1/vehicle-document/change-status/{id}`          | `FLEET_MANAGER`, `HR_LEGAL` | OR    | Có        |
| 8   | Xoá mềm                           | DELETE | `/api/v1/vehicle-document/soft-delete/{id}`            | `FLEET_MANAGER`             | —     | Có        |
| 9   | Xoá cứng                          | DELETE | `/api/v1/vehicle-document/hard-delete/{id}`            | `SYSTEM_ADMIN`              | —     | Không     |
| 10  | **Danh sách giấy tờ sắp hết hạn** | GET    | `/api/v1/vehicle-documents/get-expiring-soon`          | `FLEET_MANAGER`, `HR_LEGAL` | OR    | Có        |

### Chi tiết logic nghiệp vụ đặc thù

**`get-expiring-soon` (FR-VH-03 + FR-CP-03)** — ngưỡng "sắp hết hạn" đọc từ `SystemConfig` (không hardcode), mặc định 30 ngày theo tài liệu đặc tả:

```java
@Transactional(readOnly = true)
public List<VehicleDocument> getExpiringSoon(UUID orgId) {
    int thresholdDays = Integer.parseInt(
            systemConfigService.getByKeyCached("document.expiry.threshold_days").getConfigValue().asText("30"));

    LocalDate threshold = LocalDate.now().plusDays(thresholdDays);
    return repository.findByOrgIdAndExpiryDateBetweenAndStatusNot(
            orgId, LocalDate.now(), threshold, "expired");
}
```

**Scheduled job cập nhật `status`** — `expiry_date` không tự đổi `status` qua đêm nếu không có job quét định kỳ:

```java
@Scheduled(cron = "0 0 1 * * *") // 1h sáng mỗi ngày
public void refreshDocumentStatuses() {
    int thresholdDays = /* đọc từ SystemConfig như trên */;
    repository.bulkUpdateStatusExpiringSoon(LocalDate.now(), LocalDate.now().plusDays(thresholdDays));
    repository.bulkUpdateStatusExpired(LocalDate.now());
}
```

**Hard-delete giấy tờ xe — KHÔNG chỉ xoá DB record, còn phải xoá file vật lý trên MinIO** (liên kết qua `file_attachments.entity_id`):

```java
@Override
@Transactional
public void hardDelete(UUID id) {
    fileAttachmentService.deleteAllByEntity(EntityType.VEHICLE_DOCUMENT, id); // xoá MinIO + DB file_attachments
    repository.deleteById(id);
}
```

---

## 2.4. VehicleHealth (`vehicle_healths`)

**Nhóm cache**: KHÔNG (dữ liệu lịch sử chấm điểm, mỗi xe có nhiều bản ghi theo thời gian — query luôn cần bản mới nhất, không phù hợp cache theo ID đơn lẻ).

| #   | API                                     | Method | URL                                               | Quyền           | Logic | Workspace |
| --- | --------------------------------------- | ------ | ------------------------------------------------- | --------------- | ----- | --------- |
| 1   | Tạo (chấm điểm mới)                     | POST   | `/api/v1/vehicle-health/create`                   | `FLEET_MANAGER` | —     | Có        |
| 2   | Cập nhật                                | PUT    | `/api/v1/vehicle-health/update/{id}`              | `FLEET_MANAGER` | —     | Có        |
| 3   | Lấy theo ID                             | GET    | `/api/v1/vehicle-health/get-by-id/{id}`           | `ALL`           | OR    | Có        |
| 4   | **Lấy điểm sức khoẻ mới nhất của 1 xe** | GET    | `/api/v1/vehicle-health/get-latest/{vehicleId}`   | `ALL`           | OR    | Có        |
| 5   | Lấy lịch sử theo xe                     | GET    | `/api/v1/vehicle-healths/get-history/{vehicleId}` | `FLEET_MANAGER` | —     | Có        |
| 6   | Lọc/phân trang                          | POST   | `/api/v1/vehicle-healths/filter`                  | `FLEET_MANAGER` | —     | Có        |
| 7   | Xoá mềm                                 | DELETE | `/api/v1/vehicle-health/soft-delete/{id}`         | `FLEET_MANAGER` | —     | Có        |
| 8   | Xoá cứng                                | DELETE | `/api/v1/vehicle-health/hard-delete/{id}`         | `SYSTEM_ADMIN`  | —     | Không     |

### Chi tiết logic nghiệp vụ đặc thù

**Risk level enum** (theo thang điểm FR-HM-01 đã đối chiếu trước đó — bắt buộc thêm enum này vào code, hiện schema để tự do VARCHAR):

```java
public enum RiskLevel {
    LOW(80, 100), MEDIUM(60, 79), HIGH(40, 59), CRITICAL(0, 39);

    private final int min, max;
    RiskLevel(int min, int max) { this.min = min; this.max = max; }

    public static RiskLevel fromScore(int score) {
        for (RiskLevel level : values()) {
            if (score >= level.min && score <= level.max) return level;
        }
        throw new IllegalArgumentException("Điểm không hợp lệ: " + score);
    }
}
```

**`create` (chấm điểm)** — tự động set `risk_level` và `is_dispatch_blocked` dựa trên `health_score`, không để client tự truyền (tránh gian lận/sai sót nhập tay):

```java
@Override
@Transactional
public VehicleHealth create(VehicleHealth entity) {
    RiskLevel level = RiskLevel.fromScore(entity.getHealthScore());
    entity.setRiskLevel(level.name());
    entity.setIsDispatchBlocked(level == RiskLevel.CRITICAL); // chỉ CRITICAL mới tự động chặn (FR-HM-03)
    entity.setAssessedBy(SecurityContextHolder.getAuthInfo().getUserId());

    VehicleHealth saved = super.create(entity);

    if (entity.getIsDispatchBlocked()) {
        notificationService.notifyFleetManagers(entity.getVehicleId(), "Xe bị chặn điều phối do sức khoẻ nghiêm trọng");
    }
    return saved;
}
```

**`get-latest`** — query 1 bản ghi mới nhất, dùng `findFirstByVehicleIdOrderByCreatedAtDesc` (Spring Data derived query), KHÔNG cần custom `@Query`.

---

## 2.5. MaintenanceOrder (`maintenance_orders`)

**Nhóm cache**: KHÔNG

| #   | API             | Method | URL                                             | Quyền                          | Logic | Workspace |
| --- | --------------- | ------ | ----------------------------------------------- | ------------------------------ | ----- | --------- |
| 1   | Tạo             | POST   | `/api/v1/maintenance-order/create`              | `FLEET_MANAGER`                | —     | Có        |
| 2   | Cập nhật        | PUT    | `/api/v1/maintenance-order/update/{id}`         | `FLEET_MANAGER`                | —     | Có        |
| 3   | Cập nhật 1 phần | PATCH  | `/api/v1/maintenance-order/update-partial/{id}` | `FLEET_MANAGER`                | —     | Có        |
| 4   | Lấy theo ID     | GET    | `/api/v1/maintenance-order/get-by-id/{id}`      | `FLEET_MANAGER`, `OPS_MANAGER` | OR    | Có        |
| 5   | Lọc/phân trang  | POST   | `/api/v1/maintenance-orders/filter`             | `FLEET_MANAGER`, `OPS_MANAGER` | OR    | Có        |
| 6   | Đổi trạng thái  | PATCH  | `/api/v1/maintenance-order/change-status/{id}`  | `FLEET_MANAGER`                | —     | Có        |
| 7   | Xoá mềm         | DELETE | `/api/v1/maintenance-order/soft-delete/{id}`    | `FLEET_MANAGER`                | —     | Có        |
| 8   | Xoá cứng        | DELETE | `/api/v1/maintenance-order/hard-delete/{id}`    | `SYSTEM_ADMIN`                 | —     | Không     |

### Chi tiết logic nghiệp vụ đặc thù

**`changeStatus` → `completed`** — khi hoàn tất bảo trì, tự động tính lại `total_cost` từ tổng `maintenance_items`, và nên kích hoạt yêu cầu chấm lại `VehicleHealth` (không tự động hoá điểm số, nhưng nhắc nhở qua notification):

```java
@Override
@Transactional
public MaintenanceOrder changeStatus(UUID id, String status) {
    MaintenanceOrder order = super.changeStatus(id, status);

    if ("completed".equals(status)) {
        BigDecimal total = maintenanceItemRepository.sumTotalCostByOrderId(id);
        order.setTotalCost(total);
        order.setCompletedDate(LocalDate.now());
        repository.save(order);

        notificationService.notifyFleetManagers(order.getVehicleId(),
                "Bảo trì hoàn tất — cần đánh giá lại sức khoẻ xe trước khi điều phối");
    }
    return order;
}
```

---

## 2.6. MaintenanceItem (`maintenance_items`)

**Nhóm cache**: KHÔNG

| #   | API            | Method | URL                                                | Quyền                          | Logic | Workspace                                         |
| --- | -------------- | ------ | -------------------------------------------------- | ------------------------------ | ----- | ------------------------------------------------- |
| 1   | Tạo            | POST   | `/api/v1/maintenance-item/create`                  | `FLEET_MANAGER`                | —     | Có (suy ra từ `maintenance_order.vehicle.org_id`) |
| 2   | Cập nhật       | PUT    | `/api/v1/maintenance-item/update/{id}`             | `FLEET_MANAGER`                | —     | Có                                                |
| 3   | Lấy theo ID    | GET    | `/api/v1/maintenance-item/get-by-id/{id}`          | `FLEET_MANAGER`, `OPS_MANAGER` | OR    | Có                                                |
| 4   | Lấy theo order | GET    | `/api/v1/maintenance-items/get-by-order/{orderId}` | `FLEET_MANAGER`, `OPS_MANAGER` | OR    | Có                                                |
| 5   | Lọc/phân trang | POST   | `/api/v1/maintenance-items/filter`                 | `FLEET_MANAGER`                | —     | Có                                                |
| 6   | Xoá mềm        | DELETE | `/api/v1/maintenance-item/soft-delete/{id}`        | `FLEET_MANAGER`                | —     | Có                                                |
| 7   | Xoá cứng       | DELETE | `/api/v1/maintenance-item/hard-delete/{id}`        | `SYSTEM_ADMIN`                 | —     | Không                                             |

### Chi tiết logic nghiệp vụ đặc thù

**`create`** — `total_cost` luôn tính tự động (`unit_cost * quantity`), KHÔNG nhận từ client để tránh sai lệch:

```java
@Override
@Transactional
public MaintenanceItem create(MaintenanceItem entity) {
    entity.setTotalCost(entity.getUnitCost().multiply(BigDecimal.valueOf(entity.getQuantity())));
    return super.create(entity);
}
```

> **Lưu ý workspace cho `Vehicle`, `VehicleHealth`, `MaintenanceOrder`, `MaintenanceItem`**: các bảng này không có cột `org_id` trực tiếp trong schema (trừ `vehicles.org_id`) — `org_id` của `VehicleHealth`/`MaintenanceOrder`/`MaintenanceItem` phải **suy ra qua JOIN** tới `vehicles.org_id`. Khi triển khai `inWorkspace` check ở tầng Service cho các entity này, KHÔNG thể chỉ dựa vào field trực tiếp trên entity như `Organization`/`Vehicle` — cần custom logic kiểm tra `entity.getVehicle().getOrgId()` thay vì `entity.getOrgId()`. Đây là điểm khác biệt quan trọng so với pattern mặc định, cần lưu ý khi code `AuthorizationAspect` hoặc thêm filter ở tầng Specification.

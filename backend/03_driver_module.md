# Module 3 — Tài xế

Entity: `Driver`, `DriverDocument`

---

## 3.1. Driver (`drivers`)

**Nhóm cache**: KHÔNG (`is_dispatch_eligible`, `status` thay đổi thường xuyên).

| #   | API                                            | Method | URL                                              | Quyền                       | Logic | Workspace                           |
| --- | ---------------------------------------------- | ------ | ------------------------------------------------ | --------------------------- | ----- | ----------------------------------- |
| 1   | Tạo                                            | POST   | `/api/v1/driver/create`                          | `HR_LEGAL`, `FLEET_MANAGER` | OR    | Có                                  |
| 2   | Cập nhật toàn bộ                               | PUT    | `/api/v1/driver/update/{id}`                     | `HR_LEGAL`, `FLEET_MANAGER` | OR    | Có                                  |
| 3   | Cập nhật 1 phần                                | PATCH  | `/api/v1/driver/update-partial/{id}`             | `HR_LEGAL`, `FLEET_MANAGER` | OR    | Có                                  |
| 4   | Lấy theo ID                                    | GET    | `/api/v1/driver/get-by-id/{id}`                  | `ALL`                       | OR    | Có                                  |
| 5   | Lọc/phân trang                                 | POST   | `/api/v1/drivers/filter`                         | `ALL`                       | OR    | Có                                  |
| 6   | Đổi trạng thái                                 | PATCH  | `/api/v1/driver/change-status/{id}`              | `HR_LEGAL`                  | —     | Có                                  |
| 7   | Xoá mềm                                        | DELETE | `/api/v1/driver/soft-delete/{id}`                | `HR_LEGAL`                  | —     | Có                                  |
| 8   | Xoá cứng                                       | DELETE | `/api/v1/driver/hard-delete/{id}`                | `SYSTEM_ADMIN`              | —     | Không                               |
| 9   | **Lấy tài xế sẵn sàng điều phối**              | GET    | `/api/v1/drivers/get-available-for-dispatch`     | `DISPATCHER`                | —     | Có                                  |
| 10  | **Kiểm tra điều kiện trước điều xe**           | GET    | `/api/v1/driver/{id}/check-dispatch-eligibility` | `DISPATCHER`                | —     | Có                                  |
| 11  | Lấy thông tin tài xế hiện tại (cho mobile app) | GET    | `/api/v1/driver/me`                              | `DRIVER`                    | —     | Có (tự suy ra org của chính tài xế) |

### Chi tiết logic nghiệp vụ đặc thù

**`get-available-for-dispatch` (FR-DP-02)** — song song với Vehicle, lọc tài xế `active` + `is_dispatch_eligible = true` + không đang gắn `dispatch_assignments` active:

```java
@Query("""
    SELECT d FROM Driver d
    WHERE d.orgId = :orgId
      AND d.status = 'active'
      AND d.isDispatchEligible = true
      AND NOT EXISTS (
          SELECT 1 FROM DispatchAssignment da
          WHERE da.driverId = d.id AND da.status = 'active'
      )
    """)
List<Driver> findAvailableForDispatch(@Param("orgId") UUID orgId);
```

**`check-dispatch-eligibility` (FR-DP-04 + FR-CP-04)** — kiểm tra `driver_documents` không có giấy tờ hết hạn/thiếu, tương tự logic Vehicle ở Module 2:

```java
public DriverEligibilityResult checkDispatchEligibility(UUID driverId) {
    Driver driver = getOne(driverId).orElseThrow(() -> new NotFoundException("Tài xế không tồn tại"));

    if (!driver.getIsDispatchEligible()) {
        return DriverEligibilityResult.builder().eligible(false).reason("Tài xế bị đánh dấu không đủ điều kiện").build();
    }

    boolean docsOk = driverDocumentService.hasNoExpiredOrMissingDocs(driverId);
    return DriverEligibilityResult.builder()
            .eligible(docsOk)
            .reason(docsOk ? null : "Có giấy tờ tài xế hết hạn hoặc thiếu")
            .build();
}
```

**`/driver/me`** — đặc thù cho app di động (FR-MA-01), lấy `driverId` qua `users.id == authInfo.getUserId()` rồi tra `drivers.user_id`:

```java
@GetMapping("/me")
@RequireAuth(roles = {RoleType.DRIVER})
public ResponseData<Driver> getMe() {
    UUID userId = SecurityContextHolder.getAuthInfo().getUserId();
    Driver driver = driverRepository.findByUserId(userId)
            .orElseThrow(() -> new NotFoundException("Tài khoản chưa liên kết hồ sơ tài xế"));
    return ResponseData.<Driver>builder().status(200).data(driver).message("Thành công").build();
}
```

---

## 3.2. DriverDocument (`driver_documents`)

**Nhóm cache**: KHÔNG

| #   | API                               | Method | URL                                                 | Quyền          | Logic | Workspace |
| --- | --------------------------------- | ------ | --------------------------------------------------- | -------------- | ----- | --------- |
| 1   | Tạo                               | POST   | `/api/v1/driver-document/create`                    | `HR_LEGAL`     | —     | Có        |
| 2   | Cập nhật                          | PUT    | `/api/v1/driver-document/update/{id}`               | `HR_LEGAL`     | —     | Có        |
| 3   | Cập nhật 1 phần                   | PATCH  | `/api/v1/driver-document/update-partial/{id}`       | `HR_LEGAL`     | —     | Có        |
| 4   | Lấy theo ID                       | GET    | `/api/v1/driver-document/get-by-id/{id}`            | `ALL`          | OR    | Có        |
| 5   | Lấy theo tài xế                   | GET    | `/api/v1/driver-documents/get-by-driver/{driverId}` | `ALL`          | OR    | Có        |
| 6   | Lọc/phân trang                    | POST   | `/api/v1/driver-documents/filter`                   | `HR_LEGAL`     | —     | Có        |
| 7   | Đổi trạng thái                    | PATCH  | `/api/v1/driver-document/change-status/{id}`        | `HR_LEGAL`     | —     | Có        |
| 8   | Xoá mềm                           | DELETE | `/api/v1/driver-document/soft-delete/{id}`          | `HR_LEGAL`     | —     | Có        |
| 9   | Xoá cứng                          | DELETE | `/api/v1/driver-document/hard-delete/{id}`          | `SYSTEM_ADMIN` | —     | Không     |
| 10  | **Danh sách giấy tờ sắp hết hạn** | GET    | `/api/v1/driver-documents/get-expiring-soon`        | `HR_LEGAL`     | —     | Có        |

### Chi tiết logic nghiệp vụ đặc thù

Hoàn toàn song song với `VehicleDocument` ở Module 2 — cùng cơ chế đọc ngưỡng từ `SystemConfig`, cùng scheduled job cập nhật `status`, cùng hard-delete kèm dọn `file_attachments`. Khuyến nghị tạo 1 interface chung `IExpirableDocument` hoặc abstract service `ExpirableDocumentServiceBase<T, ID>` kế thừa thêm từ `BaseServiceImpl` để dùng chung logic `getExpiringSoon()`, tránh trùng lặp code giữa `VehicleDocumentService` và `DriverDocumentService`:

```java
public abstract class ExpirableDocumentServiceBase<T, ID> extends BaseServiceImpl<T, ID> {

    protected final SystemConfigService systemConfigService;

    protected abstract String getThresholdConfigKey(); // "document.expiry.threshold_days" dùng chung hoặc tách riêng theo loại
    protected abstract List<T> findExpiringSoon(UUID orgId, LocalDate from, LocalDate to);

    public List<T> getExpiringSoon(UUID orgId) {
        int thresholdDays = Integer.parseInt(
                systemConfigService.getByKeyCached(getThresholdConfigKey()).getConfigValue().asText("30"));
        return findExpiringSoon(orgId, LocalDate.now(), LocalDate.now().plusDays(thresholdDays));
    }
}
```

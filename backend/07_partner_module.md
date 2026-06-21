# Module 7 — Đối tác vận tải

Entity: `Partner`, `PartnerVehicle`, `PartnerDriver`, `PartnerPerformance`

---

## 7.1. Partner (`partners`)

**Nhóm cache**: KHÔNG (theo phân loại đã chốt — dù tương đối tĩnh, nhưng `rating`/`on_time_rate` cập nhật định kỳ qua `PartnerPerformance`, giữ nhất quán không cache).

| # | API | Method | URL | Quyền | Logic | Workspace |
|---|---|---|---|---|---|---|
| 1 | Tạo | POST | `/api/v1/partner/create` | `PARTNER_MANAGER` | — | Có |
| 2 | Cập nhật toàn bộ | PUT | `/api/v1/partner/update/{id}` | `PARTNER_MANAGER` | — | Có |
| 3 | Cập nhật 1 phần | PATCH | `/api/v1/partner/update-partial/{id}` | `PARTNER_MANAGER` | — | Có |
| 4 | Lấy theo ID | GET | `/api/v1/partner/get-by-id/{id}` | `ALL` | OR | Có |
| 5 | Lấy toàn bộ | GET | `/api/v1/partners/get-all` | `PARTNER_MANAGER`, `DISPATCHER` | OR | Có |
| 6 | Lọc/phân trang | POST | `/api/v1/partners/filter` | `ALL` | OR | Có |
| 7 | Đổi trạng thái | PATCH | `/api/v1/partner/change-status/{id}` | `PARTNER_MANAGER` | — | Có |
| 8 | Xoá mềm | DELETE | `/api/v1/partner/soft-delete/{id}` | `PARTNER_MANAGER` | — | Có |
| 9 | Xoá cứng | DELETE | `/api/v1/partner/hard-delete/{id}` | `SYSTEM_ADMIN` | — | Không |
| 10 | **So sánh phương án đối tác** | POST | `/api/v1/partners/compare` | `PARTNER_MANAGER`, `DISPATCHER` | OR | Có |

### Chi tiết logic nghiệp vụ đặc thù

**`compare` (FR-PT-02)** — nhận tiêu chí (loại xe cần, tuyến đường, ngày), trả về danh sách đối tác kèm xe sẵn sàng + xếp hạng theo `rating`/`on_time_rate`:

```java
@PostMapping("/compare")
@RequireAuth(roles = {RoleType.PARTNER_MANAGER, RoleType.DISPATCHER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
public ResponseData<List<PartnerComparisonDto>> compare(@RequestBody PartnerCompareRequest request) {
    List<PartnerComparisonDto> results = partnerService.compareForCriteria(request);
    return ResponseData.<List<PartnerComparisonDto>>builder().status(200).data(results).message("Thành công").build();
}
```

```java
@Transactional(readOnly = true)
public List<PartnerComparisonDto> compareForCriteria(PartnerCompareRequest request) {
    List<Partner> eligiblePartners = repository.findByOrgIdAndStatus(request.getOrgId(), "active");

    return eligiblePartners.stream()
            .map(p -> {
                List<PartnerVehicle> available = partnerVehicleRepository
                        .findByPartnerIdAndVehicleTypeIdAndStatus(p.getId(), request.getVehicleTypeId(), "available");
                return PartnerComparisonDto.builder()
                        .partner(p)
                        .availableVehicleCount(available.size())
                        .estimatedCost(estimateCost(p, request)) // logic ước tính chi phí riêng theo định mức
                        .build();
            })
            .filter(dto -> dto.getAvailableVehicleCount() > 0)
            .sorted(Comparator.comparing((PartnerComparisonDto d) -> d.getPartner().getRating()).reversed()
                    .thenComparing(d -> d.getPartner().getOnTimeRate(), Comparator.reverseOrder()))
            .collect(Collectors.toList());
}
```

---

## 7.2. PartnerVehicle (`partner_vehicles`)

**Nhóm cache**: KHÔNG

| # | API | Method | URL | Quyền | Logic | Workspace |
|---|---|---|---|---|---|---|
| 1 | Tạo | POST | `/api/v1/partner-vehicle/create` | `PARTNER_MANAGER` | — | Có |
| 2 | Cập nhật | PUT | `/api/v1/partner-vehicle/update/{id}` | `PARTNER_MANAGER` | — | Có |
| 3 | Lấy theo ID | GET | `/api/v1/partner-vehicle/get-by-id/{id}` | `ALL` | OR | Có |
| 4 | Lấy theo đối tác | GET | `/api/v1/partner-vehicles/get-by-partner/{partnerId}` | `ALL` | OR | Có |
| 5 | Lọc/phân trang | POST | `/api/v1/partner-vehicles/filter` | `PARTNER_MANAGER`, `DISPATCHER` | OR | Có |
| 6 | Đổi trạng thái | PATCH | `/api/v1/partner-vehicle/change-status/{id}` | `PARTNER_MANAGER` | — | Có |
| 7 | Xoá mềm | DELETE | `/api/v1/partner-vehicle/soft-delete/{id}` | `PARTNER_MANAGER` | — | Có |
| 8 | Xoá cứng | DELETE | `/api/v1/partner-vehicle/hard-delete/{id}` | `SYSTEM_ADMIN` | — | Không |

### Chi tiết logic nghiệp vụ đặc thù

**`changeStatus`** — khi 1 `dispatch_assignment` (giả định mở rộng sau này hỗ trợ gán xe đối tác, hiện schema `dispatch_assignments.vehicle_id` chỉ FK tới `vehicles` nội bộ, CHƯA hỗ trợ `partner_vehicles` trực tiếp — đây là **khoảng trống thiết kế cần lưu ý**, không nằm trong phạm vi sửa ở tài liệu này nhưng cần ghi nhận để team xác nhận hướng mở rộng) thì đổi `available` → `on_trip`.

> **Ghi chú khoảng trống thiết kế:** Theo schema gốc, `dispatch_assignments.vehicle_id REFERENCES vehicles(id)` — chỉ tham chiếu được xe nội bộ, không tham chiếu được `partner_vehicles.id`. Nếu nghiệp vụ thực tế cần điều phối xe đối tác qua cùng luồng `DispatchAssignment`, cần bổ sung cột nullable `partner_vehicle_id`/`partner_driver_id` hoặc tách hẳn 1 bảng `partner_dispatch_assignments` riêng. Đây là quyết định kiến trúc cần làm rõ với team trước khi triển khai Module 5 đầy đủ cho luồng đối tác.

---

## 7.3. PartnerDriver (`partner_drivers`)

**Nhóm cache**: KHÔNG

| # | API | Method | URL | Quyền | Logic | Workspace |
|---|---|---|---|---|---|---|
| 1 | Tạo | POST | `/api/v1/partner-driver/create` | `PARTNER_MANAGER` | — | Có |
| 2 | Cập nhật | PUT | `/api/v1/partner-driver/update/{id}` | `PARTNER_MANAGER` | — | Có |
| 3 | Lấy theo ID | GET | `/api/v1/partner-driver/get-by-id/{id}` | `ALL` | OR | Có |
| 4 | Lấy theo đối tác | GET | `/api/v1/partner-drivers/get-by-partner/{partnerId}` | `ALL` | OR | Có |
| 5 | Lọc/phân trang | POST | `/api/v1/partner-drivers/filter` | `PARTNER_MANAGER` | — | Có |
| 6 | Đổi trạng thái | PATCH | `/api/v1/partner-driver/change-status/{id}` | `PARTNER_MANAGER` | — | Có |
| 7 | Xoá mềm | DELETE | `/api/v1/partner-driver/soft-delete/{id}` | `PARTNER_MANAGER` | — | Có |
| 8 | Xoá cứng | DELETE | `/api/v1/partner-driver/hard-delete/{id}` | `SYSTEM_ADMIN` | — | Không |

Không có logic đặc thù khác biệt — CRUD chuẩn theo `BaseServiceImpl`, validate `license_number` không trùng trong phạm vi cùng `partner_id` khi tạo mới (tránh trùng tài xế nhập 2 lần).

---

## 7.4. PartnerPerformance (`partner_performances`)

**Nhóm cache**: KHÔNG (dữ liệu tổng hợp theo tháng, nhưng vẫn có thể được tính lại/điều chỉnh trong tháng).

| # | API | Method | URL | Quyền | Logic | Workspace |
|---|---|---|---|---|---|---|
| 1 | Tạo (thường chạy tự động cuối tháng, nhưng vẫn cho phép tạo thủ công) | POST | `/api/v1/partner-performance/create` | `PARTNER_MANAGER` | — | Có |
| 2 | Cập nhật | PUT | `/api/v1/partner-performance/update/{id}` | `PARTNER_MANAGER` | — | Có |
| 3 | Lấy theo ID | GET | `/api/v1/partner-performance/get-by-id/{id}` | `ALL` | OR | Có |
| 4 | Lấy theo đối tác | GET | `/api/v1/partner-performances/get-by-partner/{partnerId}` | `PARTNER_MANAGER`, `OPS_MANAGER` | OR | Có |
| 5 | Lọc/phân trang | POST | `/api/v1/partner-performances/filter` | `PARTNER_MANAGER`, `OPS_MANAGER` | OR | Có |
| 6 | Xoá mềm | DELETE | `/api/v1/partner-performance/soft-delete/{id}` | `PARTNER_MANAGER` | — | Có |
| 7 | Xoá cứng | DELETE | `/api/v1/partner-performance/hard-delete/{id}` | `SYSTEM_ADMIN` | — | Không |
| 8 | **Tính toán lại hiệu suất tháng** | POST | `/api/v1/partner-performance/recalculate` | `PARTNER_MANAGER` | — | Có |

### Chi tiết logic nghiệp vụ đặc thù

**`create`** — validate `UNIQUE (partner_id, period_month)` ở tầng service trước (tránh lỗi DB constraint khó đọc cho người dùng).

**`recalculate` (FR-PT-04)** — chạy bằng `@Scheduled` đầu tháng (tự động) HOẶC gọi thủ công qua API này (cho phép `PARTNER_MANAGER` re-sync nếu phát hiện sai lệch dữ liệu):

```java
@Scheduled(cron = "0 0 2 1 * *") // 2h sáng ngày 1 mỗi tháng — tính cho tháng vừa kết thúc
public void monthlyRecalculate() {
    String previousMonth = YearMonth.now().minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM"));
    partnerRepository.findAll().forEach(p -> recalculateForPartnerAndMonth(p.getId(), previousMonth));
}

@Transactional
public PartnerPerformance recalculateForPartnerAndMonth(UUID partnerId, String periodMonth) {
    // Giả định mở rộng sau này: trips gán cho partner thông qua bảng mở rộng đã ghi chú ở mục 7.2
    int totalTrips = tripRepository.countByPartnerIdAndPeriod(partnerId, periodMonth);
    int onTimeTrips = tripRepository.countOnTimeByPartnerIdAndPeriod(partnerId, periodMonth);
    BigDecimal avgCost = tripRepository.avgCostByPartnerIdAndPeriod(partnerId, periodMonth);

    PartnerPerformance perf = repository.findByPartnerIdAndPeriodMonth(partnerId, periodMonth)
            .orElse(PartnerPerformance.builder().partnerId(partnerId).periodMonth(periodMonth).build());

    perf.setTotalTrips(totalTrips);
    perf.setOnTimeTrips(onTimeTrips);
    perf.setAvgCost(avgCost);
    perf.setStatus("final");

    PartnerPerformance saved = repository.save(perf);

    // Đồng bộ ngược lại on_time_rate trên bảng partners (dùng cho /compare ở mục 7.1)
    BigDecimal onTimeRate = totalTrips > 0
            ? BigDecimal.valueOf(onTimeTrips).divide(BigDecimal.valueOf(totalTrips), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
            : BigDecimal.ZERO;
    partnerRepository.updateOnTimeRate(partnerId, onTimeRate);

    return saved;
}
```
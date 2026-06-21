# Module 5 — Điều xe & Chuyến

Entity: `DispatchRequest`, `DispatchAssignment`, `Trip`, `TripStop`, `TripIncident`, `FuelLog`

> Đây là module nghiệp vụ lõi của toàn hệ thống (FR-RQ, FR-DP, FR-TR). Tất cả entity thuộc nhóm **KHÔNG cache**.

---

## 5.1. DispatchRequest (`dispatch_requests`)

| # | API | Method | URL | Quyền | Logic | Workspace |
|---|---|---|---|---|---|---|
| 1 | Tạo | POST | `/api/v1/dispatch-request/create` | `DISPATCHER` | — | Có |
| 2 | Cập nhật toàn bộ | PUT | `/api/v1/dispatch-request/update/{id}` | `DISPATCHER` | — | Có |
| 3 | Cập nhật 1 phần | PATCH | `/api/v1/dispatch-request/update-partial/{id}` | `DISPATCHER` | — | Có |
| 4 | Lấy theo ID | GET | `/api/v1/dispatch-request/get-by-id/{id}` | `DISPATCHER`, `OPS_MANAGER` | OR | Có |
| 5 | Lọc/phân trang | POST | `/api/v1/dispatch-requests/filter` | `DISPATCHER`, `OPS_MANAGER` | OR | Có |
| 6 | Đổi trạng thái | PATCH | `/api/v1/dispatch-request/change-status/{id}` | `DISPATCHER` | — | Có |
| 7 | Xoá mềm | DELETE | `/api/v1/dispatch-request/soft-delete/{id}` | `DISPATCHER` | — | Có |
| 8 | Xoá cứng | DELETE | `/api/v1/dispatch-request/hard-delete/{id}` | `SYSTEM_ADMIN` | — | Không |
| 9 | **Hàng chờ theo ưu tiên** | GET | `/api/v1/dispatch-requests/get-queue` | `DISPATCHER` | — | Có |
| 10 | **Cập nhật hàng loạt** | PATCH | `/api/v1/dispatch-requests/bulk-update` | `DISPATCHER` | — | Có |
| 11 | **Import Excel** | POST | `/api/v1/dispatch-requests/import-excel` | `DISPATCHER` | — | Có |
| 12 | **Export Excel** | POST | `/api/v1/dispatch-requests/export-excel` | `DISPATCHER`, `OPS_MANAGER` | OR | Có |

### Chi tiết logic nghiệp vụ đặc thù

**Tạo (`create`)** — `req_number` sinh tự động (không nhận từ client), format gợi ý `DR-{orgCode}-{yyyyMMdd}-{sequence}`:

```java
@Override
@Transactional
public DispatchRequest create(DispatchRequest entity) {
    entity.setReqNumber(generateReqNumber(entity.getOrgId()));
    entity.setStatus("new");
    entity.setCreatedBy(SecurityContextHolder.getAuthInfo().getUserId());
    DispatchRequest saved = super.create(entity);

    auditLogService.record("CREATE", EntityType.DISPATCH_REQUEST, saved.getId(), null, saved);
    return saved;
}

private String generateReqNumber(UUID orgId) {
    Organization org = organizationService.getByIdCached(orgId);
    String datePart = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
    long seq = repository.countByOrgIdAndCreatedAtBetween(orgId, LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay()) + 1;
    return String.format("DR-%s-%s-%04d", org.getCode(), datePart, seq);
}
```

**`get-queue` (FR-DP-01)** — sắp xếp theo `priority` (high → low) rồi `requested_pickup_at` (sớm nhất trước), chỉ lấy trạng thái `new`/`processing`:

```java
@Query("""
    SELECT r FROM DispatchRequest r
    WHERE r.orgId = :orgId AND r.status IN ('new', 'processing')
    ORDER BY
        CASE r.priority WHEN 'high' THEN 0 WHEN 'medium' THEN 1 ELSE 2 END,
        r.requestedPickupAt ASC NULLS LAST
    """)
List<DispatchRequest> findQueue(@Param("orgId") UUID orgId);
```

**`bulk-update` (FR-RQ-04)** — nhận danh sách `{id, fields}`, dùng `updateFromMap` có sẵn trong `BaseServiceImpl` lặp qua từng phần tử trong 1 transaction:

```java
@Transactional
public List<DispatchRequest> bulkUpdate(List<BulkUpdateItem> items) {
    return items.stream()
            .map(item -> updateFromMap(item.getId(), item.getFields()))
            .collect(Collectors.toList());
}
```

**`import-excel` / `export-excel` (FR-RQ-05)** — dùng Apache POI, xử lý theo batch (không load toàn bộ file vào RAM nếu file lớn — dùng `SXSSFWorkbook` cho export, streaming row cho import). Trả về `ImportResult{successCount, failedRows: List<{rowIndex, reason}>}` thay vì fail toàn bộ batch khi có 1 dòng lỗi.

---

## 5.2. DispatchAssignment (`dispatch_assignments`)

| # | API | Method | URL | Quyền | Logic | Workspace |
|---|---|---|---|---|---|---|
| 1 | **Gán xe-tài xế** (tạo) | POST | `/api/v1/dispatch-assignment/create` | `DISPATCHER` | — | Có |
| 2 | Cập nhật | PUT | `/api/v1/dispatch-assignment/update/{id}` | `DISPATCHER` | — | Có |
| 3 | Lấy theo ID | GET | `/api/v1/dispatch-assignment/get-by-id/{id}` | `DISPATCHER`, `OPS_MANAGER` | OR | Có |
| 4 | Lọc/phân trang | POST | `/api/v1/dispatch-assignments/filter` | `DISPATCHER`, `OPS_MANAGER` | OR | Có |
| 5 | Đổi trạng thái (huỷ gán) | PATCH | `/api/v1/dispatch-assignment/change-status/{id}` | `DISPATCHER` | — | Có |
| 6 | Xoá mềm | DELETE | `/api/v1/dispatch-assignment/soft-delete/{id}` | `DISPATCHER` | — | Có |
| 7 | Xoá cứng | DELETE | `/api/v1/dispatch-assignment/hard-delete/{id}` | `SYSTEM_ADMIN` | — | Không |
| 8 | **Override khi bị chặn** | POST | `/api/v1/dispatch-assignment/{id}/override` | `DISPATCHER` (AND `OPS_MANAGER` — xem ghi chú) | AND | Có |
| 9 | **Gợi ý xe/tài xế phù hợp** | GET | `/api/v1/dispatch-assignments/suggest` | `DISPATCHER` | — | Có |

### Chi tiết logic nghiệp vụ đặc thù — ĐÂY LÀ API QUAN TRỌNG NHẤT HỆ THỐNG (FR-DP-03 đến FR-DP-06)

**`create` (gán xe-tài xế)** — thực thi tuần tự cổng chặn:

```java
@Override
@Transactional
public DispatchAssignment create(DispatchAssignment entity) {
    // 1. Check điều kiện vehicle + driver (gọi 2 API đã định nghĩa ở Module 2, 3)
    DispatchEligibilityResult vehicleCheck = vehicleService.checkDispatchEligibility(entity.getVehicleId());
    DriverEligibilityResult driverCheck = driverService.checkDispatchEligibility(entity.getDriverId());

    // 2. Check tải trọng: cargo_weight_ton của request <= load_capacity_ton của xe
    DispatchRequest request = dispatchRequestService.getOne(entity.getDispatchRequestId())
            .orElseThrow(() -> new NotFoundException("Yêu cầu điều xe không tồn tại"));
    Vehicle vehicle = vehicleService.getOne(entity.getVehicleId()).orElseThrow();
    boolean loadOk = request.getCargoWeightTon().compareTo(vehicle.getLoadCapacityTon()) <= 0;

    boolean allPassed = vehicleCheck.isEligible() && driverCheck.isEligible() && loadOk;

    Map<String, Object> preCheckDetails = Map.of(
            "load_ok", loadOk,
            "vehicle_docs_ok", vehicleCheck.isVehicleDocsOk(),
            "health_ok", vehicleCheck.isHealthOk(),
            "driver_docs_ok", driverCheck.isEligible()
    );

    entity.setPreCheckPassed(allPassed);
    entity.setPreCheckDetails(objectMapper.valueToTree(preCheckDetails));
    entity.setAssignedBy(SecurityContextHolder.getAuthInfo().getUserId());

    // 3. CHẶN nếu không pass và KHÔNG có override_reason kèm theo
    if (!allPassed && (entity.getOverrideReason() == null || entity.getOverrideReason().isBlank())) {
        throw new DispatchBlockedException("Không đủ điều kiện điều phối", preCheckDetails);
    }

    // 4. Nếu có override, ghi nhận người duyệt + audit log riêng (KHÔNG dùng audit log chung,
    //    vì đây là sự kiện nghiệp vụ đặc biệt cần truy vết rõ ràng theo FR-DP-06)
    if (!allPassed) {
        entity.setOverrideBy(SecurityContextHolder.getAuthInfo().getUserId());
        auditLogService.record("OVERRIDE", EntityType.DISPATCH_ASSIGNMENT, null, null, entity);
    }

    DispatchAssignment saved = super.create(entity);

    // 5. Đổi trạng thái dispatch_request -> assigned, tạo Trip tương ứng
    dispatchRequestService.changeStatus(request.getId(), "assigned");
    tripService.createFromAssignment(saved);

    return saved;
}
```

**Endpoint `/override`** — tách riêng khỏi `create` thông thường để **UI có thể hiển thị rõ ràng 2 bước**: bước 1 gọi `create` bình thường, nếu nhận lỗi `DispatchBlockedException` (HTTP 409 kèm `preCheckDetails`), UI hiển thị popup cảnh báo + ô nhập lý do, rồi gọi `/override` với `override_reason` bắt buộc:

```java
@PostMapping("/{id}/override")
@RequireAuth(roles = {RoleType.DISPATCHER}, inWorkspace = true)
public ResponseData<DispatchAssignment> override(
        @PathVariable UUID id,
        @RequestBody @Valid OverrideRequest body) {

    if (body.getOverrideReason() == null || body.getOverrideReason().isBlank()) {
        throw new InvalidFieldException("Lý do override là bắt buộc");
    }

    DispatchAssignment assignment = dispatchAssignmentService.applyOverride(id, body.getOverrideReason());
    return ResponseData.<DispatchAssignment>builder().status(200).data(assignment).message("Đã override thành công").build();
}
```

> **Lưu ý về phân quyền override (đã nêu ở các turn trước, vẫn CHƯA chốt được vì tài liệu không quy định rõ):** hiện đặt `roles = {DISPATCHER}` đơn giản, NHƯNG cân nhắc nâng lên `rolesLogic = AND` với `{DISPATCHER, OPS_MANAGER}` nếu nghiệp vụ thực tế yêu cầu **2 người duyệt** (dispatcher đề xuất, ops_manager phê duyệt) — đây là quyết định nghiệp vụ cần xác nhận với team trước khi code chính thức, KHÔNG nên tự quyết định khi viết code.

**`suggest` (FR-DP-07)** — thuật toán gợi ý đơn giản (rule-based, KHÔNG phải ML) cho giai đoạn đầu: lọc xe/tài xế đủ điều kiện (`get-available-for-dispatch`), rồi sắp xếp theo khoảng cách (PostGIS `ST_Distance`) tới điểm `origin` của request:

```sql
SELECT v.* FROM vehicles v
WHERE v.id IN (:eligibleVehicleIds)
ORDER BY ST_Distance(
    ST_MakePoint(v.last_known_lng, v.last_known_lat)::geography,
    ST_MakePoint(:originLng, :originLat)::geography
) ASC
LIMIT 5
```

> Cần bổ sung cột toạ độ vị trí cuối cùng của xe (`last_known_lat`/`last_known_lng`) vào `vehicles`, hoặc JOIN trực tiếp với key Redis `vehicle:position:{vehicle_id}` đã thiết kế ở phần GPS — query nên ưu tiên đọc từ Redis trước vì nhanh hơn nhiều so với JOIN PostGIS trên bảng `vehicles`.

---

## 5.3. Trip (`trips`)

| # | API | Method | URL | Quyền | Logic | Workspace |
|---|---|---|---|---|---|---|
| 1 | Tạo (nội bộ, qua `DispatchAssignment.create`) | — | — | — | — | — |
| 2 | Cập nhật | PUT | `/api/v1/trip/update/{id}` | `DISPATCHER` | — | Có |
| 3 | Lấy theo ID | GET | `/api/v1/trip/get-by-id/{id}` | `DISPATCHER`, `OPS_MANAGER`, `DRIVER` (chỉ chuyến của mình) | OR | Có |
| 4 | Lọc/phân trang | POST | `/api/v1/trips/filter` | `DISPATCHER`, `OPS_MANAGER` | OR | Có |
| 5 | Đổi trạng thái | PATCH | `/api/v1/trip/change-status/{id}` | `DISPATCHER`, `DRIVER` (chỉ chuyến của mình) | OR | Có |
| 6 | Xoá mềm | DELETE | `/api/v1/trip/soft-delete/{id}` | `OPS_MANAGER` | — | Có |
| 7 | Xoá cứng | DELETE | `/api/v1/trip/hard-delete/{id}` | `SYSTEM_ADMIN` | — | Không |
| 8 | **Chuyến của tôi (driver app)** | GET | `/api/v1/trips/get-my-trips` | `DRIVER` | — | Có |
| 9 | **Phát lại hành trình** | GET | `/api/v1/trip/{id}/replay` | `DISPATCHER`, `OPS_MANAGER` | OR | Có |

### Chi tiết logic nghiệp vụ đặc thù

**Không có endpoint `/create` công khai** — `Trip` luôn được sinh tự động khi `DispatchAssignment.create()` thành công (xem mục 5.2):

```java
@Transactional
public Trip createFromAssignment(DispatchAssignment assignment) {
    Trip trip = Trip.builder()
            .dispatchAssignmentId(assignment.getId())
            .tripNumber(generateTripNumber())
            .progressPct(0)
            .status("pending")
            .build();
    return repository.save(trip);
}
```

**`change-status` — check ownership cho role `DRIVER`**: tài xế chỉ được đổi trạng thái chuyến của chính mình (gọi qua `dispatch_assignment.driver_id == drivers.id` của user hiện tại). Đây lại là 1 ownership-check thủ công ngoài `AuthorizationAspect`:

```java
@Override
@Transactional
public Trip changeStatus(UUID id, String status) {
    AuthInfo authInfo = SecurityContextHolder.getAuthInfo();
    if (authInfo.hasAnyRole(RoleType.DRIVER) && !authInfo.hasAnyRole(RoleType.DISPATCHER, RoleType.SYSTEM_ADMIN)) {
        Trip trip = getOne(id).orElseThrow(() -> new NotFoundException("Chuyến không tồn tại"));
        UUID driverIdOfTrip = dispatchAssignmentRepository.findById(trip.getDispatchAssignmentId())
                .map(DispatchAssignment::getDriverId).orElseThrow();
        Driver currentDriver = driverRepository.findByUserId(authInfo.getUserId())
                .orElseThrow(() -> new AccessDeniedException("Tài khoản chưa liên kết tài xế"));
        if (!currentDriver.getId().equals(driverIdOfTrip)) {
            throw new AccessDeniedException("Không có quyền với chuyến này");
        }
    }

    Trip updated = super.changeStatus(id, status);

    // Nếu chuyến hoàn tất, cập nhật vehicle/driver thoát trạng thái "đang bận"
    if ("completed".equals(status) || "cancelled".equals(status)) {
        dispatchAssignmentRepository.findById(updated.getDispatchAssignmentId())
                .ifPresent(da -> dispatchAssignmentService.changeStatus(da.getId(), "completed"));
    }
    return updated;
}
```

**`replay` (FR-TR-08)** — query toàn bộ `gps_positions` của `trip_id` (composite key, đã thiết kế ở phần trước), KHÔNG gọi qua `BaseServiceImpl.filter()` generic mà dùng repository method chuyên biệt vì cần sắp xếp theo `recorded_at ASC` bắt buộc:

```java
List<GpsPosition> findByTripIdOrderByIdRecordedAtAsc(UUID tripId);
```

---

## 5.4. TripStop (`trip_stops`)

| # | API | Method | URL | Quyền | Logic | Workspace |
|---|---|---|---|---|---|---|
| 1 | Tạo | POST | `/api/v1/trip-stop/create` | `DISPATCHER` | — | Có |
| 2 | Cập nhật | PUT | `/api/v1/trip-stop/update/{id}` | `DISPATCHER`, `DRIVER` (chỉ stop thuộc chuyến của mình) | OR | Có |
| 3 | Lấy theo ID | GET | `/api/v1/trip-stop/get-by-id/{id}` | `ALL` | OR | Có |
| 4 | Lấy theo chuyến | GET | `/api/v1/trip-stops/get-by-trip/{tripId}` | `ALL` | OR | Có |
| 5 | Đổi trạng thái (đến điểm dừng) | PATCH | `/api/v1/trip-stop/change-status/{id}` | `DRIVER` | — | Có |
| 6 | Xoá mềm | DELETE | `/api/v1/trip-stop/soft-delete/{id}` | `DISPATCHER` | — | Có |
| 7 | Xoá cứng | DELETE | `/api/v1/trip-stop/hard-delete/{id}` | `SYSTEM_ADMIN` | — | Không |
| 8 | **Cập nhật ePOD (ảnh + chữ ký)** | POST | `/api/v1/trip-stop/{id}/submit-epod` | `DRIVER` | — | Có |

### Chi tiết logic nghiệp vụ đặc thù

**`submit-epod` (FR-MA-03)** — nhận multipart file (ảnh + chữ ký), upload MinIO qua `FileAttachmentService`, gắn `entity_type = epod_photo` / `epod_sign`, `entity_id = tripStopId`:

```java
@PostMapping(value = "/{id}/submit-epod", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
@RequireAuth(roles = {RoleType.DRIVER}, inWorkspace = true)
public ResponseData<TripStop> submitEpod(
        @PathVariable UUID id,
        @RequestParam("photo") MultipartFile photo,
        @RequestParam("signature") MultipartFile signature) {

    fileAttachmentService.upload(photo, "epod_photo", id);
    fileAttachmentService.upload(signature, "epod_sign", id);

    TripStop stop = tripStopService.changeStatus(id, "completed");
    stop.setActualArrival(OffsetDateTime.now());
    return ResponseData.<TripStop>builder().status(200).data(tripStopService.update(stop)).message("Đã ghi nhận ePOD").build();
}
```

> Theo phân tích ở vòng đối chiếu tài liệu trước, schema hiện tại **chưa có bảng nghiệp vụ riêng cho ePOD** — đang tận dụng `file_attachments.entity_type` với `entity_id = trip_stops.id`. Nếu sau này cần lưu thêm metadata riêng (vd: GPS lúc ký, tên người nhận hàng), nên cân nhắc thêm bảng `epod_records` riêng thay vì chỉ dựa vào `file_attachments`.

---

## 5.5. TripIncident (`trip_incidents`)

| # | API | Method | URL | Quyền | Logic | Workspace |
|---|---|---|---|---|---|---|
| 1 | Tạo (báo cáo sự cố) | POST | `/api/v1/trip-incident/create` | `DRIVER`, `DISPATCHER` | OR | Có |
| 2 | Cập nhật | PUT | `/api/v1/trip-incident/update/{id}` | `DISPATCHER` | — | Có |
| 3 | Lấy theo ID | GET | `/api/v1/trip-incident/get-by-id/{id}` | `ALL` | OR | Có |
| 4 | Lọc/phân trang | POST | `/api/v1/trip-incidents/filter` | `DISPATCHER`, `OPS_MANAGER` | OR | Có |
| 5 | **Xử lý/đóng sự cố** | PATCH | `/api/v1/trip-incident/{id}/resolve` | `DISPATCHER` | — | Có |
| 6 | Xoá mềm | DELETE | `/api/v1/trip-incident/soft-delete/{id}` | `DISPATCHER` | — | Có |
| 7 | Xoá cứng | DELETE | `/api/v1/trip-incident/hard-delete/{id}` | `SYSTEM_ADMIN` | — | Không |

### Chi tiết logic nghiệp vụ đặc thù

**`resolve`** — không dùng `change-status` generic vì cần ghi thêm `resolved_by`, `resolved_at` đồng thời:

```java
@Transactional
public TripIncident resolve(UUID id) {
    TripIncident incident = getOne(id).orElseThrow(() -> new NotFoundException("Sự cố không tồn tại"));
    incident.setStatus("resolved");
    incident.setResolvedBy(SecurityContextHolder.getAuthInfo().getUserId());
    incident.setResolvedAt(OffsetDateTime.now());
    return repository.save(incident);
}
```

**`create` với `incident_type = breakdown` hoặc `accident`** — nên tự động trigger `gps_alerts` hoặc notification khẩn tới `OPS_MANAGER` (mức độ ưu tiên cao, theo FR-TR-06 "điều phối phản ứng nhanh"):

```java
if (Set.of("breakdown", "accident").contains(entity.getIncidentType())) {
    notificationService.notifyUrgent(trip.getOrgId(), "Sự cố khẩn cấp: " + entity.getIncidentType(), incident.getId());
}
```

---

## 5.6. FuelLog (`fuel_logs`)

| # | API | Method | URL | Quyền | Logic | Workspace |
|---|---|---|---|---|---|---|
| 1 | Tạo | POST | `/api/v1/fuel-log/create` | `DRIVER`, `FLEET_MANAGER` | OR | Có |
| 2 | Cập nhật | PUT | `/api/v1/fuel-log/update/{id}` | `FLEET_MANAGER` | — | Có |
| 3 | Lấy theo ID | GET | `/api/v1/fuel-log/get-by-id/{id}` | `FLEET_MANAGER`, `OPS_MANAGER` | OR | Có |
| 4 | Lọc/phân trang | POST | `/api/v1/fuel-logs/filter` | `FLEET_MANAGER`, `OPS_MANAGER` | OR | Có |
| 5 | Xoá mềm | DELETE | `/api/v1/fuel-log/soft-delete/{id}` | `FLEET_MANAGER` | — | Có |
| 6 | Xoá cứng | DELETE | `/api/v1/fuel-log/hard-delete/{id}` | `SYSTEM_ADMIN` | — | Không |
| 7 | **Phát hiện bất thường nhiên liệu** | GET | `/api/v1/fuel-logs/get-anomalies` | `FLEET_MANAGER`, `OPS_MANAGER` | OR | Có |

### Chi tiết logic nghiệp vụ đặc thù

**`create`** — tự động tính `anomaly_flagged` bằng cách so sánh `reported_consumption` (tài xế tự khai) với `telemetry_consumption` (tính từ GPS/odometer), lệch quá ngưỡng (đọc từ `SystemConfig`, ví dụ 15%) thì gắn cờ (FR-HM-07):

```java
@Override
@Transactional
public FuelLog create(FuelLog entity) {
    if (entity.getReportedConsumption() != null && entity.getTelemetryConsumption() != null) {
        BigDecimal diff = entity.getReportedConsumption().subtract(entity.getTelemetryConsumption()).abs();
        BigDecimal threshold = entity.getTelemetryConsumption()
                .multiply(getAnomalyThresholdPercent()); // đọc từ SystemConfig, mặc định 0.15
        if (diff.compareTo(threshold) > 0) {
            entity.setAnomalyFlagged(true);
            entity.setAnomalyNotes("Lệch " + diff + "L so với telemetry, vượt ngưỡng cho phép");
        }
    }
    FuelLog saved = super.create(entity);

    if (Boolean.TRUE.equals(saved.getAnomalyFlagged())) {
        notificationService.notifyFleetManagers(saved.getVehicleId(), "Phát hiện bất thường nhiên liệu");
    }
    return saved;
}
```
# Module 6 — GPS & Telemetry

Entity: `GpsPosition` (composite key `vehicle_id + recorded_at`), `GpsAlert`, `Geofence`

> Module này có pattern Redis KHÁC với mục 1.3 — không phải cache-aside thông thường mà là **lưu trạng thái mới nhất** (latest-state store), theo đúng kiến trúc mục 5.2 tài liệu đặc tả: "Vị trí hiện tại lưu vào bộ nhớ nhanh (Redis); lịch sử lưu vào CSDL chuỗi thời gian".

---

## 6.1. GpsPosition (`gps_positions`)

**Nhóm cache**: Đặc biệt — KHÔNG thuộc nhóm cache "danh mục tĩnh" theo quy ước mục 1.3, nhưng dùng Redis theo mô hình khác (xem bên dưới). Endpoint `get-by-id` (composite key) **không cache theo nghĩa cache-aside thông thường**, mà có endpoint riêng `get-current-position` luôn đọc Redis trước.

| # | API | Method | URL | Quyền | Logic | Workspace |
|---|---|---|---|---|---|---|
| 1 | **Ghi nhận vị trí (ingest)** | POST | `/api/v1/gps-position/ingest` | `DRIVER` (qua thiết bị/app) | — | Có |
| 2 | Lấy theo composite key | GET | `/api/v1/gps-position/get-by-id?vehicleId={}&recordedAt={}` | `DISPATCHER`, `OPS_MANAGER` | OR | Có |
| 3 | Lọc/phân trang (lịch sử) | POST | `/api/v1/gps-positions/filter` | `DISPATCHER`, `OPS_MANAGER` | OR | Có |
| 4 | **Vị trí hiện tại của 1 xe** | GET | `/api/v1/gps-position/get-current/{vehicleId}` | `ALL` | OR | Có |
| 5 | **Vị trí hiện tại của TOÀN BỘ xe trong org (cho bản đồ)** | GET | `/api/v1/gps-positions/get-current-all` | `DISPATCHER`, `OPS_MANAGER` | OR | Có |
| 6 | Xoá cứng (dọn dữ liệu cũ, định kỳ — KHÔNG có soft-delete vì bảng time-series) | DELETE | `/api/v1/gps-position/hard-delete-before/{date}` | `SYSTEM_ADMIN` | — | Không |

> Không có `update` — dữ liệu GPS là **immutable** (ghi 1 lần, không sửa). Không có `change-status` vì bản chất time-series không cần trạng thái.

### Chi tiết logic nghiệp vụ đặc thù

**`ingest` (FR-MA-02, luồng 5.2 tài liệu đặc tả)** — đây là API tần suất cao nhất hệ thống (~1.000 req/s đỉnh điểm). Luồng xử lý đúng theo kiến trúc đề xuất: ghi Kafka (buffer) → consumer ghi TimescaleDB (lịch sử) + ghi đè Redis (vị trí hiện tại). KHÔNG ghi trực tiếp DB đồng bộ trong request thread để tránh nghẽn:

```java
@PostMapping("/ingest")
@RequireAuth(roles = {RoleType.DRIVER, RoleType.ALL}, inWorkspace = true)
public ResponseData<Void> ingest(@RequestBody @Valid GpsIngestRequest request) {
    // Đẩy vào Kafka topic "gps-position-events", KHÔNG insert DB trực tiếp ở đây
    kafkaTemplate.send("gps-position-events", request.getVehicleId().toString(), request);
    return ResponseData.<Void>builder().status(202).message("Đã tiếp nhận").build();
}
```

**Kafka Consumer** (chạy nền, không phải REST endpoint) — xử lý 2 việc song song:

```java
@KafkaListener(topics = "gps-position-events", groupId = "gps-processor")
public void consume(GpsIngestRequest event) {
    // 1. Ghi lịch sử vào TimescaleDB (composite key vehicle_id + recorded_at)
    GpsPosition position = mapToEntity(event);
    gpsPositionRepository.save(position);

    // 2. Ghi đè Redis — vị trí MỚI NHẤT, key KHÔNG theo timestamp (luôn overwrite)
    String redisKey = redisService.buildKey("vehicle-position", event.getVehicleId().toString());
    redisService.set(redisKey, position, 300); // TTL 5 phút — nếu xe mất tín hiệu > 5 phút, coi như "không rõ vị trí"

    // 3. Check geofence + speeding -> tạo GpsAlert nếu vi phạm (xem mục 6.2)
    geofenceService.checkViolations(position);
    if (position.getSpeedKmh() != null && position.getSpeedKmh() > getSpeedLimitForVehicle(position.getVehicleId())) {
        gpsAlertService.create(buildSpeedingAlert(position));
    }
}
```

**`get-current/{vehicleId}` — đọc Redis trước, KHÔNG fallback query DB nếu miss** (khác hẳn pattern cache-aside ở Module 1, vì nếu Redis không có nghĩa là xe mất tín hiệu thật sự, query DB lúc này chỉ trả về vị trí cũ gây hiểu lầm "đang ở đó" trong khi thực tế đã mất kết nối):

```java
@Transactional(readOnly = true)
public GpsPositionDto getCurrentPosition(UUID vehicleId) {
    String key = redisService.buildKey("vehicle-position", vehicleId.toString());
    GpsPosition cached = redisService.get(key, GpsPosition.class);

    if (cached == null) {
        return GpsPositionDto.builder().vehicleId(vehicleId).status("offline").build();
    }
    return GpsPositionDto.from(cached);
}
```

**`get-current-all` (FR-DB-02, FR-TR-02 — bản đồ vận hành)** — dùng `redisService` quét theo prefix (cần bổ sung method `scanByPrefix` vào `RedisService` nếu chưa có, vì interface hiện tại chỉ có `get/set/delete/exists/buildKey`, chưa có scan):

```java
// Cần bổ sung vào RedisService interface:
// List<String> scanKeys(String pattern);

@Transactional(readOnly = true)
public List<GpsPositionDto> getCurrentAllInOrg(UUID orgId) {
    List<UUID> vehicleIds = vehicleRepository.findIdsByOrgIdAndStatus(orgId, "active");
    return vehicleIds.stream()
            .map(this::getCurrentPosition)
            .filter(dto -> !"offline".equals(dto.getStatus()))
            .collect(Collectors.toList());
}
```

> Cách triển khai trên gọi Redis N lần (N = số xe active trong org) — chấp nhận được vì Redis GET đơn lẻ rất rẻ (~1ms), nhưng nếu org có hàng nghìn xe, cân nhắc dùng Redis Pipeline (`RedisTemplate.executePipelined`) để gom N lệnh GET thành 1 round-trip mạng, giảm độ trễ tổng thể đáng kể khi N lớn.

**`hard-delete-before/{date}`** — job dọn dữ liệu định kỳ, chỉ giữ lịch sử trong khoảng thời gian theo chính sách lưu trữ (ví dụ 12 tháng), tận dụng TimescaleDB retention policy thay vì xoá thủ công nếu đã setup hypertable:

```sql
-- Nếu dùng TimescaleDB, khuyến nghị dùng retention policy tự động thay vì gọi API xoá thủ công:
SELECT add_retention_policy('gps_positions', INTERVAL '12 months');
```

---

## 6.2. GpsAlert (`gps_alerts`)

**Nhóm cache**: KHÔNG

| # | API | Method | URL | Quyền | Logic | Workspace |
|---|---|---|---|---|---|---|
| 1 | Tạo (nội bộ, từ GPS consumer) | — | — | — | — | — |
| 2 | Lấy theo ID | GET | `/api/v1/gps-alert/get-by-id/{id}` | `DISPATCHER`, `OPS_MANAGER` | OR | Có |
| 3 | Lọc/phân trang | POST | `/api/v1/gps-alerts/filter` | `DISPATCHER`, `OPS_MANAGER` | OR | Có |
| 4 | **Danh sách cảnh báo chưa xử lý** | GET | `/api/v1/gps-alerts/get-open` | `DISPATCHER` | — | Có |
| 5 | **Xác nhận đã xử lý** | PATCH | `/api/v1/gps-alert/{id}/acknowledge` | `DISPATCHER` | — | Có |
| 6 | Xoá cứng | DELETE | `/api/v1/gps-alert/hard-delete/{id}` | `SYSTEM_ADMIN` | — | Không |

### Chi tiết logic nghiệp vụ đặc thù

**`acknowledge` (FR-TR-04, FR-TR-07)**:

```java
@Transactional
public GpsAlert acknowledge(UUID id) {
    GpsAlert alert = getOne(id).orElseThrow(() -> new NotFoundException("Cảnh báo không tồn tại"));
    alert.setIsAcknowledged(true);
    alert.setAcknowledgedBy(SecurityContextHolder.getAuthInfo().getUserId());
    alert.setAcknowledgedAt(OffsetDateTime.now());
    alert.setStatus("acknowledged");
    return repository.save(alert);
}
```

**`get-open`** — dùng index có sẵn trong schema gốc (`idx_gps_alert_open ON gps_alerts (status) WHERE status = 'open'`), query rất rẻ nhờ partial index:

```java
List<GpsAlert> findByOrgIdAndStatusOrderByTriggeredAtDesc(UUID orgId, String status);
```

---

## 6.3. Geofence (`geofences`)

**Nhóm cache**: CÓ (danh mục tĩnh, ít thay đổi) — TTL 3600s, key `geofence:{id}` + `geofence:all-by-org:{orgId}`

| # | API | Method | URL | Quyền | Logic | Workspace | Cache |
|---|---|---|---|---|---|---|---|
| 1 | Tạo | POST | `/api/v1/geofence/create` | `SYSTEM_ADMIN`, `DISPATCHER` | OR | Có | Invalidate `all-by-org:{orgId}` |
| 2 | Cập nhật | PUT | `/api/v1/geofence/update/{id}` | `SYSTEM_ADMIN`, `DISPATCHER` | OR | Có | Invalidate cả 2 key |
| 3 | Lấy theo ID | GET | `/api/v1/geofence/get-by-id/{id}` | `ALL` | OR | Có | Đọc cache trước |
| 4 | Lấy toàn bộ theo org | GET | `/api/v1/geofences/get-all` | `ALL` | OR | Có | Cache `all-by-org:{orgId}`, TTL 3600s |
| 5 | Lọc/phân trang | POST | `/api/v1/geofences/filter` | `SYSTEM_ADMIN`, `DISPATCHER` | OR | Có | Không cache |
| 6 | Đổi trạng thái | PATCH | `/api/v1/geofence/change-status/{id}` | `SYSTEM_ADMIN` | — | Có | Invalidate cả 2 key |
| 7 | Xoá mềm | DELETE | `/api/v1/geofence/soft-delete/{id}` | `SYSTEM_ADMIN` | — | Có | Invalidate cả 2 key |
| 8 | Xoá cứng | DELETE | `/api/v1/geofence/hard-delete/{id}` | `SYSTEM_ADMIN` | — | Có | Invalidate cả 2 key |

### Chi tiết logic nghiệp vụ đặc thù

**`checkViolations(GpsPosition position)` — được gọi từ GPS Kafka Consumer (mục 6.1)**, đọc cache `geofence:all-by-org:{orgId}` (không query DB mỗi lần có GPS event mới, vì tần suất gọi cực cao trong khi geofence gần như không đổi):

```java
@Transactional(readOnly = true)
public void checkViolations(GpsPosition position) {
    UUID orgId = vehicleService.getOne(position.getVehicleId()).map(Vehicle::getOrgId).orElseThrow();
    List<Geofence> geofences = getAllCachedByOrg(orgId);

    for (Geofence fence : geofences) {
        boolean isInside = isPointInGeofence(position.getLat(), position.getLng(), fence);
        boolean wasInsideBefore = checkPreviousState(position.getVehicleId(), fence.getId()); // đọc Redis state trước đó

        if (isInside && !wasInsideBefore) {
            gpsAlertService.createGeofenceAlert(position, fence, "geofence_enter");
        } else if (!isInside && wasInsideBefore) {
            gpsAlertService.createGeofenceAlert(position, fence, "geofence_exit");
        }
    }
}
```

> Việc check "đã ở trong geofence từ trước hay chưa" (`wasInsideBefore`) cần lưu trạng thái tạm — đây là use-case Redis thứ 3 trong hệ thống (khác cache-aside, khác latest-position store): **lưu trạng thái boolean theo cặp `(vehicle_id, geofence_id)`**, key dạng `geofence-state:{vehicleId}:{geofenceId}`, value `true/false`, TTL dài (vd 24h, refresh mỗi lần có event).
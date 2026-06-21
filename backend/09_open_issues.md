# Phụ lục — Các vấn đề kiến trúc còn mở (cần team xác nhận trước khi code chính thức)

Tổng hợp toàn bộ điểm "chưa chốt" phát sinh trong lúc viết đặc tả 8 module — đây không phải lỗi, mà là quyết định nghiệp vụ/kiến trúc nằm ngoài phạm vi suy luận thuần từ schema + tài liệu hiện có.

---

## 1. `AuthorizationAspect` chỉ check role + workspace, KHÔNG check ownership (row-level)

Xuất hiện lặp lại ở **5 entity**: `User` (tự sửa chính mình), `Notification` (xem/xoá thông báo của mình), `Trip`/`TripStop` (tài xế chỉ thao tác chuyến của mình), `FileAttachment` (xoá file của mình).

**Hiện trạng**: mỗi service phải tự viết check `if (!isOwner && !isAdmin) throw ...` thủ công, lặp lại logic tương tự nhiều nơi.

**Đề xuất**: cân nhắc mở rộng `RequireAuth` thêm thuộc tính `ownershipField()` (tên field chứa user_id chủ sở hữu) + 1 cơ chế chung trong `AuthorizationAspect` hoặc 1 base class `OwnableServiceBase` để tránh trùng lặp code. Cần xác nhận có đáng để đầu tư trừu tượng hoá hay giữ nguyên cách viết thủ công từng chỗ (đơn giản hơn, nhưng dễ sót khi thêm entity mới).

## 2. Quyền override điều xe (FR-DP-05) — 1 người hay 2 người duyệt?

Đặt `@RequireAuth(roles = {DISPATCHER})` đơn giản, nhưng tài liệu đặc tả không nói rõ override có cần cấp phê duyệt cao hơn (vd. `OPS_MANAGER` xác nhận) hay tự `DISPATCHER` quyết được. **Cần xác nhận với team nghiệp vụ** trước khi khoá cứng logic này — ảnh hưởng trực tiếp tới an toàn vận hành (override sai có thể đưa xe không đạt chuẩn ra đường).

## 3. JWT chứa role tĩnh — thu hồi quyền không có hiệu lực ngay lập tức

Nêu chi tiết ở Module 1, mục `UserRole`. Nếu `AuthInfo`/role được nhúng cứng vào JWT lúc đăng nhập, sửa `user_roles` không ảnh hưởng tới token đang lưu ở client cho tới khi hết hạn. Cần chọn 1 trong 2 hướng:
- JWT access-token TTL ngắn (5-15 phút) + refresh token.
- Mỗi request luôn tra lại quyền mới nhất từ DB/Redis (tốn thêm round-trip, nhưng đúng theo thời gian thực).

## 4. `org_id` của `VehicleHealth`/`MaintenanceOrder`/`MaintenanceItem` không có cột trực tiếp

Phải JOIN qua `vehicles.org_id` để xác định workspace. `AuthorizationAspect` hiện tại không tự làm được việc này — cần custom logic kiểm tra `entity.getVehicle().getOrgId()` ở tầng Service cho 3 entity này (và tương tự `MaintenanceItem` phải JOIN qua `MaintenanceOrder` → `Vehicle`).

## 5. Cổng đối tác (FR-PT-05) và actor "đối tác" — chưa có role tương ứng

`IRoleType` hiện tại có 7 role nội bộ, không có role cho user thuộc tổ chức đối tác (`organizations.org_type = PARTNER`) tự đăng nhập xem dữ liệu của họ. Đây là tính năng GĐ3 (tuỳ chọn) theo tài liệu, nhưng nếu triển khai cần thiết kế thêm role/scope riêng — không tái dùng được trực tiếp 7 role hiện tại.

## 6. `dispatch_assignments.vehicle_id`/`driver_id` chỉ tham chiếu nội bộ, KHÔNG hỗ trợ xe/tài xế đối tác

Nêu chi tiết ở Module 7, mục `PartnerVehicle`. Nếu nghiệp vụ cần điều phối xe đối tác qua cùng luồng `DispatchAssignment`, cần bổ sung cột nullable hoặc tách bảng riêng — ảnh hưởng tới toàn bộ logic cổng chặn (FR-DP-04) vì hiện tại được viết riêng cho `Vehicle`/`Driver` nội bộ.

## 7. ePOD (FR-MA-03) hiện chỉ dựa vào `file_attachments`, chưa có bảng nghiệp vụ riêng

Nếu cần lưu thêm metadata (GPS lúc ký, tên người nhận hàng, thời gian ký riêng biệt với thời gian upload file), nên cân nhắc bảng `epod_records` riêng.

## 8. `RedisService` cần bổ sung 2 method để hỗ trợ đầy đủ các pattern đã thiết kế

- `scanKeys(String pattern)` — dùng cho `get-current-all` (Module 6) nếu không muốn lặp N lần GET.
- `get(String key, TypeReference<T> typeRef)` hoặc tương đương — để cache đúng kiểu `List<T>` mà không mất type info qua Jackson type erasure (nêu ở Module 1, mục `Role.getAllCached()`).

## 9. Schema `customers.code` UNIQUE toàn hệ thống, không theo `org_id`

Nghĩa là 2 chi nhánh khác nhau không thể đặt cùng 1 mã khách hàng — cần xác nhận đây có đúng ý đồ (khách hàng dùng chung toàn hệ thống) hay nên đổi thành `UNIQUE(org_id, code)` nếu mỗi chi nhánh quản lý mã khách hàng độc lập.

## 10. Tính `total_distance_km`/`total_cost` của `Trip` — chưa định nghĩa nguồn tính

Tài liệu đặc tả không nói rõ 2 trường này tính từ đâu (tổng khoảng cách giữa các `trip_stops`, hay tích phân từ `gps_positions`, hay nhập tay). Cần xác nhận công thức trước khi code `Trip.changeStatus → completed`.

---

> **Khuyến nghị quy trình:** review tài liệu này cùng Product Owner / Tech Lead trước khi bắt đầu sprint code chính thức — đặc biệt mục 2 (override) và mục 3 (JWT revoke) ảnh hưởng tới an toàn vận hành và bảo mật, nên ưu tiên chốt sớm nhất.
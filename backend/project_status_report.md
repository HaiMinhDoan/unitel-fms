# Báo cáo Trạng thái Dự án (FMS - Fleet Management System)

Tính đến thời điểm hiện tại, dự án Quản lý Đội xe (FMS) đã hoàn thiện được một khối lượng lớn công việc với nền tảng kiến trúc vững chắc, tối ưu hóa được cả khía cạnh Clean Code và Hiệu năng.

Dưới đây là bức tranh toàn cảnh về những gì hệ thống đã có và những gì còn thiếu:

---

## ✅ Những gì hệ thống ĐÃ LÀM ĐƯỢC

### 1. Kiến trúc Nền tảng (Core Architecture - Module 1)
- Thiết lập xong toàn bộ khung **Spring Boot 3 + Java 17 + PostgreSQL**.
- Tích hợp chuẩn **MapStruct & DTO Pattern**, đảm bảo an toàn dữ liệu, chống over-posting.
- **RBAC Security**: Phân quyền Role-based (`@RequireAuth`) chi tiết cho từng loại user (`SYSTEM_ADMIN`, `FLEET_MANAGER`, `DISPATCHER`, `DRIVER`...) kết hợp kiểm tra Tenant (Workspace Isolation) bảo vệ dữ liệu chéo giữa các Organization.
- Xây dựng thành công `BaseServiceImpl` và `BaseFilterRequest`, giúp tự động hóa 90% các chức năng CRUD và lọc phân trang động (Filter/Pagination).

### 2. Quản lý Phương tiện và Bảo dưỡng (Module 2)
- Đã hoàn thiện toàn bộ Entity, API và Logic cho `Vehicle`, `VehicleType`, `MaintenanceOrder`.
- Có logic kiểm tra tình trạng sức khỏe xe (Vehicle Health), tự động tính toán tổng phí sửa chữa từ các hạng mục (`sumTotalCostByOrderId`).
- **Cron Job Tự động**: Quét hàng đêm giấy tờ xe (`VehicleDocument`) sắp hết hạn/hết hạn.

### 3. Quản lý Tài xế (Module 3)
- Triển khai thành công Abstract logic `ExpirableDocumentServiceBase` cho phép áp dụng chung luồng quản lý ngày hết hạn cho nhiều loại giấy tờ (Bằng lái, GPLX, Bảo hiểm).
- Đã triển khai API **Check Dispatch Eligibility**: Tự động đánh giá xem xe và tài xế đã hợp lệ để nhận chuyến hay chưa (dựa trên sức khoẻ xe và hạn giấy tờ).
- Có API `/me` bảo mật để tài xế tự theo dõi thông tin của mình.

### 4. Khách hàng & Tổ chức (Module 4)
- Triển khai đầy đủ CRUD, chặn xóa cứng Khách hàng nếu họ đang có Yêu cầu điều phối.

### 5. Điều phối và Hành trình (Module 5)
- Hệ thống đã liên kết các thực thể `DispatchRequest` ➔ `DispatchAssignment` ➔ `Trip` ➔ `TripStop`.
- Xây dựng API gán chuyến (Assign) và đổi trạng thái chuyến đi (`TripService.changeStatus`). Khi chuyến đi hoàn tất, tự động khép lại Assignment.
- Tích hợp bảo mật: Chỉ tài xế được gán đúng chuyến đó mới có quyền cập nhật trạng thái (`progress_pct`, hoàn thành trạm).

### 6. GPS & Telemetry (Module 6)
- **High-throughput System**: Sử dụng **Kafka** để làm bộ đệm (Buffer) tiếp nhận siêu tốc các tín hiệu tọa độ gửi về từ thiết bị IoT.
- **Real-time Map**: Sử dụng **Redis** để lưu vị trí "Latest State" của toàn bộ xe, giúp Dispatcher có thể xem xe di chuyển trên bản đồ theo thời gian thực mà không làm treo Database.
- **Geofence siêu nhẹ**: Xây dựng thuật toán toán học *Point-in-Polygon* và *Haversine* hoàn toàn bằng Java để kiểm tra xem xe đi vào hay đi ra khỏi hàng rào ảo. Tránh phải dùng PostGIS nặng nề. Gắn với cơ chế Alert khi quá tốc độ.

### 7. Lưu trữ File đính kèm (Module 8)
- Hoàn thiện tích hợp với **MinIO (S3-Compatible)**. Hệ thống có khả năng nhận đa dạng các định dạng File (ePOD, Hóa đơn xăng xe, v.v) qua API `FileAttachmentController`.
- Tự động lấy metadata và lưu đường dẫn.

---

## 🚧 Những gì hệ thống CÒN THIẾU (Cần phát triển tiếp)

Dù đã có khung xương vững chãi, hệ thống hiện tại vẫn còn thiếu một số mảnh ghép để hoàn thiện chu trình kinh doanh:

### 1. Quản lý Đối tác/Nhà thầu (Module 7)
*(Đây là Module bạn vừa yêu cầu tạm dừng)*
- Thiếu các bảng và API quản lý Đối tác (`Partner`), Xe đối tác, Tài xế đối tác.
- Thiếu logic phân bổ chuyến cho Nhà thầu ngoài.
- Thiếu thuật toán So sánh và Lựa chọn nhà thầu (Dựa vào % đúng hạn, Rating và Chi phí).

### 2. Thuật toán Tự động điều phối (Auto-Dispatch / Routing)
- Hiện tại, luồng Dispatch (Module 5) mới chỉ là **Điều phối thủ công (Manual)** (Dispatcher phải tự chọn xe, chọn tài xế, tạo Assignment).
- Thiếu giải thuật tự động gợi ý tài xế rảnh gần nhất, hoặc tìm tuyến đường (Routing) tối ưu qua Google Maps API/OSRM.

### 3. Phân hệ Báo cáo & Dashboard
- Chưa có các API phục vụ vẽ biểu đồ (Chart) tổng quan cho cấp Quản lý (Ví dụ: Doanh thu theo tháng, Tỉ lệ xe trống, Tỉ lệ hoàn thành đúng hạn SLA).
- Chưa có báo cáo tổng hợp Tiêu hao nhiên liệu (Fuel consumption). Mặc dù có entity nhưng thiếu logic tổng hợp.

### 4. Hệ thống Thông báo (Notification & WebSockets)
- Mặc dù hệ thống đã có entity `Notification`, nhưng hiện tại nó chỉ được "tạo ra" và lưu vào Database.
- Thiếu công nghệ **WebSocket / Server-Sent Events (SSE)** để bắn thông báo Popup thời gian thực xuống trình duyệt của Dispatcher hoặc App của Driver khi có chuyến mới.

### 5. Frontend & Mobile App Integration
- Toàn bộ những gì chúng ta làm đang là **Backend API**. Để dự án thực sự chạy được, chúng ta cần ghép nối với bộ Frontend (Web cho Admin/Dispatcher) và Mobile App (Dành cho Lái xe).

---

> [!TIP]
> **Khuyến nghị bước tiếp theo:** 
> Nếu tạm gác Module 7, bạn có thể lựa chọn triển khai **Hệ thống WebSocket (để làm App Real-time)** hoặc đi sâu vào **Dashboard / Logic Báo Cáo** để làm phong phú dữ liệu thống kê. Bạn muốn chọn hướng nào?

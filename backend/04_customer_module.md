# Module 4 — Khách hàng

Entity: `Customer`

---

## 4.1. Customer (`customers`)

**Nhóm cache**: KHÔNG (không nằm trong nhóm danh mục cực tĩnh theo phân loại đã chốt — dù ít thay đổi hơn `vehicles`, nhưng số lượng khách hàng có thể tăng trưởng theo thời gian và không phải dữ liệu dùng để validate logic nghiệp vụ cốt lõi như cổng chặn điều xe, nên giữ nhất quán không cache để đơn giản hoá).

| # | API | Method | URL | Quyền | Logic | Workspace |
|---|---|---|---|---|---|---|
| 1 | Tạo | POST | `/api/v1/customer/create` | `DISPATCHER`, `OPS_MANAGER` | OR | Có |
| 2 | Cập nhật toàn bộ | PUT | `/api/v1/customer/update/{id}` | `DISPATCHER`, `OPS_MANAGER` | OR | Có |
| 3 | Cập nhật 1 phần | PATCH | `/api/v1/customer/update-partial/{id}` | `DISPATCHER`, `OPS_MANAGER` | OR | Có |
| 4 | Lấy theo ID | GET | `/api/v1/customer/get-by-id/{id}` | `ALL` | OR | Có |
| 5 | Lấy toàn bộ | GET | `/api/v1/customers/get-all` | `DISPATCHER` | — | Có (chỉ khi org ít khách hàng; nếu org lớn cân nhắc bỏ, dùng filter) |
| 6 | Lọc/phân trang | POST | `/api/v1/customers/filter` | `ALL` | OR | Có |
| 7 | Đổi trạng thái | PATCH | `/api/v1/customer/change-status/{id}` | `OPS_MANAGER` | — | Có |
| 8 | Xoá mềm | DELETE | `/api/v1/customer/soft-delete/{id}` | `OPS_MANAGER` | — | Có |
| 9 | Xoá cứng | DELETE | `/api/v1/customer/hard-delete/{id}` | `SYSTEM_ADMIN` | — | Không |

### Chi tiết logic nghiệp vụ đặc thù

**Tạo khách hàng** — validate `code` UNIQUE trong phạm vi toàn hệ thống (cột `code` ở schema gốc là `UNIQUE` không kèm `org_id`, nghĩa là mã khách hàng dùng chung toàn hệ thống, không phải theo từng workspace riêng — cần lưu ý khi sinh mã tự động, tránh trùng giữa các chi nhánh):

```java
@Override
@Transactional
public Customer create(Customer entity) {
    if (repository.existsByCode(entity.getCode())) {
        throw new ConflictException("Mã khách hàng '" + entity.getCode() + "' đã tồn tại trong hệ thống");
    }
    return super.create(entity);
}
```

**Hard-delete** — chặn nếu còn `dispatch_requests.customer_id` tham chiếu (tương tự pattern Organization ở Module 1):

```java
@Override
@Transactional
public void hardDelete(UUID id) {
    if (dispatchRequestRepository.existsByCustomerId(id)) {
        throw new ConflictException("Không thể xoá cứng: khách hàng còn yêu cầu điều xe liên quan. Dùng soft-delete.");
    }
    repository.deleteById(id);
}
```
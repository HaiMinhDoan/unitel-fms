# Hướng dẫn sử dụng API Filter

Dành cho Frontend AI Agent: Tài liệu này mô tả chi tiết cách sử dụng chức năng filter động (dynamic filter) khi gọi các API GET danh sách của hệ thống (dựa trên class `BaseServiceImpl.java`).

## 1. Cấu trúc Request Body (`BaseFilterRequest`)

Để filter, bạn gửi một request với body dạng JSON bao gồm:

```json
{
  "page": 0,
  "size": 20,
  "filters": [],
  "sorts": []
}
```

- **`page`**: Trang cần lấy (bắt đầu từ `0`). Mặc định là `0`.
- **`size`**: Số lượng bản ghi trên 1 trang. Mặc định là `20`.
- **`filters`**: Mảng các điều kiện lọc (`FilterCriteria`).
- **`sorts`**: Mảng các điều kiện sắp xếp (`SortCriteria`).

---

## 2. Các điều kiện lọc (`FilterCriteria`)

Mỗi phần tử trong mảng `filters` là một object có cấu trúc:

```json
{
  "fieldName": "tên_trường_trong_entity",
  "operation": "TOÁN_TỬ",
  "value": "giá trị cần lọc",
  "logicType": "AND"
}
```

### Chi tiết các thuộc tính:

- **`fieldName` (Bắt buộc)**: Là tên thuộc tính được khai báo trong class Entity tương ứng.
  - Hỗ trợ các trường cơ bản (có `@Column`).
  - Hỗ trợ trường liên kết (ví dụ `@ManyToOne`, `@OneToOne`, `@JoinColumn`, v.v.).
  - Hỗ trợ cả trường lồng nhau (nested field) phân cách bằng dấu chấm. Ví dụ: `"id.vehicleId"`, `"user.name"`.

- **`operation` (Bắt buộc)**: Loại phép toán. Các giá trị hợp lệ (Enum):
  - `EQUALS`: Bằng chính xác (`=`).
  - `LESS_THAN`: Nhỏ hơn (`<`, chỉ dùng cho kiểu Comparable như số, ngày tháng).
  - `LESS_THAN_OR_EQUAL`: Nhỏ hơn hoặc bằng (`<=`).
  - `GREATER_THAN`: Lớn hơn (`>`).
  - `GREATER_THAN_OR_EQUAL`: Lớn hơn hoặc bằng (`>=`).
  - `LIKE` / `ILIKE`: Tìm kiếm chuỗi có chứa giá trị (không phân biệt hoa thường, hệ thống tự động bọc `%value%` và convert string sang chữ thường để tìm kiếm).
  - `NOT_LIKE` / `NOT_ILIKE`: Tìm kiếm chuỗi không chứa giá trị (không phân biệt hoa thường).
  - `IN`: Thuộc danh sách (value phải là một mảng, ví dụ: `[1, 2, 3]`).
  - `NOT_IN`: Không thuộc danh sách.

- **`value`**: Giá trị để so sánh.
  - Backend có cơ chế tự động chuyển đổi sang đúng kiểu dữ liệu của `fieldName` (hỗ trợ `UUID`, `Long`, `Integer`, `Boolean`, `String`).
  - Đối với toán tử `IN` hoặc `NOT_IN`, `value` phải là một mảng (Array).

- **`logicType` (Tuỳ chọn)**: Cách kết hợp điều kiện này với các điều kiện trước đó.
  - `AND`: Điều kiện bắt buộc đồng thời (Mặc định).
  - `OR`: Điều kiện kết hợp theo OR.
  - *Lưu ý cơ chế nhóm logic của Backend:* Khi có cả `AND` và `OR` trong mảng `filters`, backend sẽ gom tất cả các predicate `AND` lại thành một nhóm, sau đó kết hợp nhóm đó với tất cả các predicate `OR` (Dạng tương đương: `(A AND B) OR C OR D`).

---

## 3. Các điều kiện sắp xếp (`SortCriteria`)

Mỗi phần tử trong mảng `sorts` quy định cách sắp xếp kết quả:

```json
{
  "fieldName": "tên_trường",
  "direction": "DESC"
}
```

- **`fieldName`**: Tên trường dùng để sắp xếp (cần tồn tại trong Entity giống như `filters`).
- **`direction`**: `ASC` (tăng dần) hoặc `DESC` (giảm dần).

---

## 4. Ví dụ tổng hợp (JSON Request)

**Yêu cầu tham khảo:** Lấy danh sách các phương tiện thuộc tổ chức "ORG-123", có năm sản xuất từ 2020 trở đi. Đồng thời kết quả cũng phải bao gồm các xe có trạng thái "ACTIVE" hoặc có biển số xe chứa chữ "29A" (Bất kể tổ chức nào). Sắp xếp giảm dần theo năm sản xuất, và lấy trang số 2 (page 1), mỗi trang 10 kết quả.

```json
{
  "page": 1,
  "size": 10,
  "filters": [
    {
      "fieldName": "organizationId",
      "operation": "EQUALS",
      "value": "ORG-123",
      "logicType": "AND"
    },
    {
      "fieldName": "productionYear",
      "operation": "GREATER_THAN_OR_EQUAL",
      "value": 2020,
      "logicType": "AND"
    },
    {
      "fieldName": "status",
      "operation": "EQUALS",
      "value": "ACTIVE",
      "logicType": "OR"
    },
    {
      "fieldName": "plateNumber",
      "operation": "LIKE",
      "value": "29A",
      "logicType": "OR"
    }
  ],
  "sorts": [
    {
      "fieldName": "productionYear",
      "direction": "DESC"
    }
  ]
}
```

> **Ghi chú logic xử lý backend của Request trên:**
> Câu truy vấn SQL JPA do backend sinh ra sẽ mang mệnh đề WHERE tương đương như sau:
> `((organizationId = 'ORG-123' AND productionYear >= 2020) OR status = 'ACTIVE' OR lower(plateNumber) LIKE '%29a%')`

---

## 5. Những lưu ý quan trọng cho Frontend AI Agent

1. **Tránh truyền thiếu `fieldName` hoặc `operation`:** Backend sẽ bỏ qua (`Skipped`) điều kiện đó nếu một trong hai field bị `null`.
2. **Sai tên fieldName:** Nếu `fieldName` không được định nghĩa bởi các Annotation hợp lệ trong Entity (VD: `@Column`, `@JoinColumn`, `@EmbeddedId`), API sẽ trả về lỗi **`InvalidFieldException`** (Field không tồn tại hoặc không hợp lệ).
3. **Toán tử LIKE / ILIKE:** Chỉ áp dụng cho kiểu chuỗi (`String`), backend tự động bọc wildcard hai đầu (`%value%`) và convert về chữ thường. Sẽ báo lỗi nếu dùng cho kiểu dữ liệu khác.
4. **Kiểu Comparable (Ngày, Số):** Toán tử so sánh lớn hơn, nhỏ hơn (`LESS_THAN`, `LESS_THAN_OR_EQUAL`, `GREATER_THAN`, `GREATER_THAN_OR_EQUAL`) chỉ áp dụng cho các field có thể so sánh được (vd: Date, Integer, Long). Nếu dùng cho kiểu không được hỗ trợ (ví dụ Boolean hay custom Object không implements Comparable) sẽ quăng Exception.

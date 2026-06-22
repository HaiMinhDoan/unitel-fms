package com.unitel.fms.backend.constants;

import java.util.HashMap;

public final class SystemMessage {
    public static final HashMap<String, String> VI = new HashMap<>();
    public static final HashMap<String, String> EN = new HashMap<>();
    public static final HashMap<String, String> LO = new HashMap<>();

    private SystemMessage() {}

    static {
        // VI
        VI.put("CONSTRAINT_VIOLATIONS", "Vi phạm ràng buộc sql.");
        VI.put("VALIDATION_FAILED", "Lỗi xác thực dữ liệu.");
        VI.put("MALFORMED_JSON_REQUEST", "Định dạng JSON không hợp lệ.");
        VI.put("METHOD_NOT_ALLOWED", "Phương thức không được phép.");
        VI.put("ACCESS_DENIED", "Truy cập bị từ chối.");
        VI.put("UNAUTHORIZED", "Không có quyền xác thực.");
        VI.put("RESOURCE_NOT_FOUND", "Không tìm thấy dữ liệu.");
        VI.put("DATA_INTEGRITY_VIOLATION", "Dữ liệu bị xung đột hoặc vi phạm toàn vẹn.");
        VI.put("INTERNAL_SERVER_ERROR", "Lỗi máy chủ nội bộ.");
        VI.put("SUCCESS", "Thành công");
        VI.put("INVALID_CREDENTIALS", "Sai tên đăng nhập hoặc mật khẩu");
        VI.put("ACCOUNT_LOCKED", "Tài khoản đang bị khóa");
        VI.put("LOGIN_SUCCESS", "Đăng nhập thành công");
        VI.put("REFRESH_TOKEN_EXPIRED", "Refresh token đã hết hạn");
        VI.put("REFRESH_TOKEN_INVALID", "Refresh token không hợp lệ hoặc đã bị thu hồi");
        VI.put("INVALID_TOKEN", "Token không hợp lệ");
        VI.put("REFRESH_SUCCESS", "Refresh thành công");
        VI.put("LOGOUT_SUCCESS", "Đăng xuất thành công");
        VI.put("NOT_FOUND", "Không tìm thấy");
        VI.put("CREATED", "Tạo mới thành công");
        VI.put("OVERRIDDEN", "Ghi đè thành công");
        VI.put("UPDATED", "Cập nhật thành công");
        VI.put("DELETED", "Xóa thành công");
        VI.put("STATUS_UPDATED", "Cập nhật trạng thái thành công");
        VI.put("RESOLVED", "Đã giải quyết");
        VI.put("EPOD_SUBMITTED", "Đã gửi ePOD thành công");
        VI.put("CUSTOMER_EXISTS", "Mã khách hàng đã tồn tại trong hệ thống");
        VI.put("VEHICLE_EXISTS", "Biển số xe đã tồn tại trong hệ thống");
        VI.put("VEHICLE_TYPE_EXISTS", "Mã loại phương tiện đã tồn tại");
        VI.put("CANNOT_HARD_DELETE_CUSTOMER", "Không thể xoá cứng: khách hàng còn yêu cầu điều xe liên quan. Dùng soft-delete.");
        VI.put("DISPATCH_NOT_ELIGIBLE", "Không đủ điều kiện điều phối");
        VI.put("INSUFFICIENT_ROLE", "Vai trò không đủ");
        VI.put("NO_PERMISSION_FOR_TRIP", "Không có quyền với chuyến này");
        VI.put("CAN_ONLY_UPDATE_OWN_INFO", "Chỉ được phép cập nhật thông tin của chính mình");
        VI.put("INVALID_ODOMETER_VALUE", "Số km mới không được nhỏ hơn số km hiện tại");
        VI.put("PARENT_ORG_NOT_ACTIVE", "Tổ chức cha không tồn tại hoặc không active");

        // EN
        EN.put("CONSTRAINT_VIOLATIONS", "SQL constraint violation.");
        EN.put("VALIDATION_FAILED", "Validation failed.");
        EN.put("MALFORMED_JSON_REQUEST", "Malformed JSON request.");
        EN.put("METHOD_NOT_ALLOWED", "Method not allowed.");
        EN.put("ACCESS_DENIED", "Access is denied.");
        EN.put("UNAUTHORIZED", "Unauthorized.");
        EN.put("RESOURCE_NOT_FOUND", "Resource not found.");
        EN.put("DATA_INTEGRITY_VIOLATION", "Data integrity violation.");
        EN.put("INTERNAL_SERVER_ERROR", "Internal server error.");
        EN.put("SUCCESS", "Success");
        EN.put("INVALID_CREDENTIALS", "Invalid username or password");
        EN.put("ACCOUNT_LOCKED", "Account is locked");
        EN.put("LOGIN_SUCCESS", "Login successful");
        EN.put("REFRESH_TOKEN_EXPIRED", "Refresh token has expired");
        EN.put("REFRESH_TOKEN_INVALID", "Invalid or revoked refresh token");
        EN.put("INVALID_TOKEN", "Invalid token");
        EN.put("REFRESH_SUCCESS", "Refresh successful");
        EN.put("LOGOUT_SUCCESS", "Logout successful");
        EN.put("NOT_FOUND", "Not found");
        EN.put("CREATED", "Created successfully");
        EN.put("OVERRIDDEN", "Overridden successfully");
        EN.put("UPDATED", "Updated successfully");
        EN.put("DELETED", "Deleted successfully");
        EN.put("STATUS_UPDATED", "Status updated successfully");
        EN.put("RESOLVED", "Resolved");
        EN.put("EPOD_SUBMITTED", "ePOD submitted successfully");
        EN.put("CUSTOMER_EXISTS", "Customer already exists");
        EN.put("VEHICLE_EXISTS", "Vehicle already exists");
        EN.put("VEHICLE_TYPE_EXISTS", "Vehicle type already exists");
        EN.put("CANNOT_HARD_DELETE_CUSTOMER", "Cannot hard delete: customer has related dispatch requests. Use soft-delete.");
        EN.put("DISPATCH_NOT_ELIGIBLE", "Not eligible for dispatch");
        EN.put("INSUFFICIENT_ROLE", "Insufficient role");
        EN.put("NO_PERMISSION_FOR_TRIP", "No permission for this trip");
        EN.put("CAN_ONLY_UPDATE_OWN_INFO", "Can only update own info");
        EN.put("INVALID_ODOMETER_VALUE", "New odometer value cannot be less than current odometer");
        EN.put("PARENT_ORG_NOT_ACTIVE", "Parent organization does not exist or is inactive");

        // LO
        LO.put("CONSTRAINT_VIOLATIONS", "ການລະເມີດຂໍ້ຈຳກັດ SQL.");
        LO.put("VALIDATION_FAILED", "ການກວດສອບລົ້ມເຫລວ.");
        LO.put("MALFORMED_JSON_REQUEST", "ຮູບແບບ JSON ບໍ່ຖືກຕ້ອງ.");
        LO.put("METHOD_NOT_ALLOWED", "ວິທີການບໍ່ຖືກອະນຸຍາດ.");
        LO.put("ACCESS_DENIED", "ການເຂົ້າເຖິງຖືກປະຕິເສດ.");
        LO.put("UNAUTHORIZED", "ບໍ່ໄດ້ຮັບອະນຸຍາດ.");
        LO.put("RESOURCE_NOT_FOUND", "ບໍ່ພົບຂໍ້ມູນ.");
        LO.put("DATA_INTEGRITY_VIOLATION", "ການລະເມີດຄວາມສົມບູນຂອງຂໍ້ມູນ.");
        LO.put("INTERNAL_SERVER_ERROR", "ຂໍ້ຜິດພາດຂອງເຊີບເວີພາຍໃນ.");
        LO.put("SUCCESS", "ສຳເລັດ");
        LO.put("INVALID_CREDENTIALS", "ຊື່ຜູ້ໃຊ້ ຫຼືລະຫັດຜ່ານບໍ່ຖືກຕ້ອງ");
        LO.put("ACCOUNT_LOCKED", "ບັນຊີຖືກລັອກ");
        LO.put("LOGIN_SUCCESS", "ເຂົ້າສູ່ລະບົບສຳເລັດ");
        LO.put("REFRESH_TOKEN_EXPIRED", "Refresh token ໝົດອາຍຸ");
        LO.put("REFRESH_TOKEN_INVALID", "Refresh token ບໍ່ຖືກຕ້ອງ ຫຼືຖືກຖອນຄືນ");
        LO.put("INVALID_TOKEN", "Token ບໍ່ຖືກຕ້ອງ");
        LO.put("REFRESH_SUCCESS", "ໂຫຼດໃໝ່ສຳເລັດ");
        LO.put("LOGOUT_SUCCESS", "ອອກຈາກລະບົບສຳເລັດ");
        LO.put("NOT_FOUND", "ບໍ່ພົບ");
        LO.put("CREATED", "ສ້າງສຳເລັດແລ້ວ");
        LO.put("OVERRIDDEN", "ທັບຊ້ອນສຳເລັດແລ້ວ");
        LO.put("UPDATED", "ອັບເດດສຳເລັດແລ້ວ");
        LO.put("DELETED", "ລຶບສຳເລັດແລ້ວ");
        LO.put("STATUS_UPDATED", "ອັບເດດສະຖານະສຳເລັດແລ້ວ");
        LO.put("RESOLVED", "ແກ້ໄຂແລ້ວ");
        LO.put("EPOD_SUBMITTED", "ສົ່ງ ePOD ສຳເລັດແລ້ວ");
        LO.put("CUSTOMER_EXISTS", "ລູກຄ້າທີມີລະຫັດນີ້ມີຢູ່ແລ້ວ");
        LO.put("VEHICLE_EXISTS", "ລົດທະບຽນນີ້ມີຢູ່ແລ້ວ");
        LO.put("VEHICLE_TYPE_EXISTS", "ປະເພດຍານພາຫະນະນີ້ມີຢູ່ແລ້ວ");
        LO.put("CANNOT_HARD_DELETE_CUSTOMER", "ບໍ່ສາມາດລຶບຖາວອນ: ລູກຄ້າຍັງມີຄຳຂໍໃຫ້ສົ່ງລົດ. ໃຊ້ soft-delete.");
        LO.put("DISPATCH_NOT_ELIGIBLE", "ບໍ່ມີເງື່ອນໄຂສົ່ງລົດ");
        LO.put("INSUFFICIENT_ROLE", "ບົດບາດບໍ່ພຽງພໍ");
        LO.put("NO_PERMISSION_FOR_TRIP", "ບໍ່ມີສິດໃນການເດີນທາງນີ້");
        LO.put("CAN_ONLY_UPDATE_OWN_INFO", "ອະນຸຍາດໃຫ້ນັບປັບປຸງຂໍ້ມູນຕົນເອງເທົ່ານັ້ນ");
        LO.put("INVALID_ODOMETER_VALUE", "ຕົວເລກກິໂລແມັດໃໝ່ຕ້ອງບໍ່ນ້ອຍກວ່າຕົວເລກປັດຈຸບັນ");
        LO.put("PARENT_ORG_NOT_ACTIVE", "ອົງການແມ່ບໍ່ມີຢູ່ ຫຼືບໍ່ເຄື່ອນໄຫວ");
    }
}

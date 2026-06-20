package com.unitel.fms.backend.constants.enums;

public interface RoleType {
    String ALL             = "ALL";              // Bất cứ role nào cũng có thể dùng api này
    String SYSTEM_ADMIN    = "SYSTEM_ADMIN";     // Quản trị hệ thống
    String DISPATCHER      = "DISPATCHER";       // Điều phối viên
    String OPS_MANAGER     = "OPS_MANAGER";      // Quản lý vận hành / điều hành
    String FLEET_MANAGER   = "FLEET_MANAGER";    // Quản lý phương tiện & kỹ thuật
    String HR_LEGAL        = "HR_LEGAL";         // Nhân sự / pháp chế
    String DRIVER          = "DRIVER";           // Tài xế
    String PARTNER_MANAGER = "PARTNER_MANAGER";  // Quản lý đối tác
}
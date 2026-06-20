package com.unitel.fms.backend.constants.enums;

public interface EntityType {

    // Module: File Attachment
    String FILE_ATTACHMENT = "file_attachments";

    // Module: Tổ chức & Phân quyền
    String ORGANIZATION = "organizations";
    String ROLE = "roles";
    String USER = "users";
    String USER_ROLE = "user_roles";
    String AUDIT_LOG = "audit_logs";
    String SYSTEM_CONFIG = "system_configs";
    String NOTIFICATION = "notifications";

    // Module: Phương tiện
    String VEHICLE_TYPE = "vehicle_types";
    String VEHICLE = "vehicles";
    String VEHICLE_DOCUMENT = "vehicle_documents";
    String VEHICLE_HEALTH = "vehicle_healths";
    String MAINTENANCE_ORDER = "maintenance_orders";
    String MAINTENANCE_ITEM = "maintenance_items";

    // Module: Tài xế
    String DRIVER = "drivers";
    String DRIVER_DOCUMENT = "driver_documents";

    // Module: Khách hàng
    String CUSTOMER = "customers";

    // Module: Điều xe & Chuyến
    String DISPATCH_REQUEST = "dispatch_requests";
    String DISPATCH_ASSIGNMENT = "dispatch_assignments";
    String TRIP = "trips";
    String TRIP_STOP = "trip_stops";
    String TRIP_INCIDENT = "trip_incidents";
    String FUEL_LOG = "fuel_logs";

    // Module: GPS & Telemetry
    String GPS_POSITION = "gps_positions";
    String GPS_ALERT = "gps_alerts";
    String GEOFENCE = "geofences";

    // Module: Đối tác vận tải
    String PARTNER = "partners";
    String PARTNER_VEHICLE = "partner_vehicles";
    String PARTNER_DRIVER = "partner_drivers";
    String PARTNER_PERFORMANCE = "partner_performances";
}
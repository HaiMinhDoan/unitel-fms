package com.unitel.fms.backend.controllers;

import com.unitel.fms.backend.services.impl.entity.AuditLogService;
import com.unitel.fms.backend.services.impl.entity.CustomerService;
import com.unitel.fms.backend.services.impl.entity.DispatchAssignmentService;
import com.unitel.fms.backend.services.impl.entity.DispatchRequestService;
import com.unitel.fms.backend.services.impl.entity.DriverDocumentService;
import com.unitel.fms.backend.services.impl.entity.DriverService;
import com.unitel.fms.backend.services.impl.entity.FileAttachmentService;
import com.unitel.fms.backend.services.impl.entity.FuelLogService;
import com.unitel.fms.backend.services.impl.entity.GeofenceService;
import com.unitel.fms.backend.services.impl.entity.GpsAlertService;
import com.unitel.fms.backend.services.impl.entity.GpsPositionService;
import com.unitel.fms.backend.services.impl.entity.MaintenanceItemService;
import com.unitel.fms.backend.services.impl.entity.MaintenanceOrderService;
import com.unitel.fms.backend.services.impl.entity.NotificationService;
import com.unitel.fms.backend.services.impl.entity.OrganizationService;
import com.unitel.fms.backend.services.impl.entity.PartnerDriverService;
import com.unitel.fms.backend.services.impl.entity.PartnerPerformanceService;
import com.unitel.fms.backend.services.impl.entity.PartnerService;
import com.unitel.fms.backend.services.impl.entity.PartnerVehicleService;
import com.unitel.fms.backend.services.impl.entity.RoleService;
import com.unitel.fms.backend.services.impl.entity.SystemConfigService;
import com.unitel.fms.backend.services.impl.entity.TripIncidentService;
import com.unitel.fms.backend.services.impl.entity.TripService;
import com.unitel.fms.backend.services.impl.entity.TripStopService;
import com.unitel.fms.backend.services.impl.entity.UserRoleService;
import com.unitel.fms.backend.services.impl.entity.UserService;
import com.unitel.fms.backend.services.impl.entity.VehicleDocumentService;
import com.unitel.fms.backend.services.impl.entity.VehicleHealthService;
import com.unitel.fms.backend.services.impl.entity.VehicleService;
import com.unitel.fms.backend.services.impl.entity.VehicleTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/common")
public class CommonController {

    @Autowired
    private FileAttachmentService fileAttachmentService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private VehicleTypeService vehicleTypeService;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private VehicleDocumentService vehicleDocumentService;

    @Autowired
    private VehicleHealthService vehicleHealthService;

    @Autowired
    private MaintenanceOrderService maintenanceOrderService;

    @Autowired
    private MaintenanceItemService maintenanceItemService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private DriverDocumentService driverDocumentService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private DispatchRequestService dispatchRequestService;

    @Autowired
    private DispatchAssignmentService dispatchAssignmentService;

    @Autowired
    private TripService tripService;

    @Autowired
    private TripStopService tripStopService;

    @Autowired
    private TripIncidentService tripIncidentService;

    @Autowired
    private FuelLogService fuelLogService;

    @Autowired
    private GpsPositionService gpsPositionService;

    @Autowired
    private GpsAlertService gpsAlertService;

    @Autowired
    private GeofenceService geofenceService;

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private PartnerVehicleService partnerVehicleService;

    @Autowired
    private PartnerDriverService partnerDriverService;

    @Autowired
    private PartnerPerformanceService partnerPerformanceService;
}
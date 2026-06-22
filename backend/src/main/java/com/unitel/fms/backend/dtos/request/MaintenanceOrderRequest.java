package com.unitel.fms.backend.dtos.request;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class MaintenanceOrderRequest {
    private UUID vehicleId;
    private UUID createdById;
    private String orderType;
    private LocalDate scheduledDate;
    private LocalDate completedDate;
    private BigDecimal totalCost;
    private String notes;
    private String status;
}

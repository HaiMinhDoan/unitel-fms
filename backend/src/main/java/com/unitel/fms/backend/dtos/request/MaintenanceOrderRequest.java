package com.unitel.fms.backend.dtos.request;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class MaintenanceOrderRequest {
    @NotNull(message = "vehicleId is required")
    private java.util.UUID vehicleId;
    @NotNull(message = "createdById is required")
    private java.util.UUID createdById;
    private String orderType;
    private LocalDate scheduledDate;
    private LocalDate completedDate;
    private BigDecimal totalCost;
    private String notes;
    private String status;
}

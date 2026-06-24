package com.unitel.fms.backend.dtos.request;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class MaintenanceItemRequest {
    @NotNull(message = "maintenanceOrderId is required")
    private java.util.UUID maintenanceOrderId;
    private String itemName;
    private String partCode;
    private Short quantity;
    private BigDecimal unitCost;
    private BigDecimal totalCost;
    private String status;
}

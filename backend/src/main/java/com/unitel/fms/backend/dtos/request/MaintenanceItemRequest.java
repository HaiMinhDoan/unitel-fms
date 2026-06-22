package com.unitel.fms.backend.dtos.request;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class MaintenanceItemRequest {
    private UUID maintenanceOrderId;
    private String itemName;
    private String partCode;
    private Short quantity;
    private BigDecimal unitCost;
    private BigDecimal totalCost;
    private String status;
}

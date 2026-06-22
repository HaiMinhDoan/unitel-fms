package com.unitel.fms.backend.dtos.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class MaintenanceItemResponse {
    private UUID id;
    private UUID maintenanceOrderId;
    private String itemName;
    private String partCode;
    private Short quantity;
    private BigDecimal unitCost;
    private BigDecimal totalCost;
    private String status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

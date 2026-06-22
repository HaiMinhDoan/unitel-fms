package com.unitel.fms.backend.dtos.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class MaintenanceOrderResponse {
    private UUID id;
    private UUID vehicleId;
    private UUID createdById;
    private String orderType;
    private LocalDate scheduledDate;
    private LocalDate completedDate;
    private BigDecimal totalCost;
    private String notes;
    private String status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

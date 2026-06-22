package com.unitel.fms.backend.dtos.response;

import lombok.Data;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class VehicleHealthResponse {
    private UUID id;
    private UUID vehicleId;
    private Short healthScore;
    private String riskLevel;
    private Map<String, Object> componentScores;
    private Boolean isDispatchBlocked;
    private String notes;
    private UUID assessedById;
    private String status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

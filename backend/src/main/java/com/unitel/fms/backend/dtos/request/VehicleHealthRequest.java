package com.unitel.fms.backend.dtos.request;

import lombok.Data;
import java.util.Map;
import java.util.UUID;

@Data
public class VehicleHealthRequest {
    private UUID vehicleId;
    private Short healthScore;
    private String riskLevel;
    private Map<String, Object> componentScores;
    private Boolean isDispatchBlocked;
    private String notes;
    private UUID assessedById;
    private String status;
}

package com.unitel.fms.backend.dtos.request;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;

@Data
public class VehicleHealthRequest {
    @NotNull(message = "vehicleId is required")
    private java.util.UUID vehicleId;
    private Short healthScore;
    private String riskLevel;
    private Map<String, Object> componentScores;
    private Boolean isDispatchBlocked;
    private String notes;
    @NotNull(message = "assessedById is required")
    private java.util.UUID assessedById;
    private String status;
}

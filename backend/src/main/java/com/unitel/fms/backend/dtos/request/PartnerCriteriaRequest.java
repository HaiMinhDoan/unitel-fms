package com.unitel.fms.backend.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerCriteriaRequest {
    @NotNull(message = "orgId is required")
    private java.util.UUID orgId;
    private BigDecimal requiredLoadCapacity;
    private String requiredVehicleType;
    private BigDecimal minOnTimeRate;
    private String minRating;
}

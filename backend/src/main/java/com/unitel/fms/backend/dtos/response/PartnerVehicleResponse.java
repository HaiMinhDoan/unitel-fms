package com.unitel.fms.backend.dtos.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerVehicleResponse {
    private UUID id;
    private UUID partnerId;
    private UUID vehicleTypeId;
    private String vehicleTypeName;
    private String plateNumber;
    private BigDecimal loadCapacityTon;
    private String status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

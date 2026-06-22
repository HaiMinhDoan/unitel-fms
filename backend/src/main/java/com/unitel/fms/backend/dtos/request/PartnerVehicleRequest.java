package com.unitel.fms.backend.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerVehicleRequest {
    @NotNull(message = "partner_id is required")
    private UUID partnerId;

    @NotNull(message = "vehicle_type_id is required")
    private UUID vehicleTypeId;

    @NotBlank(message = "plate_number is required")
    @Size(max = 30)
    private String plateNumber;

    private BigDecimal loadCapacityTon;

    @Size(max = 50)
    private String status;
}

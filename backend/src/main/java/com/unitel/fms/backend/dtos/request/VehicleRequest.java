package com.unitel.fms.backend.dtos.request;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class VehicleRequest {
    @NotNull(message = "orgId is required")
    private java.util.UUID orgId;
    @NotNull(message = "vehicleTypeId is required")
    private java.util.UUID vehicleTypeId;
    @jakarta.validation.constraints.NotBlank(message = "plateNumber is required")
    private String plateNumber;
    private String brand;
    private String model;
    private Short manufactureYear;
    private String ownershipType;
    private BigDecimal loadCapacityTon;
    private BigDecimal currentOdometer;
    private String status;
}

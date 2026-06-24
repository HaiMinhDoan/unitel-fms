package com.unitel.fms.backend.dtos.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class VehicleResponse {
    private UUID id;
    private UUID orgId;
    private UUID vehicleTypeId;
    private String vehicleTypeName;
    private String plateNumber;
    private String brand;
    private String model;
    private Short manufactureYear;
    private String ownershipType;
    private BigDecimal loadCapacityTon;
    private BigDecimal currentOdometer;
    private String status;
    private OffsetDateTime statusChangedAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

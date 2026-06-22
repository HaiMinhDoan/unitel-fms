package com.unitel.fms.backend.dtos.request;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class VehicleRequest {
    private UUID orgId;
    private UUID vehicleTypeId;
    private String plateNumber;
    private String brand;
    private String model;
    private Short manufactureYear;
    private String ownershipType;
    private BigDecimal loadCapacityTon;
    private BigDecimal currentOdometer;
    private String status;
}

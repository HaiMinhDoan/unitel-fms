package com.unitel.fms.backend.dtos.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FuelLogResponse {
    UUID id;
    UUID vehicleId;
    UUID driverId;
    String fuelType;
    BigDecimal volumeLiters;
    BigDecimal totalCost;
    BigDecimal odometerReading;
    String location;
    OffsetDateTime filledAt;
    BigDecimal reportedConsumption;
    BigDecimal telemetryConsumption;
    Boolean anomalyFlagged;
    String anomalyNotes;
    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;
}

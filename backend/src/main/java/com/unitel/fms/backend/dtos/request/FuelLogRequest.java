package com.unitel.fms.backend.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class FuelLogRequest {

    @NotNull
    UUID vehicleId;

    UUID driverId;

    @NotNull
    @Size(max = 50)
    String fuelType;

    @NotNull
    BigDecimal volumeLiters;

    BigDecimal totalCost;

    BigDecimal odometerReading;

    @Size(max = 50)
    String location;

    @NotNull
    OffsetDateTime filledAt;

    BigDecimal reportedConsumption;
    BigDecimal telemetryConsumption;
    Boolean anomalyFlagged;
    String anomalyNotes;
    private java.util.List<org.springframework.web.multipart.MultipartFile> files;
}

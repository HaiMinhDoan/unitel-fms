package com.unitel.fms.backend.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GpsIngestRequest {

    @NotNull(message = "vehicleId is required")
    UUID vehicleId;

    @NotNull(message = "recordedAt is required")
    OffsetDateTime recordedAt;

    @NotNull(message = "lat is required")
    Double lat;

    @NotNull(message = "lng is required")
    Double lng;

    Float speedKmh;
    Float heading;
    Float altitude;
    Float accuracyM;
    String source;
    UUID tripId;
}

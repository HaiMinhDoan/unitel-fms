package com.unitel.fms.backend.dtos.response;

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
public class GpsPositionDto {
    UUID vehicleId;
    OffsetDateTime recordedAt;
    UUID tripId;
    Double lat;
    Double lng;
    Float speedKmh;
    Float heading;
    Float altitude;
    Float accuracyM;
    String source;
    String status;
}

package com.unitel.fms.backend.dtos.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GpsAlertDto {
    UUID id;
    UUID vehicleId;
    UUID tripId;
    String alertType;
    Map<String, Object> details;
    Boolean isAcknowledged;
    UUID acknowledgedBy;
    OffsetDateTime acknowledgedAt;
    OffsetDateTime triggeredAt;
    String status;
    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;
}

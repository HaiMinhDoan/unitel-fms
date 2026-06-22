package com.unitel.fms.backend.dtos.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GeofenceDto {
    UUID id;
    UUID orgId;
    String name;
    String fenceType;
    Map<String, Object> coordinates;
    BigDecimal radiusM;
    String status;
    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;
}

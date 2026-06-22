package com.unitel.fms.backend.dtos.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TripResponse {
    UUID id;
    UUID orgId;
    UUID dispatchAssignmentId;
    String tripNumber;
    OffsetDateTime startTime;
    OffsetDateTime endTime;
    Integer progressPct;
    String status;
    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;
}

package com.unitel.fms.backend.dtos.response;

import com.fasterxml.jackson.databind.JsonNode;
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
public class DispatchAssignmentResponse {
    UUID id;
    UUID orgId;
    UUID dispatchRequestId;
    String tripType;
    UUID vehicleId;
    UUID driverId;
    UUID partnerVehicleId;
    UUID partnerDriverId;
    OffsetDateTime assignedAt;
    Boolean preCheckPassed;
    java.util.Map<String, Object> preCheckDetails;
    String overrideReason;
    UUID assignedBy;
    UUID overrideBy;
    String status;
    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;
}

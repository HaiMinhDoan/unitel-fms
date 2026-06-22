package com.unitel.fms.backend.dtos.request;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DispatchAssignmentRequest {

    @NotNull
    UUID orgId;

    @NotNull
    UUID dispatchRequestId;

    String tripType;

    UUID vehicleId;

    UUID driverId;

    UUID partnerVehicleId;

    UUID partnerDriverId;

    OffsetDateTime assignedAt;

    Boolean preCheckPassed;

    Map<String, Object> preCheckDetails;

    String overrideReason;

    UUID assignedBy;
    UUID overrideBy;

    String status;
}

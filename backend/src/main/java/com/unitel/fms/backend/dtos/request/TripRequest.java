package com.unitel.fms.backend.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class TripRequest {

    @NotNull
    UUID orgId;

    @NotNull
    UUID dispatchAssignmentId;

    @NotNull
    @Size(max = 50)
    String tripNumber;

    OffsetDateTime startTime;
    OffsetDateTime endTime;

    Integer progressPct;

    @NotNull
    @Size(max = 50)
    String status;
}

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
public class TripIncidentRequest {

    @NotNull
    UUID tripId;

    @NotNull
    @Size(max = 50)
    String incidentType;

    @NotNull
    String description;

    @Size(max = 50)
    String severity;

    UUID reportedBy;
    OffsetDateTime reportedAt;

    @Size(max = 50)
    String status;

    String resolutionNotes;
    UUID resolvedBy;
    OffsetDateTime resolvedAt;
}

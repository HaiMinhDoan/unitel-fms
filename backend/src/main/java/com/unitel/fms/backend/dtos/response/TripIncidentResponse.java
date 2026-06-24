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
public class TripIncidentResponse {
    UUID id;
    UUID tripId;
    String incidentType;
    String description;
    String severity;
    UUID reportedBy;
    OffsetDateTime reportedAt;
    String status;
    String resolutionNotes;
    UUID resolvedBy;
    OffsetDateTime resolvedAt;
    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;
    java.util.List<FileAttachmentResponse> attachments;
}

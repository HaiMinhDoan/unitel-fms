package com.unitel.fms.backend.dtos.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DriverDocumentResponse {
    UUID id;
    UUID driverId;
    String docType;
    String docNumber;
    LocalDate issueDate;
    LocalDate expiryDate;
    String status;
    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;
}

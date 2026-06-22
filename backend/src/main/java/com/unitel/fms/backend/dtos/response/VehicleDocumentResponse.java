package com.unitel.fms.backend.dtos.response;

import lombok.Data;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class VehicleDocumentResponse {
    private UUID id;
    private UUID vehicleId;
    private String docType;
    private String docNumber;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

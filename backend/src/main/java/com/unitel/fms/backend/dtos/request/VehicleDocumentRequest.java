package com.unitel.fms.backend.dtos.request;

import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class VehicleDocumentRequest {
    private UUID vehicleId;
    private String docType;
    private String docNumber;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String status;
}

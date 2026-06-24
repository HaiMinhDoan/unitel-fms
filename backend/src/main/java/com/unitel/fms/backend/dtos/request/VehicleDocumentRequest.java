package com.unitel.fms.backend.dtos.request;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class VehicleDocumentRequest {
    @NotNull(message = "vehicleId is required")
    private java.util.UUID vehicleId;
    @jakarta.validation.constraints.NotBlank(message = "docType is required")
    private String docType;
    private String docNumber;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String status = "valid";
    private java.util.List<org.springframework.web.multipart.MultipartFile> files;
}

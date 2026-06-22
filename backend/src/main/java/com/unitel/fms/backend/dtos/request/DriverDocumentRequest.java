package com.unitel.fms.backend.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DriverDocumentRequest {
    @NotNull
    UUID driverId;

    @NotNull
    @Size(max = 50)
    String docType;

    @Size(max = 100)
    String docNumber;

    LocalDate issueDate;

    LocalDate expiryDate;
}

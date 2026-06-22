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
public class DriverResponse {
    UUID id;
    UUID orgId;
    UUID userId;
    String employeeCode;
    String fullName;
    String phone;
    String licenseClass;
    String licenseNumber;
    Boolean isDispatchEligible;
    String status;
    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;
}

package com.unitel.fms.backend.dtos.response;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerDriverResponse {
    private UUID id;
    private UUID partnerId;
    private String fullName;
    private String phone;
    private String licenseClass;
    private String licenseNumber;
    private String status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

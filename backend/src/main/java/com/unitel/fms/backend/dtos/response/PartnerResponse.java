package com.unitel.fms.backend.dtos.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerResponse {
    private UUID id;
    private UUID orgId;
    private String code;
    private String name;
    private String contactPerson;
    private String phone;
    private String email;
    private String address;
    private String rating;
    private BigDecimal onTimeRate;
    private String status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

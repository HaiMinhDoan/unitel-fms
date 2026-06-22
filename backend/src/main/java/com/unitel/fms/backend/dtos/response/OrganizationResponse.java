package com.unitel.fms.backend.dtos.response;

import lombok.Data;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class OrganizationResponse {
    private UUID id;
    private String code;
    private String nameEn;
    private String nameVi;
    private String nameLo;
    private String orgType;
    private UUID parentId;
    private String region;
    private String timezone;
    private String status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

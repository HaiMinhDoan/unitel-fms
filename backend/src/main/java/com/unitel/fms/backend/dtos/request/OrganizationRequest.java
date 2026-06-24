package com.unitel.fms.backend.dtos.request;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class OrganizationRequest {
    private String code;
    private String nameEn;
    private String nameVi;
    private String nameLo;
    private String orgType;
    @NotNull(message = "parentId is required")
    private java.util.UUID parentId;
    private String region;
    private String timezone;
    private String status;
}

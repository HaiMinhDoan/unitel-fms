package com.unitel.fms.backend.dtos.request;

import lombok.Data;
import java.util.UUID;

@Data
public class OrganizationRequest {
    private String code;
    private String nameEn;
    private String nameVi;
    private String nameLo;
    private String orgType;
    private UUID parentId;
    private String region;
    private String timezone;
    private String status;
}

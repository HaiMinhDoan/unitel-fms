package com.unitel.fms.backend.dtos.request;

import lombok.Data;

@Data
public class RoleRequest {
    private String code;
    private String name;
    private String description;
    private String status;
}

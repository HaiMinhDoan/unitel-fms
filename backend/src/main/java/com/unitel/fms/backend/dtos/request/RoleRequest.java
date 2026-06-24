package com.unitel.fms.backend.dtos.request;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class RoleRequest {
    private String code;
    @jakarta.validation.constraints.NotBlank(message = "name is required")
    private String name;
    private String description;
    private String status;
}

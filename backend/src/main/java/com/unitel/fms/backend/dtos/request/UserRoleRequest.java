package com.unitel.fms.backend.dtos.request;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class UserRoleRequest {
    @NotNull(message = "userId is required")
    private java.util.UUID userId;
    @NotNull(message = "roleId is required")
    private java.util.UUID roleId;
    @NotNull(message = "orgId is required")
    private java.util.UUID orgId;
    private String status;
}

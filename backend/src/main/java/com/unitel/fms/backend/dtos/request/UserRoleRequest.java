package com.unitel.fms.backend.dtos.request;

import lombok.Data;
import java.util.UUID;

@Data
public class UserRoleRequest {
    private UUID userId;
    private UUID roleId;
    private UUID orgId;
    private String status;
}

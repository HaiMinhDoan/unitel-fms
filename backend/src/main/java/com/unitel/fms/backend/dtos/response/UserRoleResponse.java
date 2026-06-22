package com.unitel.fms.backend.dtos.response;

import lombok.Data;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class UserRoleResponse {
    private UUID id;
    private UUID userId;
    private UUID roleId;
    private UUID orgId;
    private UUID grantedById;
    private String status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

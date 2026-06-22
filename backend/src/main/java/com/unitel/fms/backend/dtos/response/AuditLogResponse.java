package com.unitel.fms.backend.dtos.response;

import lombok.Data;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class AuditLogResponse {
    private UUID id;
    private UUID userId;
    private String action;
    private String entityType;
    private UUID entityId;
    private Map<String, Object> oldValues;
    private Map<String, Object> newValues;
    private String ipAddress;
    private String status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

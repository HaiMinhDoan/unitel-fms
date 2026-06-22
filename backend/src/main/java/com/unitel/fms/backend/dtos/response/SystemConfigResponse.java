package com.unitel.fms.backend.dtos.response;

import lombok.Data;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class SystemConfigResponse {
    private UUID id;
    private String configKey;
    private Map<String, Object> configValue;
    private String description;
    private UUID updatedById;
    private String status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

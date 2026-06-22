package com.unitel.fms.backend.dtos.response;

import lombok.Data;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class NotificationResponse {
    private UUID id;
    private UUID userId;
    private String title;
    private String body;
    private String channel;
    private String entityType;
    private UUID entityId;
    private Boolean isRead;
    private String status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

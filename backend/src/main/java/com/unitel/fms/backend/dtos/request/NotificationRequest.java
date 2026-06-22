package com.unitel.fms.backend.dtos.request;

import lombok.Data;
import java.util.UUID;

@Data
public class NotificationRequest {
    private String title;
    private String body;
    private String channel;
    private String entityType;
    private UUID entityId;
    private UUID userId;
}

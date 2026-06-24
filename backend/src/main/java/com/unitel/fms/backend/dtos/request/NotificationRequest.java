package com.unitel.fms.backend.dtos.request;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class NotificationRequest {
    private String title;
    private String body;
    private String channel;
    private String entityType;
    @NotNull(message = "entityId is required")
    private java.util.UUID entityId;
    @NotNull(message = "userId is required")
    private java.util.UUID userId;
}

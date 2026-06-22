package com.unitel.fms.backend.dtos.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileAttachmentResponse {
    UUID id;
    String bucket;
    String objectKey;
    String originalName;
    String mimeType;
    String extension;
    Long sizeBytes;
    String entityType;
    UUID entityId;
    UUID uploadedBy;
    String status;
    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;
    String publicUrl;
}

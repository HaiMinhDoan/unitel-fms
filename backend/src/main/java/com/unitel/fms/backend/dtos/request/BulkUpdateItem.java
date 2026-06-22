package com.unitel.fms.backend.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BulkUpdateItem {
    @NotNull
    UUID id;

    @NotNull
    Map<String, Object> fields;
}

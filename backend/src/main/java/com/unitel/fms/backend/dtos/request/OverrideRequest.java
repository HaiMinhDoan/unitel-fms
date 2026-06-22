package com.unitel.fms.backend.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OverrideRequest {
    @NotBlank(message = "Lý do override là bắt buộc")
    String overrideReason;
}

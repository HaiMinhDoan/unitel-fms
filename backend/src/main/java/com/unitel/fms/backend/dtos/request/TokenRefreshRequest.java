package com.unitel.fms.backend.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TokenRefreshRequest {
    @NotBlank(message = "Refresh Token is required")
    String refreshToken;
}

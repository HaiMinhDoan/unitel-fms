package com.unitel.fms.backend.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerDriverRequest {
    @NotNull(message = "partner_id is required")
    @NotNull(message = "partnerId is required")
    private java.util.UUID partnerId;

    @NotBlank(message = "full_name is required")
    @Size(max = 255)
    private String fullName;

    @Size(max = 30)
    private String phone;

    @Size(max = 20)
    private String licenseClass;

    @Size(max = 50)
    private String licenseNumber;

    @Size(max = 50)
    private String status;
}

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
public class PartnerRequest {
    @NotNull(message = "org_id is required")
    @NotNull(message = "orgId is required")
    private java.util.UUID orgId;

    @NotBlank(message = "code is required")
    @Size(max = 50)
    private String code;

    @NotBlank(message = "name is required")
    @Size(max = 255)
    @jakarta.validation.constraints.NotBlank(message = "name is required")
    private String name;

    @Size(max = 255)
    private String contactPerson;

    @Size(max = 30)
    private String phone;

    @Size(max = 255)
    private String email;

    private String address;

    @Size(max = 50)
    private String status;
}

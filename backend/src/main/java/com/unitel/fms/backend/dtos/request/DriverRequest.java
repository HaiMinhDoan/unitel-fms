package com.unitel.fms.backend.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DriverRequest {

    @NotNull
    UUID orgId;

    UUID userId;

    @Size(max = 50)
    String employeeCode;

    @NotNull
    @Size(max = 255)
    String fullName;

    @Size(max = 30)
    String phone;

    @Size(max = 20)
    String licenseClass;

    @Size(max = 50)
    String licenseNumber;

    @Builder.Default
    Boolean isDispatchEligible = true;
}

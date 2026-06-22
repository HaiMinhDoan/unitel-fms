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
public class CustomerRequest {

    @NotNull
    UUID orgId;

    @NotNull
    @Size(max = 50)
    String code;

    @NotNull
    @Size(max = 255)
    String name;

    @Size(max = 255)
    String contactPerson;

    @Size(max = 30)
    String phone;

    @Size(max = 255)
    String email;

    String address;

}

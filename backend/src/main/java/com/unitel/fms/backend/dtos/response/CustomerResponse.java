package com.unitel.fms.backend.dtos.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerResponse {
    UUID id;
    UUID orgId;
    String code;
    String name;
    String contactPerson;
    String phone;
    String email;
    String address;
    String status;
    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;
}

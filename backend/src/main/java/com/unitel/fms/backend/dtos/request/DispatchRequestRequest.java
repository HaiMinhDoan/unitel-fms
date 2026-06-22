package com.unitel.fms.backend.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DispatchRequestRequest {

    @NotNull
    UUID orgId;

    UUID customerId;

    String sourceChannel;

    @NotNull
    String originAddress;

    BigDecimal originLat;
    BigDecimal originLng;

    @NotNull
    String destAddress;

    BigDecimal destLat;
    BigDecimal destLng;

    BigDecimal cargoWeightTon;

    @Size(max = 100)
    String cargoType;

    String cargoNotes;

    @NotNull
    @Size(max = 20)
    String priority;

    OffsetDateTime requestedPickupAt;
    OffsetDateTime requestedDeliveryAt;

    String status;
}

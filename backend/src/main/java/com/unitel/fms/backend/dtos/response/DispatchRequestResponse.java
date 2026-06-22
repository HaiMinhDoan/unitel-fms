package com.unitel.fms.backend.dtos.response;

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
public class DispatchRequestResponse {
    UUID id;
    UUID orgId;
    UUID customerId;
    UUID createdBy;
    String reqNumber;
    String sourceChannel;
    String originAddress;
    BigDecimal originLat;
    BigDecimal originLng;
    String destAddress;
    BigDecimal destLat;
    BigDecimal destLng;
    BigDecimal cargoWeightTon;
    String cargoType;
    String cargoNotes;
    String priority;
    OffsetDateTime requestedPickupAt;
    OffsetDateTime requestedDeliveryAt;
    String status;
    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;
}

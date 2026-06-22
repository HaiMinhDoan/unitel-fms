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
public class TripStopResponse {
    UUID id;
    UUID tripId;
    String stopType;
    Integer stopOrder;
    String address;
    BigDecimal lat;
    BigDecimal lng;
    OffsetDateTime plannedArrival;
    OffsetDateTime actualArrival;
    OffsetDateTime plannedDeparture;
    OffsetDateTime actualDeparture;
    String status;
    String notes;
    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;
}

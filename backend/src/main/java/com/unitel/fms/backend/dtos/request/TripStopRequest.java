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
public class TripStopRequest {

    @NotNull
    UUID tripId;

    @NotNull
    @Size(max = 20)
    String stopType;

    @NotNull
    Integer stopOrder;

    @NotNull
    String address;

    BigDecimal lat;
    BigDecimal lng;

    OffsetDateTime plannedArrival;
    OffsetDateTime actualArrival;

    OffsetDateTime plannedDeparture;
    OffsetDateTime actualDeparture;

    @Size(max = 50)
    String status;

    String notes;
}

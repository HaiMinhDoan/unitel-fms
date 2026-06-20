package com.unitel.fms.backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
@Embeddable
public class GpsPositionId implements Serializable {

    @Column(name = "vehicle_id", nullable = false)
    private UUID vehicleId;

    @Column(name = "recorded_at", nullable = false)
    private OffsetDateTime recordedAt;
}
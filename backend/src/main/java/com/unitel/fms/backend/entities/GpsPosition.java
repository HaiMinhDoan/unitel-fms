package com.unitel.fms.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "gps_positions")
public class GpsPosition {

    @EmbeddedId
    private GpsPositionId id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false, insertable = false, updatable = false)
    private Vehicle vehicle;

    @NotNull
    @Column(name = "recorded_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime recordedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @NotNull
    @Column(name = "lat", nullable = false)
    private Double lat;

    @NotNull
    @Column(name = "lng", nullable = false)
    private Double lng;

    @Column(name = "speed_kmh")
    private Float speedKmh;

    @Column(name = "heading")
    private Float heading;

    @Column(name = "altitude")
    private Float altitude;

    @Column(name = "accuracy_m")
    private Float accuracyM;

    @Size(max = 20)
    @NotNull
    @ColumnDefault("'device'")
    @Column(name = "source", nullable = false, length = 20)
    private String source;

    @Size(max = 50)
    @NotNull
    @ColumnDefault("'received'")
    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;


}
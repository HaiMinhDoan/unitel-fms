package com.unitel.fms.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "fuel_logs")
public class FuelLog{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "logged_by")
    private User loggedBy;

    @NotNull
    @Column(name = "liters_added", nullable = false, precision = 8, scale = 2)
    private BigDecimal litersAdded;

    @Column(name = "fuel_cost", precision = 15, scale = 2)
    private BigDecimal fuelCost;

    @Column(name = "odometer_at_fill", precision = 10, scale = 2)
    private BigDecimal odometerAtFill;

    @Column(name = "reported_consumption", precision = 8, scale = 2)
    private BigDecimal reportedConsumption;

    @Column(name = "telemetry_consumption", precision = 8, scale = 2)
    private BigDecimal telemetryConsumption;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "anomaly_flagged", nullable = false)
    @Builder.Default
    private Boolean anomalyFlagged = false;

    @Column(name = "anomaly_notes", length = Integer.MAX_VALUE)
    private String anomalyNotes;

    @Size(max = 50)
    @NotNull
    @ColumnDefault("'recorded'")
    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private String status = "recorded";

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private OffsetDateTime updatedAt = OffsetDateTime.now();


}
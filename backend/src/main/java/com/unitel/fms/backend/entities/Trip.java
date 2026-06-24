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
@Table(name = "trips")
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dispatch_assignment_id", nullable = false)
    private DispatchAssignment dispatchAssignment;

    @Size(max = 50)
    @NotNull
    @Column(name = "trip_number", nullable = false, length = 50)
    private String tripNumber;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "progress_pct", nullable = false)
    private Short progressPct;

    @Column(name = "planned_start_at")
    private OffsetDateTime plannedStartAt;

    @Column(name = "planned_end_at")
    private OffsetDateTime plannedEndAt;

    @Column(name = "actual_start_at")
    private OffsetDateTime actualStartAt;

    @Column(name = "actual_end_at")
    private OffsetDateTime actualEndAt;

    @Column(name = "eta")
    private OffsetDateTime eta;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "sla_at_risk", nullable = false)
    private Boolean slaAtRisk;

    @Column(name = "total_distance_km", precision = 10, scale = 2)
    private BigDecimal totalDistanceKm;

    @Column(name = "total_cost", precision = 15, scale = 2)
    private BigDecimal totalCost;

    @Size(max = 50)
    @NotNull
    @ColumnDefault("'pending'")
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
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
@Table(name = "partner_performances")
public class PartnerPerformance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "partner_id", nullable = false)
    private Partner partner;

    @Size(max = 7)
    @NotNull
    @Column(name = "period_month", nullable = false, length = 7)
    private String periodMonth;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "total_trips", nullable = false)
    private Integer totalTrips;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "on_time_trips", nullable = false)
    private Integer onTimeTrips;

    @Column(name = "avg_cost", precision = 15, scale = 2)
    private BigDecimal avgCost;

    @Column(name = "total_revenue", precision = 15, scale = 2)
    private BigDecimal totalRevenue;

    @Column(name = "notes", length = Integer.MAX_VALUE)
    private String notes;

    @Size(max = 50)
    @NotNull
    @ColumnDefault("'final'")
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
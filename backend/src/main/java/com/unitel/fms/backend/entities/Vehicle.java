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
@Table(name = "vehicles")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "org_id", nullable = false)
    private Organization org;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_type_id", nullable = false)
    private VehicleType vehicleType;

    @Size(max = 30)
    @NotNull
    @Column(name = "plate_number", nullable = false, length = 30)
    private String plateNumber;

    @Size(max = 100)
    @Column(name = "brand", length = 100)
    private String brand;

    @Size(max = 100)
    @Column(name = "model", length = 100)
    private String model;

    @Column(name = "manufacture_year")
    private Short manufactureYear;

    @Size(max = 30)
    @NotNull
    @ColumnDefault("'owned'")
    @Column(name = "ownership_type", nullable = false, length = 30)
    private String ownershipType;

    @Column(name = "load_capacity_ton", precision = 8, scale = 2)
    private BigDecimal loadCapacityTon;

    @ColumnDefault("0")
    @Column(name = "current_odometer", precision = 10, scale = 2)
    private BigDecimal currentOdometer;

    @Column(name = "last_known_lat", precision = 10, scale = 7)
    private BigDecimal lastKnownLat;

    @Column(name = "last_known_lng", precision = 10, scale = 7)
    private BigDecimal lastKnownLng;

    @Size(max = 50)
    @NotNull
    @ColumnDefault("'active'")
    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "status_changed_at")
    private OffsetDateTime statusChangedAt;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

}
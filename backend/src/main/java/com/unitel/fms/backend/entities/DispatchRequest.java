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
@Table(name = "dispatch_requests")
public class DispatchRequest{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "org_id", nullable = false)
    private Organization org;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Size(max = 50)
    @NotNull
    @Column(name = "req_number", nullable = false, length = 50)
    private String reqNumber;

    @Size(max = 50)
    @NotNull
    @ColumnDefault("'manual'")
    @Column(name = "source_channel", nullable = false, length = 50)
    private String sourceChannel;

    @NotNull
    @Column(name = "origin_address", nullable = false, length = Integer.MAX_VALUE)
    private String originAddress;

    @Column(name = "origin_lat", precision = 10, scale = 7)
    private BigDecimal originLat;

    @Column(name = "origin_lng", precision = 10, scale = 7)
    private BigDecimal originLng;

    @NotNull
    @Column(name = "dest_address", nullable = false, length = Integer.MAX_VALUE)
    private String destAddress;

    @Column(name = "dest_lat", precision = 10, scale = 7)
    private BigDecimal destLat;

    @Column(name = "dest_lng", precision = 10, scale = 7)
    private BigDecimal destLng;

    @Column(name = "cargo_weight_ton", precision = 8, scale = 2)
    private BigDecimal cargoWeightTon;

    @Size(max = 100)
    @Column(name = "cargo_type", length = 100)
    private String cargoType;

    @Column(name = "cargo_notes", length = Integer.MAX_VALUE)
    private String cargoNotes;

    @Size(max = 20)
    @NotNull
    @ColumnDefault("'medium'")
    @Column(name = "priority", nullable = false, length = 20)
    private String priority;

    @Column(name = "requested_pickup_at")
    private OffsetDateTime requestedPickupAt;

    @Column(name = "requested_delivery_at")
    private OffsetDateTime requestedDeliveryAt;

    @Size(max = 50)
    @NotNull
    @ColumnDefault("'new'")
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
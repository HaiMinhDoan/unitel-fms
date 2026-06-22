package com.unitel.fms.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "dispatch_assignments")
public class DispatchAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dispatch_request_id", nullable = false)
    private DispatchRequest dispatchRequest;

    @Size(max = 50)
    @NotNull
    @ColumnDefault("'internal'")
    @Column(name = "trip_type", nullable = false, length = 50)
    private String tripType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_vehicle_id")
    private PartnerVehicle partnerVehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_driver_id")
    private PartnerDriver partnerDriver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by")
    private User assignedBy;

    @Size(max = 30)
    @NotNull
    @ColumnDefault("'manual'")
    @Column(name = "assignment_type", nullable = false, length = 30)
    private String assignmentType;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "pre_check_passed", nullable = false)
    private Boolean preCheckPassed;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "pre_check_details")
    private Map<String, Object> preCheckDetails;

    @Column(name = "override_reason", length = Integer.MAX_VALUE)
    private String overrideReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "override_by")
    private User overrideBy;

    @Size(max = 50)
    @NotNull
    @ColumnDefault("'active'")
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
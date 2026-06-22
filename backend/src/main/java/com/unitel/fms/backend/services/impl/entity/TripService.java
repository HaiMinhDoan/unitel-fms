package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.Trip;
import com.unitel.fms.backend.repositories.TripRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unitel.fms.backend.contexts.SecurityContextHolder;
import com.unitel.fms.backend.dtos.AuthInfo;
import com.unitel.fms.backend.entities.DispatchAssignment;
import com.unitel.fms.backend.entities.Driver;
import com.unitel.fms.backend.entities.GpsPosition;
import com.unitel.fms.backend.constants.enums.RoleType;
import com.unitel.fms.backend.repositories.DispatchAssignmentRepository;
import com.unitel.fms.backend.repositories.DriverRepository;
import com.unitel.fms.backend.repositories.GpsPositionRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TripService extends BaseServiceImpl<Trip, UUID> {

    private final TripRepository tripRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private DispatchAssignmentRepository dispatchAssignmentRepository;

    @Autowired
    private DispatchAssignmentService dispatchAssignmentService;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private GpsPositionRepository gpsPositionRepository;

    public TripService(TripRepository repository) {
        super(repository);
        this.tripRepository = repository;
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    @Transactional
    public Trip createFromAssignment(DispatchAssignment assignment) {
        Trip trip = Trip.builder()
                .dispatchAssignment(assignment)
                .tripNumber(generateTripNumber())
                .progressPct((short) 0)
                .slaAtRisk(false)
                .status("pending")
                .build();
        return tripRepository.save(trip);
    }

    private String generateTripNumber() {
        return "TR-" + System.currentTimeMillis();
    }

    @Override
    @Transactional
    public Trip changeStatus(UUID id, String status) {
        AuthInfo authInfo = SecurityContextHolder.getAuthInfo();
        if (authInfo != null && authInfo.hasAnyRole(RoleType.DRIVER) && !authInfo.hasAnyRole(RoleType.DISPATCHER, RoleType.SYSTEM_ADMIN)) {
            Trip trip = getOne(id).orElseThrow(() -> new java.util.NoSuchElementException("Chuyến không tồn tại"));
            UUID driverIdOfTrip = trip.getDispatchAssignment().getDriver().getId();
            Driver currentDriver = driverRepository.findByUserId(authInfo.getId())
                    .orElseThrow(() -> new AccessDeniedException("Tài khoản chưa liên kết tài xế"));
            if (!currentDriver.getId().equals(driverIdOfTrip)) {
                throw new AccessDeniedException("NO_PERMISSION_FOR_TRIP");
            }
        }

        Trip updated = super.changeStatus(id, status);

        if ("completed".equals(status) || "cancelled".equals(status)) {
            dispatchAssignmentService.changeStatus(updated.getDispatchAssignment().getId(), "completed");
        }
        return updated;
    }

    public List<GpsPosition> replay(UUID tripId) {
        return gpsPositionRepository.findById_TripIdOrderById_RecordedAtAsc(tripId);
    }
}

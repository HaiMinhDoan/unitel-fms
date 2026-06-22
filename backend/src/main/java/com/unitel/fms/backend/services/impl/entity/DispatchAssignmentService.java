package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.DispatchAssignment;
import com.unitel.fms.backend.repositories.DispatchAssignmentRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unitel.fms.backend.contexts.SecurityContextHolder;
import com.unitel.fms.backend.dtos.response.DispatchEligibilityResult;
import com.unitel.fms.backend.dtos.response.DriverEligibilityResult;
import com.unitel.fms.backend.entities.DispatchRequest;
import com.unitel.fms.backend.entities.Vehicle;
import com.unitel.fms.backend.exceptions.customize.DispatchBlockedException;
import com.unitel.fms.backend.exceptions.customize.InvalidFieldException;
import com.unitel.fms.backend.constants.enums.EntityType;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import java.time.OffsetDateTime;

@Service
public class DispatchAssignmentService extends BaseServiceImpl<DispatchAssignment, UUID> {

    private final DispatchAssignmentRepository dispatchAssignmentRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private DispatchRequestService dispatchRequestService;

    @Autowired
    private TripService tripService;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private ObjectMapper objectMapper;

    public DispatchAssignmentService(DispatchAssignmentRepository repository) {
        super(repository);
        this.dispatchAssignmentRepository = repository;
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    @Override
    @Transactional
    public DispatchAssignment create(DispatchAssignment entity) {
        boolean isVehicleAvailable = dispatchAssignmentRepository.isVehicleAvailableForDispatch(
                entity.getVehicle().getId(),
                OffsetDateTime.now(),
                OffsetDateTime.now().plusDays(1));

        boolean isDriverAvailable = dispatchAssignmentRepository.isDriverAvailableForDispatch(
                entity.getDriver().getId(),
                OffsetDateTime.now(),
                OffsetDateTime.now().plusDays(1));

        Map<String, Object> preCheckDetails = Map.of(
                "vehicleAvailable", isVehicleAvailable,
                "driverAvailable", isDriverAvailable,
                "documentValid", true // Mocked
        );

        boolean allPassed = isVehicleAvailable && isDriverAvailable;

        entity.setPreCheckPassed(allPassed);
        entity.setPreCheckDetails(preCheckDetails);
        
        com.unitel.fms.backend.dtos.AuthInfo auth = SecurityContextHolder.getAuthInfo();
        if (auth != null && auth.getId() != null) {
            com.unitel.fms.backend.entities.User assignedBy = new com.unitel.fms.backend.entities.User();
            assignedBy.setId(auth.getId());
            entity.setAssignedBy(assignedBy);
        }

        if (!allPassed && (entity.getOverrideReason() == null || entity.getOverrideReason().isBlank())) {
            throw new DispatchBlockedException("DISPATCH_NOT_ELIGIBLE", preCheckDetails);
        }

        if (!allPassed) {
            com.unitel.fms.backend.entities.User overrideBy = new com.unitel.fms.backend.entities.User();
            if (auth != null && auth.getId() != null) overrideBy.setId(auth.getId());
            entity.setOverrideBy(overrideBy);
            auditLogService.record("OVERRIDE_DISPATCH", "DispatchAssignment", entity.getId(), null, entity);
        }

        entity.setStatus("active");
        DispatchAssignment saved = super.create(entity);

        tripService.createFromAssignment(saved);
        dispatchRequestService.changeStatus(entity.getDispatchRequest().getId(), "assigned");

        return saved;
    }

    @Transactional
    public DispatchAssignment applyOverride(UUID id, String overrideReason) {
        DispatchAssignment entity = getOne(id).orElseThrow();
        entity.setPreCheckPassed(true);
        entity.setOverrideReason(overrideReason);
        com.unitel.fms.backend.dtos.AuthInfo auth = SecurityContextHolder.getAuthInfo();
        if (auth != null && auth.getId() != null) {
            com.unitel.fms.backend.entities.User overrideBy = new com.unitel.fms.backend.entities.User();
            overrideBy.setId(auth.getId());
            entity.setOverrideBy(overrideBy);
        }

        auditLogService.record("OVERRIDE_DISPATCH", "DispatchAssignment", id, null, entity);
        return update(id, entity);
    }
}

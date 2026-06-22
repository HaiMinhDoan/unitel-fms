package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.VehicleHealth;
import com.unitel.fms.backend.repositories.VehicleHealthRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

import com.unitel.fms.backend.constants.enums.RiskLevel;
import com.unitel.fms.backend.contexts.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class VehicleHealthService extends BaseServiceImpl<VehicleHealth, UUID> {

    private final VehicleHealthRepository vehicleHealthRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private NotificationService notificationService;

    public VehicleHealthService(VehicleHealthRepository repository) {
        super(repository);
        this.vehicleHealthRepository = repository;
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    public Optional<VehicleHealth> getLatestHealth(UUID vehicleId) {
        return vehicleHealthRepository.findFirstByVehicleIdOrderByCreatedAtDesc(vehicleId);
    }

    @Override
    @Transactional
    public VehicleHealth create(VehicleHealth entity) {
        RiskLevel level = RiskLevel.fromScore(entity.getHealthScore());
        entity.setRiskLevel(level.name());
        entity.setIsDispatchBlocked(level == RiskLevel.CRITICAL);
        if (SecurityContextHolder.getAuthInfo() != null) {
            com.unitel.fms.backend.entities.User user = entityManager.getReference(
                com.unitel.fms.backend.entities.User.class, 
                SecurityContextHolder.getAuthInfo().getId()
            );
            entity.setAssessedBy(user);
        }

        VehicleHealth saved = super.create(entity);

        if (Boolean.TRUE.equals(entity.getIsDispatchBlocked())) {
            notificationService.notifyFleetManagers(entity.getVehicle().getId(), "Xe bị chặn điều phối do sức khoẻ nghiêm trọng");
        }
        return saved;
    }
}

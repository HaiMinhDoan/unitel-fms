package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.MaintenanceItem;
import com.unitel.fms.backend.repositories.MaintenanceItemRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class MaintenanceItemService extends BaseServiceImpl<MaintenanceItem, UUID> {

    private final MaintenanceItemRepository maintenanceItemRepository;

    @Autowired
    private EntityManager entityManager;

    public MaintenanceItemService(MaintenanceItemRepository repository) {
        super(repository);
        this.maintenanceItemRepository = repository;
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    public List<MaintenanceItem> findByMaintenanceOrderId(UUID maintenanceOrderId) {
        return maintenanceItemRepository.findByMaintenanceOrderId(maintenanceOrderId);
    }

    @Override
    @Transactional
    public MaintenanceItem create(MaintenanceItem entity) {
        if (entity.getUnitCost() != null && entity.getQuantity() != null) {
            entity.setTotalCost(entity.getUnitCost().multiply(BigDecimal.valueOf(entity.getQuantity())));
        }
        return super.create(entity);
    }
}

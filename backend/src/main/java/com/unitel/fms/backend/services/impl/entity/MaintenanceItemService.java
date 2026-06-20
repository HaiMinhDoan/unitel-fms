package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.MaintenanceItem;
import com.unitel.fms.backend.repositories.MaintenanceItemRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MaintenanceItemService extends BaseServiceImpl<MaintenanceItem, UUID> {

    private final MaintenanceItemRepository maintenanceItemRepository = (MaintenanceItemRepository) getRepository();

    @Autowired
    private EntityManager entityManager;

    public MaintenanceItemService(MaintenanceItemRepository repository) {
        super(repository);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }
}

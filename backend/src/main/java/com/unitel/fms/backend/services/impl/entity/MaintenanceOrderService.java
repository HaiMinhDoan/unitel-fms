package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.MaintenanceOrder;
import com.unitel.fms.backend.repositories.MaintenanceOrderRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MaintenanceOrderService extends BaseServiceImpl<MaintenanceOrder, UUID> {

    private final MaintenanceOrderRepository maintenanceOrderRepository = (MaintenanceOrderRepository) getRepository();

    @Autowired
    private EntityManager entityManager;

    public MaintenanceOrderService(MaintenanceOrderRepository repository) {
        super(repository);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }
}

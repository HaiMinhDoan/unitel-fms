package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.VehicleHealth;
import com.unitel.fms.backend.repositories.VehicleHealthRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class VehicleHealthService extends BaseServiceImpl<VehicleHealth, UUID> {

    private final VehicleHealthRepository vehicleHealthRepository = (VehicleHealthRepository) getRepository();

    @Autowired
    private EntityManager entityManager;

    public VehicleHealthService(VehicleHealthRepository repository) {
        super(repository);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }
}

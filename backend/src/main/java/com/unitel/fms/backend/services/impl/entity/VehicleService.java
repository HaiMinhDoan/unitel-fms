package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.Vehicle;
import com.unitel.fms.backend.repositories.VehicleRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class VehicleService extends BaseServiceImpl<Vehicle, UUID> {

    private final VehicleRepository vehicleRepository = (VehicleRepository) getRepository();

    @Autowired
    private EntityManager entityManager;

    public VehicleService(VehicleRepository repository) {
        super(repository);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }
}

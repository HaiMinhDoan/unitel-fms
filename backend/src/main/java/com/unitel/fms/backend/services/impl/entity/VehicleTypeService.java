package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.VehicleType;
import com.unitel.fms.backend.repositories.VehicleTypeRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class VehicleTypeService extends BaseServiceImpl<VehicleType, UUID> {

    private final VehicleTypeRepository vehicleTypeRepository = (VehicleTypeRepository) getRepository();

    @Autowired
    private EntityManager entityManager;

    public VehicleTypeService(VehicleTypeRepository repository) {
        super(repository);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }
}

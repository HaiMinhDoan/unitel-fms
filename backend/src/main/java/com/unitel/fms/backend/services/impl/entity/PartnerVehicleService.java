package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.PartnerVehicle;
import com.unitel.fms.backend.repositories.PartnerVehicleRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PartnerVehicleService extends BaseServiceImpl<PartnerVehicle, UUID> {

    private final PartnerVehicleRepository partnerVehicleRepository = (PartnerVehicleRepository) getRepository();

    @Autowired
    private EntityManager entityManager;

    public PartnerVehicleService(PartnerVehicleRepository repository) {
        super(repository);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }
}

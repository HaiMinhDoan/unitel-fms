package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.FuelLog;
import com.unitel.fms.backend.repositories.FuelLogRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FuelLogService extends BaseServiceImpl<FuelLog, UUID> {

    private final FuelLogRepository fuelLogRepository = (FuelLogRepository) getRepository();

    @Autowired
    private EntityManager entityManager;

    public FuelLogService(FuelLogRepository repository) {
        super(repository);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }
}

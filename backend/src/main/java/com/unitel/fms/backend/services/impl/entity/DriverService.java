package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.Driver;
import com.unitel.fms.backend.repositories.DriverRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DriverService extends BaseServiceImpl<Driver, UUID> {

    private final DriverRepository driverRepository = (DriverRepository) getRepository();

    @Autowired
    private EntityManager entityManager;

    public DriverService(DriverRepository repository) {
        super(repository);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }
}

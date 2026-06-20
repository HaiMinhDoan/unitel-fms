package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.SystemConfig;
import com.unitel.fms.backend.repositories.SystemConfigRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SystemConfigService extends BaseServiceImpl<SystemConfig, UUID> {

    private final SystemConfigRepository systemConfigRepository = (SystemConfigRepository) getRepository();

    @Autowired
    private EntityManager entityManager;

    public SystemConfigService(SystemConfigRepository repository) {
        super(repository);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }
}

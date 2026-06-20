package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.AuditLog;
import com.unitel.fms.backend.repositories.AuditLogRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuditLogService extends BaseServiceImpl<AuditLog, UUID> {

    private final AuditLogRepository auditLogRepository = (AuditLogRepository) getRepository();

    @Autowired
    private EntityManager entityManager;

    public AuditLogService(AuditLogRepository repository) {
        super(repository);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }
}

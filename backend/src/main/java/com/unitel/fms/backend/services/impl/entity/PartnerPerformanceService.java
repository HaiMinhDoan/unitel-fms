package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.PartnerPerformance;
import com.unitel.fms.backend.repositories.PartnerPerformanceRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PartnerPerformanceService extends BaseServiceImpl<PartnerPerformance, UUID> {

    private final PartnerPerformanceRepository partnerPerformanceRepository = (PartnerPerformanceRepository) getRepository();

    @Autowired
    private EntityManager entityManager;

    public PartnerPerformanceService(PartnerPerformanceRepository repository) {
        super(repository);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }
}

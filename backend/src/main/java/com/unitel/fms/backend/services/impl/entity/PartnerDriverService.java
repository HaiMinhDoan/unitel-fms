package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.PartnerDriver;
import com.unitel.fms.backend.repositories.PartnerDriverRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PartnerDriverService extends BaseServiceImpl<PartnerDriver, UUID> {

    private final PartnerDriverRepository partnerDriverRepository = (PartnerDriverRepository) getRepository();

    @Autowired
    private EntityManager entityManager;

    public PartnerDriverService(PartnerDriverRepository repository) {
        super(repository);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }
}

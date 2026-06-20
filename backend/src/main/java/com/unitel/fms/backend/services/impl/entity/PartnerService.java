package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.Partner;
import com.unitel.fms.backend.repositories.PartnerRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PartnerService extends BaseServiceImpl<Partner, UUID> {

    private final PartnerRepository partnerRepository = (PartnerRepository) getRepository();

    @Autowired
    private EntityManager entityManager;

    public PartnerService(PartnerRepository repository) {
        super(repository);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }
}

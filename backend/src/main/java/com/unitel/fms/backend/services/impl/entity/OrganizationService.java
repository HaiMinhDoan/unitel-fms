package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.Organization;
import com.unitel.fms.backend.repositories.OrganizationRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrganizationService extends BaseServiceImpl<Organization, UUID> {

    private final OrganizationRepository organizationRepository = (OrganizationRepository) getRepository();

    @Autowired
    private EntityManager entityManager;

    public OrganizationService(OrganizationRepository repository) {
        super(repository);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }
}

package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.DriverDocument;
import com.unitel.fms.backend.repositories.DriverDocumentRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DriverDocumentService extends BaseServiceImpl<DriverDocument, UUID> {

    private final DriverDocumentRepository driverDocumentRepository = (DriverDocumentRepository) getRepository();

    @Autowired
    private EntityManager entityManager;

    public DriverDocumentService(DriverDocumentRepository repository) {
        super(repository);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }
}

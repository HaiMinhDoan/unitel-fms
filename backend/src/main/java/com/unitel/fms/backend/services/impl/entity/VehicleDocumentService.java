package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.VehicleDocument;
import com.unitel.fms.backend.repositories.VehicleDocumentRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class VehicleDocumentService extends BaseServiceImpl<VehicleDocument, UUID> {

    private final VehicleDocumentRepository vehicleDocumentRepository = (VehicleDocumentRepository) getRepository();

    @Autowired
    private EntityManager entityManager;

    public VehicleDocumentService(VehicleDocumentRepository repository) {
        super(repository);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }
}

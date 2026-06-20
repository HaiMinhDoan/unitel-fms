package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.DispatchRequest;
import com.unitel.fms.backend.repositories.DispatchRequestRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DispatchRequestService extends BaseServiceImpl<DispatchRequest, UUID> {

    private final DispatchRequestRepository dispatchRequestRepository = (DispatchRequestRepository) getRepository();

    @Autowired
    private EntityManager entityManager;

    public DispatchRequestService(DispatchRequestRepository repository) {
        super(repository);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }
}

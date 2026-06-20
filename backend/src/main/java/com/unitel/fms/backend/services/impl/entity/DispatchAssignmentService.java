package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.DispatchAssignment;
import com.unitel.fms.backend.repositories.DispatchAssignmentRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DispatchAssignmentService extends BaseServiceImpl<DispatchAssignment, UUID> {

    private final DispatchAssignmentRepository dispatchAssignmentRepository = (DispatchAssignmentRepository) getRepository();

    @Autowired
    private EntityManager entityManager;

    public DispatchAssignmentService(DispatchAssignmentRepository repository) {
        super(repository);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }
}

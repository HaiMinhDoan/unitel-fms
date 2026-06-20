package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.Role;
import com.unitel.fms.backend.repositories.RoleRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RoleService extends BaseServiceImpl<Role, UUID> {

    private final RoleRepository roleRepository = (RoleRepository) getRepository();

    @Autowired
    private EntityManager entityManager;

    public RoleService(RoleRepository repository) {
        super(repository);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }
}

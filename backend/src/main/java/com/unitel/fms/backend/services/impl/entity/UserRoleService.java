package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.UserRole;
import com.unitel.fms.backend.repositories.UserRoleRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserRoleService extends BaseServiceImpl<UserRole, UUID> {

    private final UserRoleRepository userRoleRepository = (UserRoleRepository) getRepository();

    @Autowired
    private EntityManager entityManager;

    public UserRoleService(UserRoleRepository repository) {
        super(repository);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }
}

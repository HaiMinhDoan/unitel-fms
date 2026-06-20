package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.User;
import com.unitel.fms.backend.repositories.UserRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService extends BaseServiceImpl<User, UUID> {

    private final UserRepository userRepository = (UserRepository) getRepository();

    @Autowired
    private EntityManager entityManager;

    public UserService(UserRepository repository) {
        super(repository);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }
}

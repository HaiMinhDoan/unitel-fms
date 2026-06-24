package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.User;
import com.unitel.fms.backend.repositories.UserRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService extends BaseServiceImpl<User, UUID> {

    private final UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository) {
        super(repository);
        this.userRepository = repository;
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public User create(User entity) {
        if (userRepository.existsByUsername(entity.getUsername())) {
            throw new com.unitel.fms.backend.exceptions.customize.ConflictException("Username đã tồn tại");
        }
        if (userRepository.existsByEmail(entity.getEmail())) {
            throw new com.unitel.fms.backend.exceptions.customize.ConflictException("Email đã tồn tại");
        }
        
        if (entity.getPasswordHash() != null) {
            entity.setPasswordHash(passwordEncoder.encode(entity.getPasswordHash()));
        }
        
        return super.create(entity);
    }
}

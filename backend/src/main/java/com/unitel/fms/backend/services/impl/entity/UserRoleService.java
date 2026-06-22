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

    private final UserRoleRepository userRoleRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private com.unitel.fms.backend.repositories.RoleRepository roleRepository;

    public UserRoleService(UserRoleRepository repository) {
        super(repository);
        this.userRoleRepository = repository;
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public UserRole create(UserRole entity) {
        if (entity.getRole() != null && entity.getRole().getId() != null) {
            com.unitel.fms.backend.entities.Role role = roleRepository.findById(entity.getRole().getId())
                    .orElseThrow(() -> new com.unitel.fms.backend.exceptions.customize.NotFoundException("Role không tồn tại"));
            
            if ("SYSTEM_ADMIN".equals(role.getCode())) {
                com.unitel.fms.backend.dtos.AuthInfo authInfo = com.unitel.fms.backend.contexts.SecurityContextHolder.getAuthInfo();
                if (!authInfo.hasAnyRole(com.unitel.fms.backend.constants.enums.RoleType.SYSTEM_ADMIN)) {
                    throw new org.springframework.security.access.AccessDeniedException("Chỉ SYSTEM_ADMIN mới có quyền gán role SYSTEM_ADMIN");
                }
            }
        }
        return super.create(entity);
    }

    @org.springframework.transaction.annotation.Transactional
    public void hardDelete(UUID id) {
        repository.deleteById(id);
    }
}

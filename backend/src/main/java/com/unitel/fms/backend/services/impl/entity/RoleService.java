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

    private final RoleRepository roleRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private com.unitel.fms.backend.services.RedisService redisService;

    @Autowired
    private com.unitel.fms.backend.repositories.UserRoleRepository userRoleRepository;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    public RoleService(RoleRepository repository) {
        super(repository);
        this.roleRepository = repository;
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    private void invalidateCache(UUID id) {
        if (id != null) {
            redisService.delete(redisService.buildKey("role", id.toString()));
        }
        redisService.delete("role:all-list");
    }

    public Role getByIdCached(UUID id) {
        String key = redisService.buildKey("role", id.toString());
        Role cached = redisService.get(key, Role.class);
        if (cached != null) return cached;

        Role entity = repository.findById(id)
                .orElseThrow(() -> new com.unitel.fms.backend.exceptions.customize.NotFoundException("Role không tồn tại: " + id));

        redisService.set(key, entity, 3600);
        return entity;
    }

    public java.util.List<Role> getAllCached() {
        String key = "role:all-list";
        
        try {
            String cachedStr = redisService.get(key, String.class);
            if (cachedStr != null) {
                return objectMapper.readValue(cachedStr, new com.fasterxml.jackson.core.type.TypeReference<java.util.List<Role>>() {});
            }
        } catch (Exception e) {
            // ignore JSON parse error and fallback to DB
        }

        java.util.List<Role> roles = repository.findAll(); // Could filter by status
        try {
            redisService.set(key, objectMapper.writeValueAsString(roles), 3600);
        } catch (Exception e) {
            // ignore
        }
        return roles;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public Role create(Role entity) {
        Role saved = super.create(entity);
        invalidateCache(saved.getId());
        return saved;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public Role update(UUID id, Role entity) {
        Role updated = super.update(id, entity);
        invalidateCache(id);
        return updated;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public Role updateFromMap(UUID id, java.util.Map<String, Object> updates) {
        Role updated = super.updateFromMap(id, updates);
        invalidateCache(id);
        return updated;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public Role changeStatus(UUID id, String status) {
        Role updated = super.changeStatus(id, status);
        invalidateCache(id);
        return updated;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void delete(UUID id) {
        super.delete(id);
        invalidateCache(id);
    }

    @org.springframework.transaction.annotation.Transactional
    public void hardDelete(UUID id) {
        if (userRoleRepository.existsByRoleId(id)) {
            throw new com.unitel.fms.backend.exceptions.customize.ConflictException("Không thể xoá role này do có user đang sử dụng");
        }
        repository.deleteById(id);
        invalidateCache(id);
    }
}

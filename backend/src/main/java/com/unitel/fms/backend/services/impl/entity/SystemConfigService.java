package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.SystemConfig;
import com.unitel.fms.backend.repositories.SystemConfigRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SystemConfigService extends BaseServiceImpl<SystemConfig, UUID> {

    private final SystemConfigRepository systemConfigRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private com.unitel.fms.backend.services.RedisService redisService;

    public SystemConfigService(SystemConfigRepository repository) {
        super(repository);
        this.systemConfigRepository = repository;
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    private void invalidateCache(SystemConfig entity) {
        if (entity != null && entity.getConfigKey() != null) {
            redisService.delete(redisService.buildKey("sysconfig", entity.getConfigKey()));
        }
    }

    public SystemConfig getByKeyCached(String key) {
        String redisKey = redisService.buildKey("sysconfig", key);
        SystemConfig cached = redisService.get(redisKey, SystemConfig.class);
        if (cached != null) return cached;

        SystemConfig entity = systemConfigRepository.findByConfigKey(key)
                .orElseThrow(() -> new com.unitel.fms.backend.exceptions.customize.NotFoundException("Config không tồn tại: " + key));

        redisService.set(redisKey, entity, 3600);
        return entity;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public SystemConfig create(SystemConfig entity) {
        SystemConfig saved = super.create(entity);
        invalidateCache(saved);
        return saved;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public SystemConfig update(UUID id, SystemConfig entity) {
        SystemConfig updated = super.update(id, entity);
        invalidateCache(updated);
        return updated;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public SystemConfig updateFromMap(UUID id, java.util.Map<String, Object> updates) {
        SystemConfig updated = super.updateFromMap(id, updates);
        invalidateCache(updated);
        return updated;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public SystemConfig changeStatus(UUID id, String status) {
        SystemConfig updated = super.changeStatus(id, status);
        invalidateCache(updated);
        return updated;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void delete(UUID id) {
        systemConfigRepository.findById(id).ifPresent(this::invalidateCache);
        super.delete(id);
    }

    @org.springframework.transaction.annotation.Transactional
    public void hardDelete(UUID id) {
        systemConfigRepository.findById(id).ifPresent(this::invalidateCache);
        repository.deleteById(id);
    }
}

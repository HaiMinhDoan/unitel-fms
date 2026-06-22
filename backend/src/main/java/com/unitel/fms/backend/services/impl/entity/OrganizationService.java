package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.Organization;
import com.unitel.fms.backend.repositories.OrganizationRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrganizationService extends BaseServiceImpl<Organization, UUID> {

    private final OrganizationRepository organizationRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private com.unitel.fms.backend.services.RedisService redisService;

    @Autowired
    private com.unitel.fms.backend.repositories.UserRepository userRepository;

    @Autowired
    private com.unitel.fms.backend.repositories.VehicleRepository vehicleRepository;

    public OrganizationService(OrganizationRepository repository) {
        super(repository);
        this.organizationRepository = repository;
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    public Organization getByIdCached(UUID id) {
        String key = redisService.buildKey("organization", id.toString());

        Organization cached = redisService.get(key, Organization.class);
        if (cached != null) {
            return cached;
        }

        Organization entity = repository.findById(id)
                .orElseThrow(() -> new com.unitel.fms.backend.exceptions.customize.NotFoundException("Organization không tồn tại: " + id));

        redisService.set(key, entity, 3600); // TTL 1 giờ
        return entity;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public Organization create(Organization entity) {
        if (organizationRepository.existsByCode(entity.getCode())) {
            throw new com.unitel.fms.backend.exceptions.customize.ConflictException("Mã tổ chức '" + entity.getCode() + "' đã tồn tại");
        }
        if (entity.getParent() != null && entity.getParent().getId() != null) {
            organizationRepository.findById(entity.getParent().getId())
                    .filter(p -> "active".equals(p.getStatus()))
                    .orElseThrow(() -> new com.unitel.fms.backend.exceptions.customize.InvalidFieldException("Tổ chức cha không tồn tại hoặc không active"));
        }
        return super.create(entity);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public Organization update(UUID id, Organization entity) {
        Organization updated = super.update(id, entity);
        redisService.delete(redisService.buildKey("organization", id.toString()));
        return updated;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public Organization updateFromMap(UUID id, java.util.Map<String, Object> updates) {
        Organization updated = super.updateFromMap(id, updates);
        redisService.delete(redisService.buildKey("organization", id.toString()));
        return updated;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public Organization changeStatus(UUID id, String status) {
        Organization updated = super.changeStatus(id, status);
        redisService.delete(redisService.buildKey("organization", id.toString()));
        return updated;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void delete(UUID id) {
        super.delete(id);
        redisService.delete(redisService.buildKey("organization", id.toString()));
    }

    @org.springframework.transaction.annotation.Transactional
    public void hardDelete(UUID id) {
        if (userRepository.existsByOrgId(id) || vehicleRepository.existsByOrgId(id)) {
            throw new com.unitel.fms.backend.exceptions.customize.ConflictException("Không thể xoá cứng: tổ chức vẫn còn dữ liệu liên quan. Dùng soft-delete.");
        }
        repository.deleteById(id);
        redisService.delete(redisService.buildKey("organization", id.toString()));
    }
}

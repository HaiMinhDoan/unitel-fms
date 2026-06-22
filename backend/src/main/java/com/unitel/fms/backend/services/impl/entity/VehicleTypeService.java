package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.VehicleType;
import com.unitel.fms.backend.repositories.VehicleTypeRepository;
import com.unitel.fms.backend.services.RedisService;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import com.unitel.fms.backend.exceptions.customize.ConflictException;
import com.unitel.fms.backend.exceptions.customize.NotFoundException;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
public class VehicleTypeService extends BaseServiceImpl<VehicleType, UUID> {

    private final VehicleTypeRepository vehicleTypeRepository;
    private final RedisService redisService;

    @Autowired
    private EntityManager entityManager;

    public VehicleTypeService(VehicleTypeRepository repository, RedisService redisService) {
        super(repository);
        this.vehicleTypeRepository = repository;
        this.redisService = redisService;
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    private String getCacheKey(UUID id) {
        return redisService.buildKey("vehicle-type", id.toString());
    }

    @Transactional(readOnly = true)
    public VehicleType getByIdCached(UUID id) {
        String key = getCacheKey(id);
        VehicleType cached = redisService.get(key, VehicleType.class);
        if (cached != null) {
            return cached;
        }

        VehicleType entity = vehicleTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Loại phương tiện không tồn tại: " + id));

        redisService.set(key, entity, 3600);
        return entity;
    }

    @Override
    @Transactional
    public VehicleType create(VehicleType entity) {
        if (vehicleTypeRepository.existsByCode(entity.getCode())) {
            throw new ConflictException("Mã loại phương tiện '" + entity.getCode() + "' đã tồn tại");
        }
        return super.create(entity);
    }

    @Override
    @Transactional
    public VehicleType update(UUID id, VehicleType entity) {
        VehicleType updated = super.update(id, entity);
        redisService.delete(getCacheKey(id));
        return updated;
    }

    @Override
    @Transactional
    public VehicleType updateFromMap(UUID id, Map<String, Object> updates) {
        VehicleType updated = super.updateFromMap(id, updates);
        redisService.delete(getCacheKey(id));
        return updated;
    }

    @Override
    @Transactional
    public VehicleType changeStatus(UUID id, String status) {
        VehicleType updated = super.changeStatus(id, status);
        redisService.delete(getCacheKey(id));
        return updated;
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        super.delete(id);
        redisService.delete(getCacheKey(id));
    }
}

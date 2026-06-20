package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.GpsPosition;
import com.unitel.fms.backend.repositories.GpsPositionRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unitel.fms.backend.entities.GpsPositionId;

@Service
public class GpsPositionService extends BaseServiceImpl<GpsPosition, GpsPositionId> {

    private final GpsPositionRepository gpsPositionRepository = (GpsPositionRepository) getRepository();

    @Autowired
    private EntityManager entityManager;

    public GpsPositionService(GpsPositionRepository repository) {
        super(repository);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }
}

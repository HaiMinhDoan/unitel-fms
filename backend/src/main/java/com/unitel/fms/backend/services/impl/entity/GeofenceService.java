package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.Geofence;
import com.unitel.fms.backend.repositories.GeofenceRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GeofenceService extends BaseServiceImpl<Geofence, UUID> {

    private final GeofenceRepository geofenceRepository = (GeofenceRepository) getRepository();

    @Autowired
    private EntityManager entityManager;

    public GeofenceService(GeofenceRepository repository) {
        super(repository);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }
}

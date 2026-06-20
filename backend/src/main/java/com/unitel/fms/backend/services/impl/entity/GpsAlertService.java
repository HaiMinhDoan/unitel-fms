package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.GpsAlert;
import com.unitel.fms.backend.repositories.GpsAlertRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GpsAlertService extends BaseServiceImpl<GpsAlert, UUID> {

    private final GpsAlertRepository gpsAlertRepository = (GpsAlertRepository) getRepository();

    @Autowired
    private EntityManager entityManager;

    public GpsAlertService(GpsAlertRepository repository) {
        super(repository);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }
}

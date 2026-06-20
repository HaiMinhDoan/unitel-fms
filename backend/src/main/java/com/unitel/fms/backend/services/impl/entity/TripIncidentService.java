package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.TripIncident;
import com.unitel.fms.backend.repositories.TripIncidentRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TripIncidentService extends BaseServiceImpl<TripIncident, UUID> {

    private final TripIncidentRepository tripIncidentRepository = (TripIncidentRepository) getRepository();

    @Autowired
    private EntityManager entityManager;

    public TripIncidentService(TripIncidentRepository repository) {
        super(repository);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }
}

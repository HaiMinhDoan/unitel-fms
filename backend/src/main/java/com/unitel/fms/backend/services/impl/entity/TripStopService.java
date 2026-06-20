package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.TripStop;
import com.unitel.fms.backend.repositories.TripStopRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TripStopService extends BaseServiceImpl<TripStop, UUID> {

    private final TripStopRepository tripStopRepository = (TripStopRepository) getRepository();

    @Autowired
    private EntityManager entityManager;

    public TripStopService(TripStopRepository repository) {
        super(repository);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }
}

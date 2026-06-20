package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.Trip;
import com.unitel.fms.backend.repositories.TripRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TripService extends BaseServiceImpl<Trip, UUID> {

    private final TripRepository tripRepository = (TripRepository) getRepository();

    @Autowired
    private EntityManager entityManager;

    public TripService(TripRepository repository) {
        super(repository);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }
}

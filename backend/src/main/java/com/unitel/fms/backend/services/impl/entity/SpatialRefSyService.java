package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.SpatialRefSy;
import com.unitel.fms.backend.repositories.SpatialRefSyRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class SpatialRefSyService extends BaseServiceImpl<SpatialRefSy, Integer> {

    private final SpatialRefSyRepository spatialRefSyRepository = (SpatialRefSyRepository) getRepository();

    @Autowired
    private EntityManager entityManager;

    public SpatialRefSyService(SpatialRefSyRepository repository) {
        super(repository);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }
}

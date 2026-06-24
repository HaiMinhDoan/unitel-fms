package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.TripStop;
import com.unitel.fms.backend.repositories.TripStopRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class TripStopService extends BaseServiceImpl<TripStop, UUID> {

    private final TripStopRepository tripStopRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private FileAttachmentService fileAttachmentService;

    public TripStopService(TripStopRepository repository) {
        super(repository);
        this.tripStopRepository = repository;
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    @Transactional
    public TripStop submitEpod(UUID id, MultipartFile photo, MultipartFile signature) {
        fileAttachmentService.upload(photo, "trip_stops/epod_photo", id);
        fileAttachmentService.upload(signature, "trip_stops/epod_sign", id);

        TripStop stop = changeStatus(id, "completed");
        stop.setActualArrival(OffsetDateTime.now());
        return update(id, stop);
    }

}
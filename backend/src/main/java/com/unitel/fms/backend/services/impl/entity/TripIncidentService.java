package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.TripIncident;
import com.unitel.fms.backend.repositories.TripIncidentRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unitel.fms.backend.contexts.SecurityContextHolder;
import com.unitel.fms.backend.dtos.AuthInfo;
import com.unitel.fms.backend.entities.Trip;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Service
public class TripIncidentService extends BaseServiceImpl<TripIncident, UUID> {

    private final TripIncidentRepository tripIncidentRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private TripService tripService;

    public TripIncidentService(TripIncidentRepository repository) {
        super(repository);
        this.tripIncidentRepository = repository;
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    @Override
    @Transactional
    public TripIncident create(TripIncident entity) {
        AuthInfo authInfo = SecurityContextHolder.getAuthInfo();
        if (authInfo != null && authInfo.getId() != null) {
            com.unitel.fms.backend.entities.User reportedBy = new com.unitel.fms.backend.entities.User();
            reportedBy.setId(authInfo.getId());
            entity.setReportedBy(reportedBy);
        }
        entity.setStatus("open");

        TripIncident saved = super.create(entity);

        Trip trip = tripService.getOne(saved.getTrip().getId()).orElseThrow();
        if (Set.of("breakdown", "accident").contains(entity.getIncidentType())) {
            notificationService.notifyUrgent(trip.getDispatchAssignment().getDispatchRequest().getOrg().getId(), "Sự cố khẩn cấp: " + entity.getIncidentType(), saved.getId());
        }

        return saved;
    }

    @Transactional
    public TripIncident resolve(UUID id, String resolutionNotes) {
        TripIncident incident = getOne(id).orElseThrow(() -> new java.util.NoSuchElementException("Sự cố không tồn tại"));
        incident.setStatus("resolved");
        
        java.util.Map<String, Object> metadata = incident.getMetadata();
        if (metadata == null) metadata = new java.util.HashMap<>();
        metadata.put("resolutionNotes", resolutionNotes);
        incident.setMetadata(metadata);
        
        AuthInfo authInfo = SecurityContextHolder.getAuthInfo();
        if (authInfo != null && authInfo.getId() != null) {
            com.unitel.fms.backend.entities.User resolvedBy = new com.unitel.fms.backend.entities.User();
            resolvedBy.setId(authInfo.getId());
            incident.setResolvedBy(resolvedBy);
        }
        incident.setResolvedAt(OffsetDateTime.now());
        
        return update(id, incident);
    }
}

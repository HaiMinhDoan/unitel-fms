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

    @Autowired
    private com.unitel.fms.backend.services.impl.entity.VehicleService vehicleService;

    public GpsAlertService(GpsAlertRepository repository) {
        super(repository);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    public void createGeofenceAlert(com.unitel.fms.backend.entities.GpsPosition position, com.unitel.fms.backend.entities.Geofence fence, String alertType) {
        GpsAlert alert = new GpsAlert();
        vehicleService.getOne(position.getId().getVehicleId()).ifPresent(v -> {
            alert.setVehicle(v);
            alert.setTrip(position.getTrip());
            alert.setAlertType(alertType); // "geofence_enter" or "geofence_exit"
            alert.setStatus("open");
            alert.setIsAcknowledged(false);
            
            java.util.Map<String, Object> details = new java.util.HashMap<>();
            details.put("geofenceId", fence.getId());
            details.put("geofenceName", fence.getName());
            details.put("lat", position.getLat());
            details.put("lng", position.getLng());
            alert.setDetails(details);
            
            super.create(alert);
        });
    }

    public void createSpeedingAlert(com.unitel.fms.backend.entities.GpsPosition position, double limitKmh) {
        GpsAlert alert = new GpsAlert();
        vehicleService.getOne(position.getId().getVehicleId()).ifPresent(v -> {
            alert.setVehicle(v);
            alert.setTrip(position.getTrip());
            alert.setAlertType("speeding");
            alert.setStatus("open");
            alert.setIsAcknowledged(false);

            java.util.Map<String, Object> details = new java.util.HashMap<>();
            details.put("speedKmh", position.getSpeedKmh());
            details.put("limitKmh", limitKmh);
            details.put("lat", position.getLat());
            details.put("lng", position.getLng());
            alert.setDetails(details);

            super.create(alert);
        });
    }

    @org.springframework.transaction.annotation.Transactional
    public GpsAlert acknowledge(UUID id) {
        GpsAlert alert = getOne(id).orElseThrow(() -> new RuntimeException("Cảnh báo không tồn tại"));
        alert.setIsAcknowledged(true);
        com.unitel.fms.backend.dtos.AuthInfo auth = com.unitel.fms.backend.contexts.SecurityContextHolder.getAuthInfo();
        if (auth != null) {
            com.unitel.fms.backend.entities.User user = new com.unitel.fms.backend.entities.User();
            user.setId(auth.getId());
            alert.setAcknowledgedBy(user);
        }
        alert.setAcknowledgedAt(java.time.OffsetDateTime.now());
        alert.setStatus("acknowledged");
        return super.update(id, alert);
    }

    public java.util.List<GpsAlert> getOpenAlerts(UUID orgId) {
        return gpsAlertRepository.findByVehicleOrgIdAndStatusOrderByTriggeredAtDesc(orgId, "open");
    }
}

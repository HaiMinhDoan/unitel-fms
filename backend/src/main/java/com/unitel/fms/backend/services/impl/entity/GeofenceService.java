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

    @Autowired
    private com.unitel.fms.backend.services.RedisService redisService;
    
    @Autowired
    @org.springframework.context.annotation.Lazy
    private com.unitel.fms.backend.services.impl.entity.GpsAlertService gpsAlertService;

    @Autowired
    private com.unitel.fms.backend.services.impl.entity.VehicleService vehicleService;

    public GeofenceService(GeofenceRepository repository) {
        super(repository);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public java.util.List<Geofence> getAllCachedByOrg(UUID orgId) {
        String key = redisService.buildKey("geofence:all-by-org", orgId.toString());
        java.util.List<Geofence> cached = redisService.get(key, java.util.List.class);
        if (cached != null) {
            return cached;
        }
        java.util.List<Geofence> dbGeofences = geofenceRepository.findByOrgIdAndStatus(orgId, "active");
        redisService.set(key, dbGeofences, 3600);
        return dbGeofences;
    }

    public void checkViolations(com.unitel.fms.backend.entities.GpsPosition position) {
        vehicleService.getOne(position.getId().getVehicleId()).ifPresent(vehicle -> {
            UUID orgId = vehicle.getOrg().getId();
            java.util.List<Geofence> geofences = getAllCachedByOrg(orgId);

            for (Geofence fence : geofences) {
                boolean isInside = isInsideGeofence(position.getLat(), position.getLng(), fence);
                String stateKey = redisService.buildKey("geofence-state:" + position.getId().getVehicleId().toString(), fence.getId().toString());
                Boolean wasInsideBefore = redisService.get(stateKey, Boolean.class);
                if (wasInsideBefore == null) wasInsideBefore = false;

                if (isInside && !wasInsideBefore) {
                    gpsAlertService.createGeofenceAlert(position, fence, "geofence_enter");
                    redisService.set(stateKey, true, 86400); // 24h
                } else if (!isInside && wasInsideBefore) {
                    gpsAlertService.createGeofenceAlert(position, fence, "geofence_exit");
                    redisService.set(stateKey, false, 86400); // 24h
                }
            }
        });
    }

    private boolean isInsideGeofence(double lat, double lng, Geofence fence) {
        if ("circle".equalsIgnoreCase(fence.getFenceType())) {
            java.util.Map<String, Object> center = (java.util.Map<String, Object>) fence.getCoordinates().get("center");
            double centerLat = Double.parseDouble(center.get("lat").toString());
            double centerLng = Double.parseDouble(center.get("lng").toString());
            double distance = distanceMeters(lat, lng, centerLat, centerLng);
            return fence.getRadiusM() != null && distance <= fence.getRadiusM().doubleValue();
        } else if ("polygon".equalsIgnoreCase(fence.getFenceType())) {
            java.util.List<java.util.Map<String, Object>> polygon = (java.util.List<java.util.Map<String, Object>>) fence.getCoordinates().get("points");
            return isPointInPolygon(lat, lng, polygon);
        }
        return false;
    }

    private boolean isPointInPolygon(double lat, double lng, java.util.List<java.util.Map<String, Object>> polygon) {
        if (polygon == null || polygon.size() < 3) return false;
        boolean inside = false;
        for (int i = 0, j = polygon.size() - 1; i < polygon.size(); j = i++) {
            double xi = Double.parseDouble(polygon.get(i).get("lng").toString()), yi = Double.parseDouble(polygon.get(i).get("lat").toString());
            double xj = Double.parseDouble(polygon.get(j).get("lng").toString()), yj = Double.parseDouble(polygon.get(j).get("lat").toString());

            boolean intersect = ((yi > lat) != (yj > lat))
                    && (lng < (xj - xi) * (lat - yi) / (yj - yi) + xi);
            if (intersect) inside = !inside;
        }
        return inside;
    }

    private double distanceMeters(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371000; // Radius of the earth in m
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    @Override
    public Geofence create(Geofence entity) {
        Geofence saved = super.create(entity);
        redisService.delete(redisService.buildKey("geofence:all-by-org", saved.getOrg().getId().toString()));
        return saved;
    }

    @Override
    public Geofence update(UUID id, Geofence entity) {
        Geofence updated = super.update(id, entity);
        redisService.delete(redisService.buildKey("geofence:all-by-org", updated.getOrg().getId().toString()));
        return updated;
    }
}

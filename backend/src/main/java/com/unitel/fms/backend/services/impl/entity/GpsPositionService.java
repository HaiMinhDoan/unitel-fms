package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.GpsPosition;
import com.unitel.fms.backend.repositories.GpsPositionRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unitel.fms.backend.entities.GpsPositionId;

@Service
public class GpsPositionService extends BaseServiceImpl<GpsPosition, GpsPositionId> {

    private final GpsPositionRepository gpsPositionRepository = (GpsPositionRepository) getRepository();

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private org.springframework.kafka.core.KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private com.unitel.fms.backend.services.RedisService redisService;

    @Autowired
    private GeofenceService geofenceService;

    @Autowired
    private GpsAlertService gpsAlertService;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private com.unitel.fms.backend.mappers.GpsPositionMapper gpsPositionMapper;

    public GpsPositionService(GpsPositionRepository repository) {
        super(repository);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    public void ingest(com.unitel.fms.backend.dtos.request.GpsIngestRequest request) {
        kafkaTemplate.send("gps-position-events", request.getVehicleId().toString(), request);
    }

    @org.springframework.kafka.annotation.KafkaListener(topics = "gps-position-events", groupId = "gps-processor")
    public void consume(String eventJson) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            com.unitel.fms.backend.dtos.request.GpsIngestRequest event = mapper.readValue(eventJson, com.unitel.fms.backend.dtos.request.GpsIngestRequest.class);
            
            GpsPosition position = gpsPositionMapper.toEntity(event);
            position.setStatus("received");
            if (position.getCreatedAt() == null) position.setCreatedAt(java.time.OffsetDateTime.now());
            if (position.getUpdatedAt() == null) position.setUpdatedAt(java.time.OffsetDateTime.now());
            
            // Save to DB
            position = gpsPositionRepository.save(position);

            // Update Redis (Latest state)
            String redisKey = redisService.buildKey("vehicle-position", event.getVehicleId().toString());
            redisService.set(redisKey, position, 300); // 5 mins

            // Check geofence
            geofenceService.checkViolations(position);
            
            // Check speed
            if (position.getSpeedKmh() != null && position.getSpeedKmh() > 80.0) {
                gpsAlertService.createSpeedingAlert(position, 80.0);
            }
        } catch (Exception e) {
            System.err.println("Error processing GPS event: " + e.getMessage());
        }
    }

    public com.unitel.fms.backend.dtos.response.GpsPositionDto getCurrentPosition(java.util.UUID vehicleId) {
        String key = redisService.buildKey("vehicle-position", vehicleId.toString());
        GpsPosition cached = redisService.get(key, GpsPosition.class);
        if (cached == null) {
            return com.unitel.fms.backend.dtos.response.GpsPositionDto.builder().vehicleId(vehicleId).status("offline").build();
        }
        return gpsPositionMapper.toDto(cached);
    }

    public java.util.List<com.unitel.fms.backend.dtos.response.GpsPositionDto> getCurrentAllInOrg(java.util.UUID orgId) {
        java.util.List<java.util.UUID> vehicleIds = vehicleService.getRepository().findAll().stream()
                .filter(v -> v.getOrg().getId().equals(orgId) && "active".equals(v.getStatus()))
                .map(com.unitel.fms.backend.entities.Vehicle::getId)
                .collect(java.util.stream.Collectors.toList());
                
        return vehicleIds.stream()
                .map(this::getCurrentPosition)
                .filter(dto -> !"offline".equals(dto.getStatus()))
                .collect(java.util.stream.Collectors.toList());
    }
}

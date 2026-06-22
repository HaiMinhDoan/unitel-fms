package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.FuelLog;
import com.unitel.fms.backend.repositories.FuelLogRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unitel.fms.backend.entities.Vehicle;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class FuelLogService extends BaseServiceImpl<FuelLog, UUID> {

    private final FuelLogRepository fuelLogRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private NotificationService notificationService;

    public FuelLogService(FuelLogRepository repository) {
        super(repository);
        this.fuelLogRepository = repository;
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    @Override
    @Transactional
    public FuelLog create(FuelLog entity) {
        // Mocking telemetry consumption to be slightly lower than reported
        if (entity.getReportedConsumption() != null) {
            BigDecimal telemetry = entity.getReportedConsumption().multiply(new BigDecimal("0.9"));
            entity.setTelemetryConsumption(telemetry);
        } else {
            entity.setTelemetryConsumption(BigDecimal.ZERO);
            entity.setReportedConsumption(BigDecimal.ZERO);
        }

        detectAnomaly(entity);

        FuelLog saved = super.create(entity);

        if (Boolean.TRUE.equals(saved.getAnomalyFlagged())) {
            Vehicle vehicle = vehicleService.getOne(saved.getVehicle().getId()).orElseThrow();
            notificationService.notifyUrgent(vehicle.getOrg().getId(), "Cảnh báo gian lận nhiên liệu", saved.getId());
        }

        return saved;
    }

    private void detectAnomaly(FuelLog log) {
        if (log.getReportedConsumption() == null || log.getTelemetryConsumption() == null) {
            log.setAnomalyFlagged(false);
            return;
        }
        
        BigDecimal diff = log.getReportedConsumption().subtract(log.getTelemetryConsumption()).abs();
        // Assuming 10% tolerance for anomaly
        BigDecimal threshold = log.getTelemetryConsumption().multiply(new BigDecimal("0.1"));
        
        if (diff.compareTo(threshold) > 0) {
            log.setAnomalyFlagged(true);
            log.setAnomalyNotes("Chênh lệch vượt quá 10%");
        } else {
            log.setAnomalyFlagged(false);
        }
    }
}

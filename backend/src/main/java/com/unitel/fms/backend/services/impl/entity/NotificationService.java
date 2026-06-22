package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.Notification;
import com.unitel.fms.backend.repositories.NotificationRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NotificationService extends BaseServiceImpl<Notification, UUID> {

    private final NotificationRepository notificationRepository = (NotificationRepository) getRepository();

    @Autowired
    private EntityManager entityManager;

    public NotificationService(NotificationRepository repository) {
        super(repository);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    public void notifyFleetManagers(UUID vehicleId, String message) {
        // TODO: Implement actual notification logic
        System.out.println("NOTIFY FLEET MANAGERS: " + message + " FOR VEHICLE " + vehicleId);
    }

    public void notifyUrgent(UUID orgId, String message, UUID referenceId) {
        // TODO: Implement actual notification logic
        System.out.println("URGENT NOTIFY ORG " + orgId + ": " + message + " (REF: " + referenceId + ")");
    }
}

package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.VehicleDocument;
import com.unitel.fms.backend.repositories.VehicleDocumentRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unitel.fms.backend.constants.enums.EntityType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class VehicleDocumentService extends ExpirableDocumentServiceBase<VehicleDocument, UUID> {

    private final VehicleDocumentRepository vehicleDocumentRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private FileAttachmentService fileAttachmentService;

    public VehicleDocumentService(VehicleDocumentRepository repository) {
        super(repository);
        this.vehicleDocumentRepository = repository;
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    @Override
    protected String getThresholdConfigKey() {
        return "document.expiry.threshold_days";
    }

    @Override
    protected List<VehicleDocument> findExpiringSoon(UUID orgId, LocalDate from, LocalDate to) {
        return vehicleDocumentRepository.findByVehicleOrgIdAndExpiryDateBetweenAndStatusNot(orgId, from, to, "expired");
    }

    public boolean hasNoExpiredOrMissingDocs(UUID vehicleId) {
        return !vehicleDocumentRepository.existsByVehicleIdAndStatus(vehicleId, "expired");
    }

    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void refreshDocumentStatuses() {
        int thresholdDays = 30;
        try {
            thresholdDays = Integer.parseInt(
                String.valueOf(systemConfigService.getByKeyCached(getThresholdConfigKey()).getConfigValue())
            );
        } catch (Exception e) {}
        
        vehicleDocumentRepository.bulkUpdateStatusExpiringSoon(LocalDate.now(), LocalDate.now().plusDays(thresholdDays));
        vehicleDocumentRepository.bulkUpdateStatusExpired(LocalDate.now());
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        fileAttachmentService.deleteAllByEntity(EntityType.VEHICLE_DOCUMENT, id);
        super.delete(id);
    }
}

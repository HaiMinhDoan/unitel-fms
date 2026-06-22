package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.DriverDocument;
import com.unitel.fms.backend.repositories.DriverDocumentRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class DriverDocumentService extends ExpirableDocumentServiceBase<DriverDocument, UUID> {

    private final DriverDocumentRepository driverDocumentRepository;

    @Autowired
    private EntityManager entityManager;

    public DriverDocumentService(DriverDocumentRepository repository) {
        super(repository);
        this.driverDocumentRepository = repository;
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
    protected List<DriverDocument> findExpiringSoon(UUID orgId, LocalDate from, LocalDate to) {
        return driverDocumentRepository.findByDriverOrgIdAndExpiryDateBetweenAndStatusNot(orgId, from, to, "expired");
    }

    public boolean hasNoExpiredOrMissingDocs(UUID driverId) {
        return !driverDocumentRepository.existsByDriverIdAndStatus(driverId, "expired");
    }

    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void refreshDocumentStatuses() {
        int thresholdDays = 30;
        try {
            thresholdDays = Integer.parseInt(
                    String.valueOf(systemConfigService.getByKeyCached(getThresholdConfigKey()).getConfigValue()));
        } catch (Exception e) {
        }

        driverDocumentRepository.bulkUpdateStatusExpiringSoon(LocalDate.now(), LocalDate.now().plusDays(thresholdDays));
        driverDocumentRepository.bulkUpdateStatusExpired(LocalDate.now());
    }

    @Autowired
    private FileAttachmentService fileAttachmentService;

    @Override
    @Transactional
    public void delete(UUID id) {
        fileAttachmentService.deleteAllByEntity(com.unitel.fms.backend.constants.enums.EntityType.DRIVER_DOCUMENT, id);
        super.delete(id);
    }
}

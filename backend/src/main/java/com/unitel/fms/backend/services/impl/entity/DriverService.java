package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.Driver;
import com.unitel.fms.backend.repositories.DriverRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unitel.fms.backend.dtos.response.DriverEligibilityResult;
import com.unitel.fms.backend.exceptions.customize.NotFoundException;

import java.util.List;
import java.util.UUID;

@Service
public class DriverService extends BaseServiceImpl<Driver, UUID> {

    private final DriverRepository driverRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private DriverDocumentService driverDocumentService;

    public DriverService(DriverRepository repository) {
        super(repository);
        this.driverRepository = repository;
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    public List<Driver> findAvailableForDispatch(UUID orgId) {
        return driverRepository.findAvailableForDispatch(orgId);
    }

    public DriverEligibilityResult checkDispatchEligibility(UUID driverId) {
        Driver driver = getOne(driverId).orElseThrow(() -> new NotFoundException("Tài xế không tồn tại"));

        if (!driver.getIsDispatchEligible()) {
            return DriverEligibilityResult.builder().eligible(false).reason("Tài xế bị đánh dấu không đủ điều kiện").build();
        }

        boolean docsOk = driverDocumentService.hasNoExpiredOrMissingDocs(driverId);
        return DriverEligibilityResult.builder()
                .eligible(docsOk)
                .reason(docsOk ? null : "Có giấy tờ tài xế hết hạn hoặc thiếu")
                .build();
    }

    public Driver getMe(UUID userId) {
        return driverRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Tài khoản chưa liên kết hồ sơ tài xế"));
    }
}

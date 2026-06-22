package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public abstract class ExpirableDocumentServiceBase<T, ID> extends BaseServiceImpl<T, ID> {

    @Autowired
    protected SystemConfigService systemConfigService;

    public ExpirableDocumentServiceBase(org.springframework.data.jpa.repository.JpaRepository<T, ID> repository) {
        super(repository);
    }

    protected abstract String getThresholdConfigKey();
    protected abstract List<T> findExpiringSoon(UUID orgId, LocalDate from, LocalDate to);

    public List<T> getExpiringSoon(UUID orgId) {
        int thresholdDays = 30; // default
        try {
            thresholdDays = Integer.parseInt(
                String.valueOf(systemConfigService.getByKeyCached(getThresholdConfigKey()).getConfigValue())
            );
        } catch (Exception e) {
            // ignore and use default
        }
        return findExpiringSoon(orgId, LocalDate.now(), LocalDate.now().plusDays(thresholdDays));
    }
}

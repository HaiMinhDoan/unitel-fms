package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.DispatchRequest;
import com.unitel.fms.backend.repositories.DispatchRequestRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unitel.fms.backend.contexts.SecurityContextHolder;
import com.unitel.fms.backend.dtos.request.BulkUpdateItem;
import com.unitel.fms.backend.entities.Organization;
import com.unitel.fms.backend.constants.enums.EntityType;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DispatchRequestService extends BaseServiceImpl<DispatchRequest, UUID> {

    private final DispatchRequestRepository dispatchRequestRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private AuditLogService auditLogService;

    public DispatchRequestService(DispatchRequestRepository repository) {
        super(repository);
        this.dispatchRequestRepository = repository;
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    @Override
    @Transactional
    public DispatchRequest create(DispatchRequest entity) {
        entity.setReqNumber(generateReqNumber(entity.getOrg().getId()));
        entity.setStatus("new");
        
        com.unitel.fms.backend.dtos.AuthInfo auth = SecurityContextHolder.getAuthInfo();
        if (auth != null && auth.getId() != null) {
            com.unitel.fms.backend.entities.User createdBy = new com.unitel.fms.backend.entities.User();
            createdBy.setId(auth.getId());
            entity.setCreatedBy(createdBy);
        }

        DispatchRequest saved = super.create(entity);
        auditLogService.record("CREATE", "DispatchRequest", saved.getId(), null, saved);
        return saved;
    }

    public List<DispatchRequest> getQueue(UUID orgId) {
        return dispatchRequestRepository.findQueue(orgId);
    }

    @Transactional
    public List<DispatchRequest> bulkUpdate(List<BulkUpdateItem> items) {
        List<DispatchRequest> updatedRequests = new java.util.ArrayList<>();
        
        for (BulkUpdateItem item : items) {
            DispatchRequest req = getOne(item.getId()).orElseThrow();
            
            // Only allow update if it's new
            if (!"new".equals(req.getStatus())) continue;
            
            DispatchRequest updated = updateFromMap(req.getId(), item.getFields());
            updatedRequests.add(updated);
            
            auditLogService.record("BULK_UPDATE", "DispatchRequest", req.getId(), null, updated);
        }
        
        return updatedRequests;
    }

    private String generateReqNumber(UUID orgId) {
        Organization org = organizationService.getOne(orgId).orElseThrow();
        String datePart = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        long seq = dispatchRequestRepository.countByOrgIdAndCreatedAtBetween(orgId, LocalDate.now().atStartOfDay().atOffset(java.time.ZoneOffset.UTC), LocalDate.now().plusDays(1).atStartOfDay().atOffset(java.time.ZoneOffset.UTC)) + 1;
        return String.format("DR-%s-%s-%04d", org.getCode(), datePart, seq);
    }
}

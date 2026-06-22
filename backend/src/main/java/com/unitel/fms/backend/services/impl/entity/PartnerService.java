package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.Partner;
import com.unitel.fms.backend.repositories.PartnerRepository;
import com.unitel.fms.backend.repositories.PartnerVehicleRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PartnerService extends BaseServiceImpl<Partner, UUID> {

    private final PartnerRepository partnerRepository = (PartnerRepository) getRepository();

    @Autowired
    private PartnerVehicleRepository partnerVehicleRepository;

    @Autowired
    private EntityManager entityManager;

    public PartnerService(PartnerRepository repository) {
        super(repository);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    public List<Partner> compareForCriteria(com.unitel.fms.backend.dtos.request.PartnerCriteriaRequest criteria) {
        // Build JPQL to query partners based on criteria
        StringBuilder jpql = new StringBuilder("SELECT DISTINCT p FROM Partner p WHERE p.status = 'active' ");
        
        if (criteria.getOrgId() != null) {
            jpql.append("AND p.org.id = :orgId ");
        }
        if (criteria.getMinOnTimeRate() != null) {
            jpql.append("AND p.onTimeRate >= :minOnTimeRate ");
        }
        if (criteria.getMinRating() != null && !criteria.getMinRating().isEmpty()) {
            // Unrated = 0, Bronze = 1, Silver = 2, Gold = 3 logic could be applied here
            // For simplicity, just exact match or logic handled via mapped integer rating if available
            jpql.append("AND p.rating = :minRating ");
        }

        var query = entityManager.createQuery(jpql.toString(), Partner.class);

        if (criteria.getOrgId() != null) {
            query.setParameter("orgId", criteria.getOrgId());
        }
        if (criteria.getMinOnTimeRate() != null) {
            query.setParameter("minOnTimeRate", criteria.getMinOnTimeRate());
        }
        if (criteria.getMinRating() != null && !criteria.getMinRating().isEmpty()) {
            query.setParameter("minRating", criteria.getMinRating());
        }

        List<Partner> partners = query.getResultList();

        // Further filter by checking if they have vehicles matching requiredLoadCapacity and requiredVehicleType
        if (criteria.getRequiredLoadCapacity() != null || criteria.getRequiredVehicleType() != null) {
            partners.removeIf(partner -> !hasMatchingVehicles(partner, criteria));
        }

        return partners;
    }

    private boolean hasMatchingVehicles(Partner partner, com.unitel.fms.backend.dtos.request.PartnerCriteriaRequest criteria) {
        StringBuilder jpql = new StringBuilder("SELECT COUNT(v) FROM PartnerVehicle v WHERE v.partner.id = :partnerId AND v.status = 'available' ");
        
        if (criteria.getRequiredLoadCapacity() != null) {
            jpql.append("AND v.loadCapacityTon >= :loadCapacity ");
        }
        if (criteria.getRequiredVehicleType() != null && !criteria.getRequiredVehicleType().isEmpty()) {
            // Assuming requiredVehicleType matches VehicleType.code or similar. Let's assume code.
            jpql.append("AND v.vehicleType.code = :vehicleType ");
        }

        var query = entityManager.createQuery(jpql.toString(), Long.class);
        query.setParameter("partnerId", partner.getId());
        
        if (criteria.getRequiredLoadCapacity() != null) {
            query.setParameter("loadCapacity", criteria.getRequiredLoadCapacity());
        }
        if (criteria.getRequiredVehicleType() != null && !criteria.getRequiredVehicleType().isEmpty()) {
            query.setParameter("vehicleType", criteria.getRequiredVehicleType());
        }

        Long count = query.getSingleResult();
        return count > 0;
    }
}

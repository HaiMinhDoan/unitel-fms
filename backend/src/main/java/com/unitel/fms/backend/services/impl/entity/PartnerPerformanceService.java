package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.PartnerPerformance;
import com.unitel.fms.backend.repositories.PartnerPerformanceRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PartnerPerformanceService extends BaseServiceImpl<PartnerPerformance, UUID> {

    private final PartnerPerformanceRepository partnerPerformanceRepository = (PartnerPerformanceRepository) getRepository();

    @Autowired
    private com.unitel.fms.backend.repositories.PartnerRepository partnerRepository;

    @Autowired
    private EntityManager entityManager;

    public PartnerPerformanceService(PartnerPerformanceRepository repository) {
        super(repository);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    @org.springframework.transaction.annotation.Transactional
    public PartnerPerformance recalculateForPartnerAndMonth(UUID partnerId, String periodMonth) {
        // periodMonth format: YYYY-MM
        String startDateStr = periodMonth + "-01T00:00:00Z"; // Approximation
        
        // Count trips where dispatch assignment uses this partner's vehicle or driver
        // For simplicity, we just check partnerVehicle.partner.id
        String countJpql = "SELECT COUNT(t), SUM(CASE WHEN t.slaAtRisk = false THEN 1 ELSE 0 END), SUM(t.totalCost), SUM(t.totalDistanceKm) " +
                           "FROM Trip t " +
                           "WHERE t.dispatchAssignment.partnerVehicle.partner.id = :partnerId " +
                           "AND to_char(t.actualEndAt, 'YYYY-MM') = :periodMonth " +
                           "AND t.status = 'completed'";

        var query = entityManager.createQuery(countJpql, Object[].class);
        query.setParameter("partnerId", partnerId);
        query.setParameter("periodMonth", periodMonth);
        
        Object[] result = null;
        try {
            result = query.getSingleResult();
        } catch (Exception e) {
            // No trips found
        }

        int totalTrips = 0;
        int onTimeTrips = 0;
        java.math.BigDecimal totalCost = java.math.BigDecimal.ZERO;
        java.math.BigDecimal totalDistance = java.math.BigDecimal.ZERO;

        if (result != null && result[0] != null) {
            totalTrips = ((Number) result[0]).intValue();
            if (result[1] != null) {
                onTimeTrips = ((Number) result[1]).intValue();
            }
            if (result[2] != null) {
                totalCost = new java.math.BigDecimal(result[2].toString());
            }
            if (result[3] != null) {
                totalDistance = new java.math.BigDecimal(result[3].toString());
            }
        }

        // Revenue logic could be some multiplier, e.g., totalDistance * costPerKm
        java.math.BigDecimal totalRevenue = totalDistance.multiply(new java.math.BigDecimal("10.0")); // Fake logic

        // Find existing performance record
        String findJpql = "SELECT pp FROM PartnerPerformance pp WHERE pp.partner.id = :partnerId AND pp.periodMonth = :periodMonth";
        var findQuery = entityManager.createQuery(findJpql, PartnerPerformance.class);
        findQuery.setParameter("partnerId", partnerId);
        findQuery.setParameter("periodMonth", periodMonth);
        
        PartnerPerformance pp = null;
        try {
            pp = findQuery.getSingleResult();
        } catch (Exception e) {
            // Not found, create new
            pp = new PartnerPerformance();
            pp.setPartner(partnerRepository.findById(partnerId).orElseThrow(() -> new RuntimeException("Partner not found")));
            pp.setPeriodMonth(periodMonth);
            pp.setStatus("final");
            pp.setCreatedAt(java.time.OffsetDateTime.now());
        }

        pp.setTotalTrips(totalTrips);
        pp.setOnTimeTrips(onTimeTrips);
        pp.setAvgCost(totalTrips > 0 ? totalCost.divide(new java.math.BigDecimal(totalTrips), 2, java.math.RoundingMode.HALF_UP) : java.math.BigDecimal.ZERO);
        pp.setTotalRevenue(totalRevenue);
        pp.setUpdatedAt(java.time.OffsetDateTime.now());

        if (pp.getId() == null) {
            entityManager.persist(pp);
        } else {
            entityManager.merge(pp);
        }

        return pp;
    }
}

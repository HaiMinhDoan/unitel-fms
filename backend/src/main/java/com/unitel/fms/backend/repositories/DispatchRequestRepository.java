package com.unitel.fms.backend.repositories;

import com.unitel.fms.backend.entities.DispatchRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface DispatchRequestRepository extends JpaRepository<DispatchRequest, UUID>, JpaSpecificationExecutor<DispatchRequest> {
    boolean existsByCustomerId(UUID customerId);

    long countByOrgIdAndCreatedAtBetween(UUID orgId, OffsetDateTime start, OffsetDateTime end);

    @Query("""
        SELECT r FROM DispatchRequest r
        WHERE r.org.id = :orgId AND r.status IN ('new', 'processing')
        ORDER BY
            CASE r.priority WHEN 'high' THEN 0 WHEN 'medium' THEN 1 ELSE 2 END,
            r.requestedPickupAt ASC NULLS LAST
        """)
    List<DispatchRequest> findQueue(@Param("orgId") UUID orgId);
}

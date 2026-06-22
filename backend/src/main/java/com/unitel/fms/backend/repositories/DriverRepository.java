package com.unitel.fms.backend.repositories;

import com.unitel.fms.backend.entities.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DriverRepository extends JpaRepository<Driver, UUID>, JpaSpecificationExecutor<Driver> {

    Optional<Driver> findByUserId(UUID userId);

    @Query("""
        SELECT d FROM Driver d
        WHERE d.org.id = :orgId
          AND d.status = 'active'
          AND d.isDispatchEligible = true
          AND NOT EXISTS (
              SELECT 1 FROM DispatchAssignment da
              WHERE da.driver.id = d.id AND da.status = 'active'
          )
        """)
    List<Driver> findAvailableForDispatch(@Param("orgId") UUID orgId);
}

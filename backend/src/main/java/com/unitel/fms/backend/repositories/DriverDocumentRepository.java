package com.unitel.fms.backend.repositories;

import com.unitel.fms.backend.entities.DriverDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface DriverDocumentRepository extends JpaRepository<DriverDocument, UUID>, JpaSpecificationExecutor<DriverDocument> {

    List<DriverDocument> findByDriverOrgIdAndExpiryDateBetweenAndStatusNot(UUID orgId, LocalDate from, LocalDate to, String status);

    boolean existsByDriverIdAndStatus(UUID driverId, String status);

    @Modifying
    @Query("UPDATE DriverDocument d SET d.status = 'expiring_soon' WHERE d.expiryDate > :now AND d.expiryDate <= :threshold AND d.status = 'valid'")
    void bulkUpdateStatusExpiringSoon(@Param("now") LocalDate now, @Param("threshold") LocalDate threshold);

    @Modifying
    @Query("UPDATE DriverDocument d SET d.status = 'expired' WHERE d.expiryDate <= :now AND d.status IN ('valid', 'expiring_soon')")
    void bulkUpdateStatusExpired(@Param("now") LocalDate now);
}

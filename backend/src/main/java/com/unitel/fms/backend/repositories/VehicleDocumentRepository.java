package com.unitel.fms.backend.repositories;

import com.unitel.fms.backend.entities.VehicleDocument;
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
public interface VehicleDocumentRepository extends JpaRepository<VehicleDocument, UUID>, JpaSpecificationExecutor<VehicleDocument> {
    List<VehicleDocument> findByVehicleOrgIdAndExpiryDateBetweenAndStatusNot(UUID orgId, LocalDate from, LocalDate to, String status);

    boolean existsByVehicleIdAndStatus(UUID vehicleId, String status);

    @Modifying
    @Query("UPDATE VehicleDocument d SET d.status = 'expiring_soon' WHERE d.expiryDate > :today AND d.expiryDate <= :threshold AND d.status = 'valid'")
    void bulkUpdateStatusExpiringSoon(@Param("today") LocalDate today, @Param("threshold") LocalDate threshold);

    @Modifying
    @Query("UPDATE VehicleDocument d SET d.status = 'expired' WHERE d.expiryDate <= :today AND d.status != 'expired'")
    void bulkUpdateStatusExpired(@Param("today") LocalDate today);
}

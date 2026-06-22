package com.unitel.fms.backend.repositories;

import com.unitel.fms.backend.entities.VehicleHealth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VehicleHealthRepository extends JpaRepository<VehicleHealth, UUID>, JpaSpecificationExecutor<VehicleHealth> {
    Optional<VehicleHealth> findFirstByVehicleIdOrderByCreatedAtDesc(UUID vehicleId);
}

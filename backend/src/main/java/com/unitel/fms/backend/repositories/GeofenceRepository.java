package com.unitel.fms.backend.repositories;

import com.unitel.fms.backend.entities.Geofence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GeofenceRepository extends JpaRepository<Geofence, UUID>, JpaSpecificationExecutor<Geofence> {
}

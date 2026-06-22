package com.unitel.fms.backend.repositories;

import com.unitel.fms.backend.entities.GpsPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.unitel.fms.backend.entities.GpsPositionId;

import java.util.List;
import java.util.UUID;

@Repository
public interface GpsPositionRepository extends JpaRepository<GpsPosition, GpsPositionId>, JpaSpecificationExecutor<GpsPosition> {
    List<GpsPosition> findById_TripIdOrderById_RecordedAtAsc(UUID tripId);
}

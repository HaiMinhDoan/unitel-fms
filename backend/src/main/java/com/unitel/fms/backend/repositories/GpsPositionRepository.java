package com.unitel.fms.backend.repositories;

import com.unitel.fms.backend.entities.GpsPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.unitel.fms.backend.entities.GpsPositionId;

@Repository
public interface GpsPositionRepository extends JpaRepository<GpsPosition, GpsPositionId>, JpaSpecificationExecutor<GpsPosition> {
}

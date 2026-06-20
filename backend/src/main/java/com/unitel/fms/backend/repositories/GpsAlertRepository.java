package com.unitel.fms.backend.repositories;

import com.unitel.fms.backend.entities.GpsAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GpsAlertRepository extends JpaRepository<GpsAlert, UUID>, JpaSpecificationExecutor<GpsAlert> {
}

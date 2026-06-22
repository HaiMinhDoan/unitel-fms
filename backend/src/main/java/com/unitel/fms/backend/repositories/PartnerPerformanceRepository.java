package com.unitel.fms.backend.repositories;

import com.unitel.fms.backend.entities.PartnerPerformance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PartnerPerformanceRepository
        extends JpaRepository<PartnerPerformance, UUID>, JpaSpecificationExecutor<PartnerPerformance> {
}

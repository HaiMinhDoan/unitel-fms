package com.unitel.fms.backend.repositories;

import com.unitel.fms.backend.entities.SpatialRefSy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface SpatialRefSyRepository extends JpaRepository<SpatialRefSy, Integer>, JpaSpecificationExecutor<SpatialRefSy> {
}

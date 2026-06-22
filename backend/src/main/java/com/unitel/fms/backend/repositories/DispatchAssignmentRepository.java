package com.unitel.fms.backend.repositories;

import com.unitel.fms.backend.entities.DispatchAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface DispatchAssignmentRepository extends JpaRepository<DispatchAssignment, UUID>, JpaSpecificationExecutor<DispatchAssignment> {
    
    @Query("SELECT COUNT(da) = 0 FROM DispatchAssignment da " +
           "JOIN Trip t ON t.dispatchAssignment = da " +
           "WHERE da.vehicle.id = :vehicleId AND " +
           "da.status = 'active' AND " +
           "((t.plannedStartAt BETWEEN :start AND :end) OR " +
           "(t.plannedEndAt BETWEEN :start AND :end))")
    boolean isVehicleAvailableForDispatch(@Param("vehicleId") UUID vehicleId, 
                                          @Param("start") java.time.OffsetDateTime start, 
                                          @Param("end") java.time.OffsetDateTime end);

    @Query("SELECT COUNT(da) = 0 FROM DispatchAssignment da " +
           "JOIN Trip t ON t.dispatchAssignment = da " +
           "WHERE da.driver.id = :driverId AND " +
           "da.status = 'active' AND " +
           "((t.plannedStartAt BETWEEN :start AND :end) OR " +
           "(t.plannedEndAt BETWEEN :start AND :end))")
    boolean isDriverAvailableForDispatch(@Param("driverId") UUID driverId, 
                                         @Param("start") java.time.OffsetDateTime start, 
                                         @Param("end") java.time.OffsetDateTime end);
}

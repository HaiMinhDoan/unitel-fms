package com.unitel.fms.backend.repositories;

import com.unitel.fms.backend.entities.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, UUID>, JpaSpecificationExecutor<Vehicle> {
    boolean existsByOrgId(UUID orgId);

    boolean existsByPlateNumber(String plateNumber);

    @Query("""
            SELECT v FROM Vehicle v
            WHERE v.org.id = :orgId
              AND v.status = 'active'
              AND NOT EXISTS (
                  SELECT 1 FROM DispatchAssignment da
                  WHERE da.vehicle.id = v.id AND da.status = 'active'
              )
              AND NOT EXISTS (
                  SELECT 1 FROM VehicleHealth vh
                  WHERE vh.vehicle.id = v.id AND vh.isDispatchBlocked = true
                  AND vh.createdAt = (SELECT MAX(vh2.createdAt) FROM VehicleHealth vh2 WHERE vh2.vehicle.id = v.id)
              )
            """)
    List<Vehicle> findAvailableForDispatch(@Param("orgId") UUID orgId);

    @Query(value = """
            SELECT v.* FROM vehicles v
            WHERE v.id IN :eligibleVehicleIds
            ORDER BY ST_Distance(
                ST_MakePoint(v.last_known_lng, v.last_known_lat)::geography,
                ST_MakePoint(:originLng, :originLat)::geography
            ) ASC
            LIMIT :limit
            """, nativeQuery = true)
    List<Vehicle> findSuggestedVehicles(
            @Param("eligibleVehicleIds") List<UUID> eligibleVehicleIds,
            @Param("originLat") java.math.BigDecimal originLat,
            @Param("originLng") java.math.BigDecimal originLng,
            @Param("limit") int limit);
}

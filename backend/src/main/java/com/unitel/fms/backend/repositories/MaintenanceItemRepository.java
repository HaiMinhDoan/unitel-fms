package com.unitel.fms.backend.repositories;

import com.unitel.fms.backend.entities.MaintenanceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface MaintenanceItemRepository extends JpaRepository<MaintenanceItem, UUID>, JpaSpecificationExecutor<MaintenanceItem> {
    List<MaintenanceItem> findByMaintenanceOrderId(UUID maintenanceOrderId);

    @Query("SELECT COALESCE(SUM(m.totalCost), 0) FROM MaintenanceItem m WHERE m.maintenanceOrder.id = :orderId")
    BigDecimal sumTotalCostByOrderId(@Param("orderId") UUID orderId);
}

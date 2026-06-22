package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.MaintenanceOrder;
import com.unitel.fms.backend.repositories.MaintenanceOrderRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

import com.unitel.fms.backend.repositories.MaintenanceItemRepository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class MaintenanceOrderService extends BaseServiceImpl<MaintenanceOrder, UUID> {

    private final MaintenanceOrderRepository maintenanceOrderRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MaintenanceItemRepository maintenanceItemRepository;

    @Autowired
    private NotificationService notificationService;

    public MaintenanceOrderService(MaintenanceOrderRepository repository) {
        super(repository);
        this.maintenanceOrderRepository = repository;
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    @Override
    @Transactional
    public MaintenanceOrder changeStatus(UUID id, String status) {
        MaintenanceOrder order = super.changeStatus(id, status);

        if ("completed".equals(status)) {
            BigDecimal total = maintenanceItemRepository.sumTotalCostByOrderId(id);
            order.setTotalCost(total);
            order.setCompletedDate(LocalDate.now());
            maintenanceOrderRepository.save(order);

            notificationService.notifyFleetManagers(order.getVehicle().getId(),
                    "Bảo trì hoàn tất — cần đánh giá lại sức khoẻ xe trước khi điều phối");
        }
        return order;
    }
}

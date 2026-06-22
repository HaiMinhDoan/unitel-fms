package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.Vehicle;
import com.unitel.fms.backend.repositories.VehicleRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import com.unitel.fms.backend.exceptions.customize.ConflictException;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import com.unitel.fms.backend.dtos.response.DispatchEligibilityResult;
import com.unitel.fms.backend.exceptions.customize.NotFoundException;
import com.unitel.fms.backend.exceptions.customize.InvalidFieldException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class VehicleService extends BaseServiceImpl<Vehicle, UUID> {

    private final VehicleRepository vehicleRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private VehicleDocumentService vehicleDocumentService;

    @Autowired
    private VehicleHealthService vehicleHealthService;

    public VehicleService(VehicleRepository repository) {
        super(repository);
        this.vehicleRepository = repository;
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    @Override
    @Transactional
    public Vehicle create(Vehicle entity) {
        if (vehicleRepository.existsByPlateNumber(entity.getPlateNumber())) {
            throw new ConflictException("VEHICLE_EXISTS");
        }
        return super.create(entity);
    }

    @Override
    @Transactional
    public Vehicle updateFromMap(UUID id, Map<String, Object> updates) {
        Vehicle vehicle = getOne(id).orElseThrow(() -> new NotFoundException("Không tìm thấy phương tiện"));
        
        if (updates.containsKey("currentOdometer")) {
            BigDecimal newOdo = new BigDecimal(updates.get("currentOdometer").toString());
            if (vehicle.getCurrentOdometer() != null && newOdo.compareTo(vehicle.getCurrentOdometer()) < 0) {
                throw new InvalidFieldException("INVALID_ODOMETER_VALUE");
            }
        }
        return super.updateFromMap(id, updates);
    }

    public List<Vehicle> findAvailableForDispatch(UUID orgId) {
        return vehicleRepository.findAvailableForDispatch(orgId);
    }

    public DispatchEligibilityResult checkDispatchEligibility(UUID vehicleId) {
        Vehicle vehicle = getOne(vehicleId).orElseThrow(() -> new NotFoundException("Xe không tồn tại"));

        boolean vehicleDocsOk = vehicleDocumentService.hasNoExpiredOrMissingDocs(vehicleId);
        boolean healthOk = vehicleHealthService.getLatestHealth(vehicleId)
                .map(h -> !h.getIsDispatchBlocked())
                .orElse(false);

        return DispatchEligibilityResult.builder()
                .vehicleDocsOk(vehicleDocsOk)
                .healthOk(healthOk)
                .eligible(vehicleDocsOk && healthOk)
                .build();
    }
}

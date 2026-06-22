package com.unitel.fms.backend.controllers;

import com.unitel.fms.backend.dtos.request.BaseFilterRequest;
import com.unitel.fms.backend.dtos.request.MaintenanceOrderRequest;
import com.unitel.fms.backend.dtos.response.ResponseData;
import com.unitel.fms.backend.dtos.response.MaintenanceOrderResponse;
import com.unitel.fms.backend.entities.MaintenanceOrder;
import com.unitel.fms.backend.customizeanotations.RequireAuth;
import com.unitel.fms.backend.mappers.MaintenanceOrderMapper;
import com.unitel.fms.backend.services.impl.entity.MaintenanceOrderService;
import com.unitel.fms.backend.constants.enums.RoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class MaintenanceOrderController {

    @Autowired
    private MaintenanceOrderService maintenanceOrderService;

    @Autowired
    private MaintenanceOrderMapper maintenanceOrderMapper;

    @PostMapping("/maintenance-order/create")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<MaintenanceOrderResponse> create(@RequestBody MaintenanceOrderRequest request) {
        MaintenanceOrder entity = maintenanceOrderMapper.toEntity(request);
        MaintenanceOrder saved = maintenanceOrderService.create(entity);
        return ResponseData.<MaintenanceOrderResponse>builder().status(200).messageCode("SUCCESS").data(maintenanceOrderMapper.toResponse(saved)).build();
    }

    @PutMapping("/maintenance-order/update/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<MaintenanceOrderResponse> update(@PathVariable UUID id, @RequestBody MaintenanceOrderRequest request) {
        MaintenanceOrder entity = maintenanceOrderMapper.toEntity(request);
        MaintenanceOrder updated = maintenanceOrderService.update(id, entity);
        return ResponseData.<MaintenanceOrderResponse>builder().status(200).messageCode("SUCCESS").data(maintenanceOrderMapper.toResponse(updated)).build();
    }

    @PatchMapping("/maintenance-order/update-partial/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<MaintenanceOrderResponse> updatePartial(@PathVariable UUID id, @RequestBody Map<String, Object> updates) {
        MaintenanceOrder updated = maintenanceOrderService.updateFromMap(id, updates);
        return ResponseData.<MaintenanceOrderResponse>builder().status(200).messageCode("SUCCESS").data(maintenanceOrderMapper.toResponse(updated)).build();
    }

    @GetMapping("/maintenance-order/get-by-id/{id}")
    @RequireAuth(roles = {RoleType.ALL}, inWorkspace = true)
    public ResponseData<MaintenanceOrderResponse> getById(@PathVariable UUID id) {
        MaintenanceOrder entity = maintenanceOrderService.getOne(id).orElse(null);
        return ResponseData.<MaintenanceOrderResponse>builder().status(200).messageCode("SUCCESS").data(entity != null ? maintenanceOrderMapper.toResponse(entity) : null).build();
    }

    @PostMapping("/maintenance-orders/filter")
    @RequireAuth(roles = {RoleType.ALL}, inWorkspace = true)
    public ResponseData<Page<MaintenanceOrderResponse>> filter(@RequestBody BaseFilterRequest filter) {
        Page<MaintenanceOrderResponse> page = maintenanceOrderService.filter(filter).map(maintenanceOrderMapper::toResponse);
        return ResponseData.<Page<MaintenanceOrderResponse>>builder().status(200).messageCode("SUCCESS").data(page).build();
    }

    @PatchMapping("/maintenance-order/change-status/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<MaintenanceOrderResponse> changeStatus(@PathVariable UUID id, @RequestParam String status) {
        MaintenanceOrder updated = maintenanceOrderService.changeStatus(id, status);
        return ResponseData.<MaintenanceOrderResponse>builder().status(200).messageCode("SUCCESS").data(maintenanceOrderMapper.toResponse(updated)).build();
    }

    @DeleteMapping("/maintenance-order/soft-delete/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<Void> softDelete(@PathVariable UUID id) {
        maintenanceOrderService.changeStatus(id, "deleted");
        return ResponseData.<Void>builder().status(200).messageCode("SUCCESS").build();
    }

    @DeleteMapping("/maintenance-order/hard-delete/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN}, inWorkspace = true)
    public ResponseData<Void> hardDelete(@PathVariable UUID id) {
        maintenanceOrderService.delete(id);
        return ResponseData.<Void>builder().status(200).messageCode("SUCCESS").build();
    }
}

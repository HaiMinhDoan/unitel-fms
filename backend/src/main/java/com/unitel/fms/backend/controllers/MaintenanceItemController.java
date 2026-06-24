package com.unitel.fms.backend.controllers;

import com.unitel.fms.backend.dtos.request.BaseFilterRequest;
import com.unitel.fms.backend.dtos.request.MaintenanceItemRequest;
import com.unitel.fms.backend.dtos.response.ResponseData;
import com.unitel.fms.backend.dtos.response.MaintenanceItemResponse;
import com.unitel.fms.backend.entities.MaintenanceItem;
import com.unitel.fms.backend.customizeanotations.RequireAuth;
import com.unitel.fms.backend.mappers.MaintenanceItemMapper;
import com.unitel.fms.backend.services.impl.entity.MaintenanceItemService;
import com.unitel.fms.backend.constants.enums.RoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class MaintenanceItemController {

    @Autowired
    private MaintenanceItemService maintenanceItemService;

    @Autowired
    private MaintenanceItemMapper maintenanceItemMapper;

    @PostMapping("/maintenance-item/create")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<MaintenanceItemResponse> create(@Valid @RequestBody MaintenanceItemRequest request) {
        MaintenanceItem entity = maintenanceItemMapper.toEntity(request);
        MaintenanceItem saved = maintenanceItemService.create(entity);
        return ResponseData.<MaintenanceItemResponse>builder().status(200).messageCode("SUCCESS").data(maintenanceItemMapper.toResponse(saved)).build();
    }

    @PutMapping("/maintenance-item/update/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<MaintenanceItemResponse> update(@PathVariable UUID id, @Valid @RequestBody MaintenanceItemRequest request) {
        MaintenanceItem entity = maintenanceItemMapper.toEntity(request);
        MaintenanceItem updated = maintenanceItemService.update(id, entity);
        return ResponseData.<MaintenanceItemResponse>builder().status(200).messageCode("SUCCESS").data(maintenanceItemMapper.toResponse(updated)).build();
    }

    @PatchMapping("/maintenance-item/update-partial/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<MaintenanceItemResponse> updatePartial(@PathVariable UUID id, @Valid @RequestBody Map<String, Object> updates) {
        MaintenanceItem updated = maintenanceItemService.updateFromMap(id, updates);
        return ResponseData.<MaintenanceItemResponse>builder().status(200).messageCode("SUCCESS").data(maintenanceItemMapper.toResponse(updated)).build();
    }

    @GetMapping("/maintenance-item/get-by-id/{id}")
    @RequireAuth(roles = {RoleType.ALL}, inWorkspace = true)
    public ResponseData<MaintenanceItemResponse> getById(@PathVariable UUID id) {
        MaintenanceItem entity = maintenanceItemService.getOne(id).orElse(null);
        return ResponseData.<MaintenanceItemResponse>builder().status(200).messageCode("SUCCESS").data(entity != null ? maintenanceItemMapper.toResponse(entity) : null).build();
    }

    @PostMapping("/maintenance-items/filter")
    @RequireAuth(roles = {RoleType.ALL}, inWorkspace = true)
    public ResponseData<Page<MaintenanceItemResponse>> filter(@Valid @RequestBody BaseFilterRequest filter) {
        Page<MaintenanceItemResponse> page = maintenanceItemService.filter(filter).map(maintenanceItemMapper::toResponse);
        return ResponseData.<Page<MaintenanceItemResponse>>builder().status(200).messageCode("SUCCESS").data(page).build();
    }

    @PatchMapping("/maintenance-item/change-status/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<MaintenanceItemResponse> changeStatus(@PathVariable UUID id, @RequestParam String status) {
        MaintenanceItem updated = maintenanceItemService.changeStatus(id, status);
        return ResponseData.<MaintenanceItemResponse>builder().status(200).messageCode("SUCCESS").data(maintenanceItemMapper.toResponse(updated)).build();
    }

    @DeleteMapping("/maintenance-item/soft-delete/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<Void> softDelete(@PathVariable UUID id) {
        maintenanceItemService.changeStatus(id, "deleted");
        return ResponseData.<Void>builder().status(200).messageCode("SUCCESS").build();
    }

    @DeleteMapping("/maintenance-item/hard-delete/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN}, inWorkspace = true)
    public ResponseData<Void> hardDelete(@PathVariable UUID id) {
        maintenanceItemService.delete(id);
        return ResponseData.<Void>builder().status(200).messageCode("SUCCESS").build();
    }

    @GetMapping("/maintenance-items/get-by-order/{orderId}")
    @RequireAuth(roles = {RoleType.FLEET_MANAGER, RoleType.OPS_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<java.util.List<MaintenanceItemResponse>> getByOrder(@PathVariable UUID orderId) {
        java.util.List<MaintenanceItemResponse> list = maintenanceItemService.findByMaintenanceOrderId(orderId).stream()
                .map(maintenanceItemMapper::toResponse).toList();
        return ResponseData.<java.util.List<MaintenanceItemResponse>>builder().status(200).messageCode("SUCCESS").data(list).build();
    }
}

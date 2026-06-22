package com.unitel.fms.backend.controllers;

import com.unitel.fms.backend.dtos.request.BaseFilterRequest;
import com.unitel.fms.backend.dtos.request.VehicleHealthRequest;
import com.unitel.fms.backend.dtos.response.ResponseData;
import com.unitel.fms.backend.dtos.response.VehicleHealthResponse;
import com.unitel.fms.backend.entities.VehicleHealth;
import com.unitel.fms.backend.customizeanotations.RequireAuth;
import com.unitel.fms.backend.mappers.VehicleHealthMapper;
import com.unitel.fms.backend.services.impl.entity.VehicleHealthService;
import com.unitel.fms.backend.constants.enums.RoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class VehicleHealthController {

    @Autowired
    private VehicleHealthService vehicleHealthService;

    @Autowired
    private VehicleHealthMapper vehicleHealthMapper;

    @PostMapping("/vehicle-health/create")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<VehicleHealthResponse> create(@RequestBody VehicleHealthRequest request) {
        VehicleHealth entity = vehicleHealthMapper.toEntity(request);
        VehicleHealth saved = vehicleHealthService.create(entity);
        return ResponseData.<VehicleHealthResponse>builder().status(200).messageCode("SUCCESS").data(vehicleHealthMapper.toResponse(saved)).build();
    }

    @PutMapping("/vehicle-health/update/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<VehicleHealthResponse> update(@PathVariable UUID id, @RequestBody VehicleHealthRequest request) {
        VehicleHealth entity = vehicleHealthMapper.toEntity(request);
        VehicleHealth updated = vehicleHealthService.update(id, entity);
        return ResponseData.<VehicleHealthResponse>builder().status(200).messageCode("SUCCESS").data(vehicleHealthMapper.toResponse(updated)).build();
    }

    @PatchMapping("/vehicle-health/update-partial/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<VehicleHealthResponse> updatePartial(@PathVariable UUID id, @RequestBody Map<String, Object> updates) {
        VehicleHealth updated = vehicleHealthService.updateFromMap(id, updates);
        return ResponseData.<VehicleHealthResponse>builder().status(200).messageCode("SUCCESS").data(vehicleHealthMapper.toResponse(updated)).build();
    }

    @GetMapping("/vehicle-health/get-by-id/{id}")
    @RequireAuth(roles = {RoleType.ALL}, inWorkspace = true)
    public ResponseData<VehicleHealthResponse> getById(@PathVariable UUID id) {
        VehicleHealth entity = vehicleHealthService.getOne(id).orElse(null);
        return ResponseData.<VehicleHealthResponse>builder().status(200).messageCode("SUCCESS").data(entity != null ? vehicleHealthMapper.toResponse(entity) : null).build();
    }

    @PostMapping("/vehicle-healths/filter")
    @RequireAuth(roles = {RoleType.ALL}, inWorkspace = true)
    public ResponseData<Page<VehicleHealthResponse>> filter(@RequestBody BaseFilterRequest filter) {
        Page<VehicleHealthResponse> page = vehicleHealthService.filter(filter).map(vehicleHealthMapper::toResponse);
        return ResponseData.<Page<VehicleHealthResponse>>builder().status(200).messageCode("SUCCESS").data(page).build();
    }

    @PatchMapping("/vehicle-health/change-status/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<VehicleHealthResponse> changeStatus(@PathVariable UUID id, @RequestParam String status) {
        VehicleHealth updated = vehicleHealthService.changeStatus(id, status);
        return ResponseData.<VehicleHealthResponse>builder().status(200).messageCode("SUCCESS").data(vehicleHealthMapper.toResponse(updated)).build();
    }

    @DeleteMapping("/vehicle-health/soft-delete/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<Void> softDelete(@PathVariable UUID id) {
        vehicleHealthService.changeStatus(id, "deleted");
        return ResponseData.<Void>builder().status(200).messageCode("SUCCESS").build();
    }

    @DeleteMapping("/vehicle-health/hard-delete/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN}, inWorkspace = true)
    public ResponseData<Void> hardDelete(@PathVariable UUID id) {
        vehicleHealthService.delete(id);
        return ResponseData.<Void>builder().status(200).messageCode("SUCCESS").build();
    }

    @GetMapping("/vehicle-health/get-latest/{vehicleId}")
    @RequireAuth(roles = {RoleType.ALL}, inWorkspace = true)
    public ResponseData<VehicleHealthResponse> getLatest(@PathVariable UUID vehicleId) {
        VehicleHealth entity = vehicleHealthService.getLatestHealth(vehicleId).orElse(null);
        return ResponseData.<VehicleHealthResponse>builder().status(200).messageCode("SUCCESS").data(entity != null ? vehicleHealthMapper.toResponse(entity) : null).build();
    }

    @GetMapping("/vehicle-healths/get-history/{vehicleId}")
    @RequireAuth(roles = {RoleType.FLEET_MANAGER}, inWorkspace = true)
    public ResponseData<java.util.List<VehicleHealthResponse>> getHistory(@PathVariable UUID vehicleId) {
        BaseFilterRequest filter = new BaseFilterRequest();
        filter.getFilters().add(com.unitel.fms.backend.dtos.request.FilterCriteria.builder()
                .fieldName("vehicle.id")
                .operation(com.unitel.fms.backend.constants.enums.FilterOperation.EQUALS)
                .value(vehicleId.toString())
                .build());
        // Should also probably add sort by createdAt desc if we implement sorting in BaseFilterRequest
        java.util.List<VehicleHealthResponse> list = vehicleHealthService.filter(filter).getContent().stream()
                .map(vehicleHealthMapper::toResponse).toList();
        return ResponseData.<java.util.List<VehicleHealthResponse>>builder().status(200).messageCode("SUCCESS").data(list).build();
    }
}

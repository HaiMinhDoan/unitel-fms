package com.unitel.fms.backend.controllers;

import com.unitel.fms.backend.dtos.request.BaseFilterRequest;
import com.unitel.fms.backend.dtos.request.VehicleRequest;
import com.unitel.fms.backend.dtos.response.ResponseData;
import com.unitel.fms.backend.dtos.response.VehicleResponse;
import com.unitel.fms.backend.entities.Vehicle;
import com.unitel.fms.backend.customizeanotations.RequireAuth;
import com.unitel.fms.backend.mappers.VehicleMapper;
import com.unitel.fms.backend.services.impl.entity.VehicleService;
import com.unitel.fms.backend.constants.enums.RoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private VehicleMapper vehicleMapper;

    @PostMapping("/vehicle/create")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<VehicleResponse> create(@RequestBody VehicleRequest request) {
        Vehicle entity = vehicleMapper.toEntity(request);
        Vehicle saved = vehicleService.create(entity);
        return ResponseData.<VehicleResponse>builder().status(200).message("Thành công").data(vehicleMapper.toResponse(saved)).build();
    }

    @PutMapping("/vehicle/update/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<VehicleResponse> update(@PathVariable UUID id, @RequestBody VehicleRequest request) {
        Vehicle entity = vehicleMapper.toEntity(request);
        Vehicle updated = vehicleService.update(id, entity);
        return ResponseData.<VehicleResponse>builder().status(200).message("Thành công").data(vehicleMapper.toResponse(updated)).build();
    }

    @PatchMapping("/vehicle/update-partial/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<VehicleResponse> updatePartial(@PathVariable UUID id, @RequestBody Map<String, Object> updates) {
        Vehicle updated = vehicleService.updateFromMap(id, updates);
        return ResponseData.<VehicleResponse>builder().status(200).message("Thành công").data(vehicleMapper.toResponse(updated)).build();
    }

    @GetMapping("/vehicle/get-by-id/{id}")
    @RequireAuth(roles = {RoleType.ALL}, inWorkspace = true)
    public ResponseData<VehicleResponse> getById(@PathVariable UUID id) {
        Vehicle entity = vehicleService.getOne(id).orElse(null);
        return ResponseData.<VehicleResponse>builder().status(200).message("Thành công").data(entity != null ? vehicleMapper.toResponse(entity) : null).build();
    }

    @PostMapping("/vehicles/filter")
    @RequireAuth(roles = {RoleType.ALL}, inWorkspace = true)
    public ResponseData<Page<VehicleResponse>> filter(@RequestBody BaseFilterRequest filter) {
        Page<VehicleResponse> page = vehicleService.filter(filter).map(vehicleMapper::toResponse);
        return ResponseData.<Page<VehicleResponse>>builder().status(200).message("Thành công").data(page).build();
    }

    @PatchMapping("/vehicle/change-status/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<VehicleResponse> changeStatus(@PathVariable UUID id, @RequestParam String status) {
        Vehicle updated = vehicleService.changeStatus(id, status);
        return ResponseData.<VehicleResponse>builder().status(200).message("Thành công").data(vehicleMapper.toResponse(updated)).build();
    }

    @DeleteMapping("/vehicle/soft-delete/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<Void> softDelete(@PathVariable UUID id) {
        vehicleService.changeStatus(id, "deleted");
        return ResponseData.<Void>builder().status(200).message("Thành công").build();
    }

    @DeleteMapping("/vehicle/hard-delete/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN}, inWorkspace = true)
    public ResponseData<Void> hardDelete(@PathVariable UUID id) {
        vehicleService.delete(id);
        return ResponseData.<Void>builder().status(200).message("Thành công").build();
    }

    @GetMapping("/vehicles/get-available-for-dispatch")
    @RequireAuth(roles = {RoleType.DISPATCHER}, inWorkspace = true)
    public ResponseData<List<VehicleResponse>> getAvailableForDispatch() {
        UUID orgId = com.unitel.fms.backend.contexts.SecurityContextHolder.getAuthInfo().getOrgId();
        List<VehicleResponse> list = vehicleService.findAvailableForDispatch(orgId).stream()
                .map(vehicleMapper::toResponse).toList();
        return ResponseData.<List<VehicleResponse>>builder().status(200).message("Thành công").data(list).build();
    }

    @GetMapping("/vehicle/{id}/check-dispatch-eligibility")
    @RequireAuth(roles = {RoleType.DISPATCHER}, inWorkspace = true)
    public ResponseData<com.unitel.fms.backend.dtos.response.DispatchEligibilityResult> checkDispatchEligibility(@PathVariable UUID id) {
        com.unitel.fms.backend.dtos.response.DispatchEligibilityResult result = vehicleService.checkDispatchEligibility(id);
        return ResponseData.<com.unitel.fms.backend.dtos.response.DispatchEligibilityResult>builder().status(200).message("Thành công").data(result).build();
    }
}

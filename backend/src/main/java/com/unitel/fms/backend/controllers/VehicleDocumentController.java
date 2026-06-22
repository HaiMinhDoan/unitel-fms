package com.unitel.fms.backend.controllers;

import com.unitel.fms.backend.dtos.request.BaseFilterRequest;
import com.unitel.fms.backend.dtos.request.VehicleDocumentRequest;
import com.unitel.fms.backend.dtos.response.ResponseData;
import com.unitel.fms.backend.dtos.response.VehicleDocumentResponse;
import com.unitel.fms.backend.entities.VehicleDocument;
import com.unitel.fms.backend.customizeanotations.RequireAuth;
import com.unitel.fms.backend.mappers.VehicleDocumentMapper;
import com.unitel.fms.backend.services.impl.entity.VehicleDocumentService;
import com.unitel.fms.backend.constants.enums.RoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class VehicleDocumentController {

    @Autowired
    private VehicleDocumentService vehicleDocumentService;

    @Autowired
    private VehicleDocumentMapper vehicleDocumentMapper;

    @PostMapping("/vehicle-document/create")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<VehicleDocumentResponse> create(@RequestBody VehicleDocumentRequest request) {
        VehicleDocument entity = vehicleDocumentMapper.toEntity(request);
        VehicleDocument saved = vehicleDocumentService.create(entity);
        return ResponseData.<VehicleDocumentResponse>builder().status(200).message("Thành công").data(vehicleDocumentMapper.toResponse(saved)).build();
    }

    @PutMapping("/vehicle-document/update/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<VehicleDocumentResponse> update(@PathVariable UUID id, @RequestBody VehicleDocumentRequest request) {
        VehicleDocument entity = vehicleDocumentMapper.toEntity(request);
        VehicleDocument updated = vehicleDocumentService.update(id, entity);
        return ResponseData.<VehicleDocumentResponse>builder().status(200).message("Thành công").data(vehicleDocumentMapper.toResponse(updated)).build();
    }

    @PatchMapping("/vehicle-document/update-partial/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<VehicleDocumentResponse> updatePartial(@PathVariable UUID id, @RequestBody Map<String, Object> updates) {
        VehicleDocument updated = vehicleDocumentService.updateFromMap(id, updates);
        return ResponseData.<VehicleDocumentResponse>builder().status(200).message("Thành công").data(vehicleDocumentMapper.toResponse(updated)).build();
    }

    @GetMapping("/vehicle-document/get-by-id/{id}")
    @RequireAuth(roles = {RoleType.ALL}, inWorkspace = true)
    public ResponseData<VehicleDocumentResponse> getById(@PathVariable UUID id) {
        VehicleDocument entity = vehicleDocumentService.getOne(id).orElse(null);
        return ResponseData.<VehicleDocumentResponse>builder().status(200).message("Thành công").data(entity != null ? vehicleDocumentMapper.toResponse(entity) : null).build();
    }

    @PostMapping("/vehicle-documents/filter")
    @RequireAuth(roles = {RoleType.ALL}, inWorkspace = true)
    public ResponseData<Page<VehicleDocumentResponse>> filter(@RequestBody BaseFilterRequest filter) {
        Page<VehicleDocumentResponse> page = vehicleDocumentService.filter(filter).map(vehicleDocumentMapper::toResponse);
        return ResponseData.<Page<VehicleDocumentResponse>>builder().status(200).message("Thành công").data(page).build();
    }

    @PatchMapping("/vehicle-document/change-status/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<VehicleDocumentResponse> changeStatus(@PathVariable UUID id, @RequestParam String status) {
        VehicleDocument updated = vehicleDocumentService.changeStatus(id, status);
        return ResponseData.<VehicleDocumentResponse>builder().status(200).message("Thành công").data(vehicleDocumentMapper.toResponse(updated)).build();
    }

    @DeleteMapping("/vehicle-document/soft-delete/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<Void> softDelete(@PathVariable UUID id) {
        vehicleDocumentService.changeStatus(id, "deleted");
        return ResponseData.<Void>builder().status(200).message("Thành công").build();
    }

    @DeleteMapping("/vehicle-document/hard-delete/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN}, inWorkspace = true)
    public ResponseData<Void> hardDelete(@PathVariable UUID id) {
        vehicleDocumentService.delete(id);
        return ResponseData.<Void>builder().status(200).message("Thành công").build();
    }

    @GetMapping("/vehicle-documents/get-by-vehicle/{vehicleId}")
    @RequireAuth(roles = {RoleType.ALL}, inWorkspace = true)
    public ResponseData<List<VehicleDocumentResponse>> getByVehicle(@PathVariable UUID vehicleId) {
        // Simple implementation: Use filter
        BaseFilterRequest filter = new BaseFilterRequest();
        filter.getFilters().add(com.unitel.fms.backend.dtos.request.FilterCriteria.builder()
                .fieldName("vehicle.id")
                .operation(com.unitel.fms.backend.constants.enums.FilterOperation.EQUALS)
                .value(vehicleId.toString())
                .build());
        List<VehicleDocumentResponse> list = vehicleDocumentService.filter(filter).getContent().stream()
                .map(vehicleDocumentMapper::toResponse).toList();
        return ResponseData.<List<VehicleDocumentResponse>>builder().status(200).message("Thành công").data(list).build();
    }

    @GetMapping("/vehicle-documents/get-expiring-soon")
    @RequireAuth(roles = {RoleType.FLEET_MANAGER, RoleType.HR_LEGAL}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<List<VehicleDocumentResponse>> getExpiringSoon() {
        UUID orgId = com.unitel.fms.backend.contexts.SecurityContextHolder.getAuthInfo().getOrgId();
        List<VehicleDocumentResponse> list = vehicleDocumentService.getExpiringSoon(orgId).stream()
                .map(vehicleDocumentMapper::toResponse).toList();
        return ResponseData.<List<VehicleDocumentResponse>>builder().status(200).message("Thành công").data(list).build();
    }
}

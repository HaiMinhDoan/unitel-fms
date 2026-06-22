package com.unitel.fms.backend.controllers;

import com.unitel.fms.backend.dtos.request.BaseFilterRequest;
import com.unitel.fms.backend.dtos.request.VehicleTypeRequest;
import com.unitel.fms.backend.dtos.response.ResponseData;
import com.unitel.fms.backend.dtos.response.VehicleTypeResponse;
import com.unitel.fms.backend.entities.VehicleType;
import com.unitel.fms.backend.customizeanotations.RequireAuth;
import com.unitel.fms.backend.mappers.VehicleTypeMapper;
import com.unitel.fms.backend.services.impl.entity.VehicleTypeService;
import com.unitel.fms.backend.constants.enums.RoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class VehicleTypeController {

    @Autowired
    private VehicleTypeService vehicleTypeService;

    @Autowired
    private VehicleTypeMapper vehicleTypeMapper;

    @PostMapping("/vehicle-type/create")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN})
    public ResponseData<VehicleTypeResponse> create(@RequestBody VehicleTypeRequest request) {
        VehicleType entity = vehicleTypeMapper.toEntity(request);
        VehicleType saved = vehicleTypeService.create(entity);
        return ResponseData.<VehicleTypeResponse>builder().status(200).message("Thành công").data(vehicleTypeMapper.toResponse(saved)).build();
    }

    @PutMapping("/vehicle-type/update/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN})
    public ResponseData<VehicleTypeResponse> update(@PathVariable UUID id, @RequestBody VehicleTypeRequest request) {
        VehicleType entity = vehicleTypeMapper.toEntity(request);
        VehicleType updated = vehicleTypeService.update(id, entity);
        return ResponseData.<VehicleTypeResponse>builder().status(200).message("Thành công").data(vehicleTypeMapper.toResponse(updated)).build();
    }

    @PatchMapping("/vehicle-type/update-partial/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN})
    public ResponseData<VehicleTypeResponse> updatePartial(@PathVariable UUID id, @RequestBody Map<String, Object> updates) {
        VehicleType updated = vehicleTypeService.updateFromMap(id, updates);
        return ResponseData.<VehicleTypeResponse>builder().status(200).message("Thành công").data(vehicleTypeMapper.toResponse(updated)).build();
    }

    @GetMapping("/vehicle-type/get-by-id/{id}")
    @RequireAuth(roles = {RoleType.ALL})
    public ResponseData<VehicleTypeResponse> getById(@PathVariable UUID id) {
        VehicleType entity = vehicleTypeService.getByIdCached(id);
        return ResponseData.<VehicleTypeResponse>builder().status(200).message("Thành công").data(vehicleTypeMapper.toResponse(entity)).build();
    }

    @GetMapping("/vehicle-types/get-all")
    @RequireAuth(roles = {RoleType.ALL})
    public ResponseData<List<VehicleTypeResponse>> getAll() {
        List<VehicleTypeResponse> list = vehicleTypeService.getAll().stream().map(vehicleTypeMapper::toResponse).collect(Collectors.toList());
        return ResponseData.<List<VehicleTypeResponse>>builder().status(200).message("Thành công").data(list).build();
    }

    @PostMapping("/vehicle-types/filter")
    @RequireAuth(roles = {RoleType.ALL})
    public ResponseData<Page<VehicleTypeResponse>> filter(@RequestBody BaseFilterRequest filter) {
        Page<VehicleTypeResponse> page = vehicleTypeService.filter(filter).map(vehicleTypeMapper::toResponse);
        return ResponseData.<Page<VehicleTypeResponse>>builder().status(200).message("Thành công").data(page).build();
    }

    @PatchMapping("/vehicle-type/change-status/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN})
    public ResponseData<VehicleTypeResponse> changeStatus(@PathVariable UUID id, @RequestParam String status) {
        VehicleType updated = vehicleTypeService.changeStatus(id, status);
        return ResponseData.<VehicleTypeResponse>builder().status(200).message("Thành công").data(vehicleTypeMapper.toResponse(updated)).build();
    }

    @DeleteMapping("/vehicle-type/soft-delete/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN})
    public ResponseData<Void> softDelete(@PathVariable UUID id) {
        vehicleTypeService.changeStatus(id, "deleted");
        return ResponseData.<Void>builder().status(200).message("Thành công").build();
    }

    @DeleteMapping("/vehicle-type/hard-delete/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN})
    public ResponseData<Void> hardDelete(@PathVariable UUID id) {
        vehicleTypeService.delete(id);
        return ResponseData.<Void>builder().status(200).message("Thành công").build();
    }
}

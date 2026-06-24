package com.unitel.fms.backend.controllers;

import com.unitel.fms.backend.dtos.request.BaseFilterRequest;
import com.unitel.fms.backend.dtos.response.ResponseData;
import com.unitel.fms.backend.entities.Role;
import com.unitel.fms.backend.customizeanotations.RequireAuth;
import com.unitel.fms.backend.services.impl.entity.RoleService;
import com.unitel.fms.backend.constants.enums.RoleType;
import com.unitel.fms.backend.dtos.request.RoleRequest;
import com.unitel.fms.backend.dtos.response.RoleResponse;
import com.unitel.fms.backend.mappers.RoleMapper;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleMapper roleMapper;

    @PostMapping("/role/create")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN})
    public ResponseData<RoleResponse> create(@Valid @RequestBody RoleRequest request) {
        Role entity = roleMapper.toEntity(request);
        Role saved = roleService.create(entity);
        return ResponseData.<RoleResponse>builder().status(200).messageCode("SUCCESS").data(roleMapper.toResponse(saved)).build();
    }

    @PutMapping("/role/update/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN})
    public ResponseData<RoleResponse> update(@PathVariable UUID id, @Valid @RequestBody RoleRequest request) {
        Role entity = roleMapper.toEntity(request);
        Role updated = roleService.update(id, entity);
        return ResponseData.<RoleResponse>builder().status(200).messageCode("SUCCESS").data(roleMapper.toResponse(updated)).build();
    }

    @PatchMapping("/role/update-partial/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN})
    public ResponseData<RoleResponse> updatePartial(@PathVariable UUID id, @Valid @RequestBody RoleRequest request) {
        Role existing = roleService.getOne(id).orElseThrow(() -> new RuntimeException("Not found"));
        roleMapper.updateEntity(request, existing);
        Role updated = roleService.update(existing);
        return ResponseData.<RoleResponse>builder().status(200).messageCode("SUCCESS").data(roleMapper.toResponse(updated)).build();
    }

    @GetMapping("/role/get-by-id/{id}")
    @RequireAuth(roles = {RoleType.ALL})
    public ResponseData<RoleResponse> getById(@PathVariable UUID id) {
        Role entity = roleService.getByIdCached(id);
        return ResponseData.<RoleResponse>builder().status(200).messageCode("SUCCESS").data(roleMapper.toResponse(entity)).build();
    }

    @GetMapping("/roles/get-all")
    @RequireAuth(roles = {RoleType.ALL})
    public ResponseData<List<RoleResponse>> getAll() {
        List<RoleResponse> list = roleService.getAllCached().stream().map(roleMapper::toResponse).collect(Collectors.toList());
        return ResponseData.<List<RoleResponse>>builder().status(200).messageCode("SUCCESS").data(list).build();
    }

    @PostMapping("/roles/filter")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN})
    public ResponseData<Page<RoleResponse>> filter(@Valid @RequestBody BaseFilterRequest filter) {
        Page<RoleResponse> page = roleService.filter(filter).map(roleMapper::toResponse);
        return ResponseData.<Page<RoleResponse>>builder().status(200).messageCode("SUCCESS").data(page).build();
    }

    @PatchMapping("/role/change-status/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN})
    public ResponseData<RoleResponse> changeStatus(@PathVariable UUID id, @RequestParam String status) {
        Role updated = roleService.changeStatus(id, status);
        return ResponseData.<RoleResponse>builder().status(200).messageCode("SUCCESS").data(roleMapper.toResponse(updated)).build();
    }

    @DeleteMapping("/role/soft-delete/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN})
    public ResponseData<Void> softDelete(@PathVariable UUID id) {
        roleService.changeStatus(id, "deleted");
        return ResponseData.<Void>builder().status(200).messageCode("SUCCESS").build();
    }

    @DeleteMapping("/role/hard-delete/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN})
    public ResponseData<Void> hardDelete(@PathVariable UUID id) {
        roleService.hardDelete(id);
        return ResponseData.<Void>builder().status(200).messageCode("SUCCESS").build();
    }
}

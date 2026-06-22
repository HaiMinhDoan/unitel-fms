package com.unitel.fms.backend.controllers;

import com.unitel.fms.backend.dtos.request.BaseFilterRequest;
import com.unitel.fms.backend.dtos.request.SystemConfigRequest;
import com.unitel.fms.backend.dtos.response.ResponseData;
import com.unitel.fms.backend.dtos.response.SystemConfigResponse;
import com.unitel.fms.backend.entities.SystemConfig;
import com.unitel.fms.backend.customizeanotations.RequireAuth;
import com.unitel.fms.backend.mappers.SystemConfigMapper;
import com.unitel.fms.backend.services.impl.entity.SystemConfigService;
import com.unitel.fms.backend.constants.enums.RoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class SystemConfigController {

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private SystemConfigMapper systemConfigMapper;

    @PostMapping("/system-config/create")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN})
    public ResponseData<SystemConfigResponse> create(@RequestBody SystemConfigRequest request) {
        SystemConfig entity = systemConfigMapper.toEntity(request);
        SystemConfig saved = systemConfigService.create(entity);
        return ResponseData.<SystemConfigResponse>builder().status(200).messageCode("SUCCESS").data(systemConfigMapper.toResponse(saved)).build();
    }

    @PutMapping("/system-config/update/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN})
    public ResponseData<SystemConfigResponse> update(@PathVariable UUID id, @RequestBody SystemConfigRequest request) {
        SystemConfig entity = systemConfigMapper.toEntity(request);
        SystemConfig updated = systemConfigService.update(id, entity);
        return ResponseData.<SystemConfigResponse>builder().status(200).messageCode("SUCCESS").data(systemConfigMapper.toResponse(updated)).build();
    }

    @PatchMapping("/system-config/update-partial/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN})
    public ResponseData<SystemConfigResponse> updatePartial(@PathVariable UUID id, @RequestBody SystemConfigRequest request) {
        SystemConfig existing = systemConfigService.getOne(id).orElseThrow(() -> new RuntimeException("Not found"));
        systemConfigMapper.updateEntity(request, existing);
        SystemConfig updated = systemConfigService.update(existing);
        return ResponseData.<SystemConfigResponse>builder().status(200).messageCode("SUCCESS").data(systemConfigMapper.toResponse(updated)).build();
    }

    @GetMapping("/system-config/get-by-key")
    @RequireAuth(roles = {RoleType.ALL})
    public ResponseData<SystemConfigResponse> getByKey(@RequestParam String key) {
        SystemConfig entity = systemConfigService.getByKeyCached(key);
        return ResponseData.<SystemConfigResponse>builder().status(200).messageCode("SUCCESS").data(systemConfigMapper.toResponse(entity)).build();
    }

    @GetMapping("/system-configs/get-all")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN})
    public ResponseData<List<SystemConfigResponse>> getAll() {
        List<SystemConfigResponse> list = systemConfigService.getAll().stream().map(systemConfigMapper::toResponse).collect(Collectors.toList());
        return ResponseData.<List<SystemConfigResponse>>builder().status(200).messageCode("SUCCESS").data(list).build();
    }

    @PostMapping("/system-configs/filter")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN})
    public ResponseData<Page<SystemConfigResponse>> filter(@RequestBody BaseFilterRequest filter) {
        Page<SystemConfigResponse> page = systemConfigService.filter(filter).map(systemConfigMapper::toResponse);
        return ResponseData.<Page<SystemConfigResponse>>builder().status(200).messageCode("SUCCESS").data(page).build();
    }

    @PatchMapping("/system-config/change-status/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN})
    public ResponseData<SystemConfigResponse> changeStatus(@PathVariable UUID id, @RequestParam String status) {
        SystemConfig updated = systemConfigService.changeStatus(id, status);
        return ResponseData.<SystemConfigResponse>builder().status(200).messageCode("SUCCESS").data(systemConfigMapper.toResponse(updated)).build();
    }

    @DeleteMapping("/system-config/soft-delete/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN})
    public ResponseData<Void> softDelete(@PathVariable UUID id) {
        systemConfigService.changeStatus(id, "deleted");
        return ResponseData.<Void>builder().status(200).messageCode("SUCCESS").build();
    }

    @DeleteMapping("/system-config/hard-delete/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN})
    public ResponseData<Void> hardDelete(@PathVariable UUID id) {
        systemConfigService.hardDelete(id);
        return ResponseData.<Void>builder().status(200).messageCode("SUCCESS").build();
    }
}

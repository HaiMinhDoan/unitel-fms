package com.unitel.fms.backend.controllers;

import com.unitel.fms.backend.dtos.request.BaseFilterRequest;
import com.unitel.fms.backend.dtos.response.ResponseData;
import com.unitel.fms.backend.entities.Organization;
import com.unitel.fms.backend.customizeanotations.RequireAuth;
import com.unitel.fms.backend.services.impl.entity.OrganizationService;
import com.unitel.fms.backend.constants.enums.RoleType;
import com.unitel.fms.backend.dtos.request.OrganizationRequest;
import com.unitel.fms.backend.dtos.response.OrganizationResponse;
import com.unitel.fms.backend.mappers.OrganizationMapper;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private OrganizationMapper organizationMapper;

    @PostMapping("/organization/create")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN})
    public ResponseData<OrganizationResponse> create(@RequestBody OrganizationRequest request) {
        Organization entity = organizationMapper.toEntity(request);
        Organization saved = organizationService.create(entity);
        return ResponseData.<OrganizationResponse>builder().status(200).messageCode("SUCCESS").data(organizationMapper.toResponse(saved)).build();
    }

    @PutMapping("/organization/update/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN})
    public ResponseData<OrganizationResponse> update(@PathVariable UUID id, @RequestBody OrganizationRequest request) {
        Organization entity = organizationMapper.toEntity(request);
        Organization updated = organizationService.update(id, entity);
        return ResponseData.<OrganizationResponse>builder().status(200).messageCode("SUCCESS").data(organizationMapper.toResponse(updated)).build();
    }

    @PatchMapping("/organization/update-partial/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN})
    public ResponseData<OrganizationResponse> updatePartial(@PathVariable UUID id, @RequestBody OrganizationRequest request) {
        Organization existing = organizationService.getOne(id).orElseThrow(() -> new RuntimeException("Not found"));
        organizationMapper.updateEntity(request, existing);
        Organization updated = organizationService.update(existing);
        return ResponseData.<OrganizationResponse>builder().status(200).messageCode("SUCCESS").data(organizationMapper.toResponse(updated)).build();
    }

    @GetMapping("/organization/get-by-id/{id}")
    @RequireAuth(roles = {RoleType.ALL}, inWorkspace = true)
    public ResponseData<OrganizationResponse> getById(@PathVariable UUID id) {
        Organization entity = organizationService.getByIdCached(id);
        return ResponseData.<OrganizationResponse>builder().status(200).messageCode("SUCCESS").data(organizationMapper.toResponse(entity)).build();
    }

    @GetMapping("/organizations/get-all")
    @RequireAuth(roles = {RoleType.ALL})
    public ResponseData<List<OrganizationResponse>> getAll() {
        List<OrganizationResponse> list = organizationService.getAll().stream().map(organizationMapper::toResponse).collect(Collectors.toList());
        return ResponseData.<List<OrganizationResponse>>builder().status(200).messageCode("SUCCESS").data(list).build();
    }

    @PostMapping("/organizations/filter")
    @RequireAuth(roles = {RoleType.ALL})
    public ResponseData<Page<OrganizationResponse>> filter(@RequestBody BaseFilterRequest filter) {
        Page<OrganizationResponse> page = organizationService.filter(filter).map(organizationMapper::toResponse);
        return ResponseData.<Page<OrganizationResponse>>builder().status(200).messageCode("SUCCESS").data(page).build();
    }

    @PatchMapping("/organization/change-status/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN})
    public ResponseData<OrganizationResponse> changeStatus(@PathVariable UUID id, @RequestParam String status) {
        Organization updated = organizationService.changeStatus(id, status);
        return ResponseData.<OrganizationResponse>builder().status(200).messageCode("SUCCESS").data(organizationMapper.toResponse(updated)).build();
    }

    @DeleteMapping("/organization/soft-delete/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN})
    public ResponseData<Void> softDelete(@PathVariable UUID id) {
        organizationService.changeStatus(id, "deleted");
        return ResponseData.<Void>builder().status(200).messageCode("SUCCESS").build();
    }

    @DeleteMapping("/organization/hard-delete/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN})
    public ResponseData<Void> hardDelete(@PathVariable UUID id) {
        organizationService.hardDelete(id);
        return ResponseData.<Void>builder().status(200).messageCode("SUCCESS").build();
    }
}

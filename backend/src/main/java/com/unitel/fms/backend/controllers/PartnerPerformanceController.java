package com.unitel.fms.backend.controllers;

import com.unitel.fms.backend.dtos.request.PartnerPerformanceRequest;
import com.unitel.fms.backend.dtos.response.PartnerPerformanceResponse;
import com.unitel.fms.backend.dtos.response.ResponseData;
import com.unitel.fms.backend.entities.PartnerPerformance;
import com.unitel.fms.backend.mappers.PartnerPerformanceMapper;
import com.unitel.fms.backend.services.impl.entity.PartnerPerformanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.unitel.fms.backend.customizeanotations.RequireAuth;
import com.unitel.fms.backend.constants.enums.RoleType;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/partner-performances")
@RequiredArgsConstructor
public class PartnerPerformanceController {

    private final PartnerPerformanceService partnerPerformanceService;
    private final PartnerPerformanceMapper partnerPerformanceMapper;

    @GetMapping
    @RequireAuth(roles = {RoleType.ALL}, inWorkspace = true)
    public ResponseData<List<PartnerPerformanceResponse>> getAll() {
        List<PartnerPerformanceResponse> responses = partnerPerformanceService.getAll().stream()
                .map(partnerPerformanceMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseData.<List<PartnerPerformanceResponse>>builder().status(200).messageCode("SUCCESS").data(responses).build();
    }

    @GetMapping("/{id}")
    @RequireAuth(roles = {RoleType.ALL}, inWorkspace = true)
    public ResponseData<PartnerPerformanceResponse> getById(@PathVariable UUID id) {
        PartnerPerformance partnerPerformance = partnerPerformanceService.getOne(id).orElseThrow(() -> new RuntimeException("PartnerPerformance not found"));
        return ResponseData.<PartnerPerformanceResponse>builder().status(200).messageCode("SUCCESS").data(partnerPerformanceMapper.toResponse(partnerPerformance)).build();
    }

    @PostMapping
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<PartnerPerformanceResponse> create(@Valid @RequestBody PartnerPerformanceRequest request) {
        PartnerPerformance entity = partnerPerformanceMapper.toEntity(request);
        PartnerPerformance saved = partnerPerformanceService.create(entity);
        return ResponseData.<PartnerPerformanceResponse>builder().status(200).messageCode("SUCCESS").data(partnerPerformanceMapper.toResponse(saved)).build();
    }

    @PutMapping("/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<PartnerPerformanceResponse> update(@PathVariable UUID id, @Valid @RequestBody PartnerPerformanceRequest request) {
        PartnerPerformance entity = partnerPerformanceService.getOne(id).orElseThrow(() -> new RuntimeException("PartnerPerformance not found"));
        partnerPerformanceMapper.updateEntityFromRequest(request, entity);
        PartnerPerformance saved = partnerPerformanceService.update(entity);
        return ResponseData.<PartnerPerformanceResponse>builder().status(200).messageCode("SUCCESS").data(partnerPerformanceMapper.toResponse(saved)).build();
    }

    @DeleteMapping("/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN}, inWorkspace = true)
    public ResponseData<Void> delete(@PathVariable UUID id) {
        partnerPerformanceService.delete(id);
        return ResponseData.<Void>builder().status(200).messageCode("SUCCESS").data(null).build();
    }

    @PostMapping("/recalculate")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<PartnerPerformanceResponse> recalculate(@RequestParam UUID partnerId, @RequestParam String periodMonth) {
        PartnerPerformance pp = partnerPerformanceService.recalculateForPartnerAndMonth(partnerId, periodMonth);
        return ResponseData.<PartnerPerformanceResponse>builder().status(200).messageCode("SUCCESS").data(partnerPerformanceMapper.toResponse(pp)).build();
    }

    @PostMapping("/filter")
    @RequireAuth(roles = {RoleType.ALL}, inWorkspace = true)
    public ResponseData<org.springframework.data.domain.Page<PartnerPerformanceResponse>> filter(@Valid @RequestBody com.unitel.fms.backend.dtos.request.BaseFilterRequest filter) {
        org.springframework.data.domain.Page<PartnerPerformanceResponse> page = partnerPerformanceService.filter(filter).map(partnerPerformanceMapper::toResponse);
        return ResponseData.<org.springframework.data.domain.Page<PartnerPerformanceResponse>>builder().status(200).messageCode("SUCCESS").data(page).build();
    }

    @PatchMapping("/change-status/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<PartnerPerformanceResponse> changeStatus(@PathVariable UUID id, @RequestParam String status) {
        PartnerPerformance updated = partnerPerformanceService.changeStatus(id, status);
        return ResponseData.<PartnerPerformanceResponse>builder().status(200).messageCode("SUCCESS").data(partnerPerformanceMapper.toResponse(updated)).build();
    }

    @DeleteMapping("/soft-delete/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<Void> softDelete(@PathVariable UUID id) {
        partnerPerformanceService.changeStatus(id, "deleted");
        return ResponseData.<Void>builder().status(200).messageCode("SUCCESS").build();
    }
}

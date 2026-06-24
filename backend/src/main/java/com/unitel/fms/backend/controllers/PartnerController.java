package com.unitel.fms.backend.controllers;

import com.unitel.fms.backend.dtos.request.PartnerCriteriaRequest;
import com.unitel.fms.backend.dtos.request.PartnerRequest;
import com.unitel.fms.backend.dtos.response.PartnerResponse;
import com.unitel.fms.backend.dtos.response.ResponseData;
import com.unitel.fms.backend.entities.Partner;
import com.unitel.fms.backend.mappers.PartnerMapper;
import com.unitel.fms.backend.services.impl.entity.PartnerService;
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
@RequestMapping("/api/v1/partners")
@RequiredArgsConstructor
public class PartnerController {

    private final PartnerService partnerService;
    private final PartnerMapper partnerMapper;

    @GetMapping
    @RequireAuth(roles = {RoleType.ALL}, inWorkspace = true)
    public ResponseData<List<PartnerResponse>> getAll() {
        List<PartnerResponse> responses = partnerService.getAll().stream()
                .map(partnerMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseData.<List<PartnerResponse>>builder().status(200).messageCode("SUCCESS").data(responses).build();
    }

    @GetMapping("/{id}")
    @RequireAuth(roles = {RoleType.ALL}, inWorkspace = true)
    public ResponseData<PartnerResponse> getById(@PathVariable UUID id) {
        Partner partner = partnerService.getOne(id).orElseThrow(() -> new RuntimeException("Partner not found"));
        return ResponseData.<PartnerResponse>builder().status(200).messageCode("SUCCESS").data(partnerMapper.toResponse(partner)).build();
    }

    @PostMapping
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<PartnerResponse> create(@Valid @RequestBody PartnerRequest request) {
        Partner entity = partnerMapper.toEntity(request);
        Partner saved = partnerService.create(entity);
        return ResponseData.<PartnerResponse>builder().status(200).messageCode("SUCCESS").data(partnerMapper.toResponse(saved)).build();
    }

    @PutMapping("/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<PartnerResponse> update(@PathVariable UUID id, @Valid @RequestBody PartnerRequest request) {
        Partner entity = partnerService.getOne(id).orElseThrow(() -> new RuntimeException("Partner not found"));
        partnerMapper.updateEntityFromRequest(request, entity);
        Partner saved = partnerService.update(entity);
        return ResponseData.<PartnerResponse>builder().status(200).messageCode("SUCCESS").data(partnerMapper.toResponse(saved)).build();
    }

    @DeleteMapping("/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN}, inWorkspace = true)
    public ResponseData<Void> delete(@PathVariable UUID id) {
        partnerService.delete(id);
        return ResponseData.<Void>builder().status(200).messageCode("SUCCESS").data(null).build();
    }

    @PostMapping("/compare")
    @RequireAuth(roles = {RoleType.ALL}, inWorkspace = true)
    public ResponseData<List<PartnerResponse>> compareForCriteria(@Valid @RequestBody PartnerCriteriaRequest criteria) {
        List<PartnerResponse> responses = partnerService.compareForCriteria(criteria).stream()
                .map(partnerMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseData.<List<PartnerResponse>>builder().status(200).messageCode("SUCCESS").data(responses).build();
    }

    @PostMapping("/filter")
    @RequireAuth(roles = {RoleType.ALL}, inWorkspace = true)
    public ResponseData<org.springframework.data.domain.Page<PartnerResponse>> filter(@Valid @RequestBody com.unitel.fms.backend.dtos.request.BaseFilterRequest filter) {
        org.springframework.data.domain.Page<PartnerResponse> page = partnerService.filter(filter).map(partnerMapper::toResponse);
        return ResponseData.<org.springframework.data.domain.Page<PartnerResponse>>builder().status(200).messageCode("SUCCESS").data(page).build();
    }

    @PatchMapping("/change-status/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<PartnerResponse> changeStatus(@PathVariable UUID id, @RequestParam String status) {
        Partner updated = partnerService.changeStatus(id, status);
        return ResponseData.<PartnerResponse>builder().status(200).messageCode("SUCCESS").data(partnerMapper.toResponse(updated)).build();
    }

    @DeleteMapping("/soft-delete/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<Void> softDelete(@PathVariable UUID id) {
        partnerService.changeStatus(id, "deleted");
        return ResponseData.<Void>builder().status(200).messageCode("SUCCESS").build();
    }
}

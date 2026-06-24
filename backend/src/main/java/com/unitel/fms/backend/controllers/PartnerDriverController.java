package com.unitel.fms.backend.controllers;

import com.unitel.fms.backend.dtos.request.PartnerDriverRequest;
import com.unitel.fms.backend.dtos.response.PartnerDriverResponse;
import com.unitel.fms.backend.dtos.response.ResponseData;
import com.unitel.fms.backend.entities.PartnerDriver;
import com.unitel.fms.backend.mappers.PartnerDriverMapper;
import com.unitel.fms.backend.services.impl.entity.PartnerDriverService;
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
@RequestMapping("/api/v1/partner-drivers")
@RequiredArgsConstructor
public class PartnerDriverController {

    private final PartnerDriverService partnerDriverService;
    private final PartnerDriverMapper partnerDriverMapper;

    @GetMapping
    @RequireAuth(roles = {RoleType.ALL}, inWorkspace = true)
    public ResponseData<List<PartnerDriverResponse>> getAll() {
        List<PartnerDriverResponse> responses = partnerDriverService.getAll().stream()
                .map(partnerDriverMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseData.<List<PartnerDriverResponse>>builder().status(200).messageCode("SUCCESS").data(responses).build();
    }

    @GetMapping("/{id}")
    @RequireAuth(roles = {RoleType.ALL}, inWorkspace = true)
    public ResponseData<PartnerDriverResponse> getById(@PathVariable UUID id) {
        PartnerDriver partnerDriver = partnerDriverService.getOne(id).orElseThrow(() -> new RuntimeException("PartnerDriver not found"));
        return ResponseData.<PartnerDriverResponse>builder().status(200).messageCode("SUCCESS").data(partnerDriverMapper.toResponse(partnerDriver)).build();
    }

    @PostMapping
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<PartnerDriverResponse> create(@Valid @RequestBody PartnerDriverRequest request) {
        PartnerDriver entity = partnerDriverMapper.toEntity(request);
        PartnerDriver saved = partnerDriverService.create(entity);
        return ResponseData.<PartnerDriverResponse>builder().status(200).messageCode("SUCCESS").data(partnerDriverMapper.toResponse(saved)).build();
    }

    @PutMapping("/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<PartnerDriverResponse> update(@PathVariable UUID id, @Valid @RequestBody PartnerDriverRequest request) {
        PartnerDriver entity = partnerDriverService.getOne(id).orElseThrow(() -> new RuntimeException("PartnerDriver not found"));
        partnerDriverMapper.updateEntityFromRequest(request, entity);
        PartnerDriver saved = partnerDriverService.update(entity);
        return ResponseData.<PartnerDriverResponse>builder().status(200).messageCode("SUCCESS").data(partnerDriverMapper.toResponse(saved)).build();
    }

    @DeleteMapping("/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN}, inWorkspace = true)
    public ResponseData<Void> delete(@PathVariable UUID id) {
        partnerDriverService.delete(id);
        return ResponseData.<Void>builder().status(200).messageCode("SUCCESS").data(null).build();
    }

    @PostMapping("/filter")
    @RequireAuth(roles = {RoleType.ALL}, inWorkspace = true)
    public ResponseData<org.springframework.data.domain.Page<PartnerDriverResponse>> filter(@Valid @RequestBody com.unitel.fms.backend.dtos.request.BaseFilterRequest filter) {
        org.springframework.data.domain.Page<PartnerDriverResponse> page = partnerDriverService.filter(filter).map(partnerDriverMapper::toResponse);
        return ResponseData.<org.springframework.data.domain.Page<PartnerDriverResponse>>builder().status(200).messageCode("SUCCESS").data(page).build();
    }

    @PatchMapping("/change-status/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<PartnerDriverResponse> changeStatus(@PathVariable UUID id, @RequestParam String status) {
        PartnerDriver updated = partnerDriverService.changeStatus(id, status);
        return ResponseData.<PartnerDriverResponse>builder().status(200).messageCode("SUCCESS").data(partnerDriverMapper.toResponse(updated)).build();
    }

    @DeleteMapping("/soft-delete/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<Void> softDelete(@PathVariable UUID id) {
        partnerDriverService.changeStatus(id, "deleted");
        return ResponseData.<Void>builder().status(200).messageCode("SUCCESS").build();
    }
}

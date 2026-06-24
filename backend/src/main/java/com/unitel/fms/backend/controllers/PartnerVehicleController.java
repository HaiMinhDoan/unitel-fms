package com.unitel.fms.backend.controllers;

import com.unitel.fms.backend.dtos.request.PartnerVehicleRequest;
import com.unitel.fms.backend.dtos.response.PartnerVehicleResponse;
import com.unitel.fms.backend.dtos.response.ResponseData;
import com.unitel.fms.backend.entities.PartnerVehicle;
import com.unitel.fms.backend.mappers.PartnerVehicleMapper;
import com.unitel.fms.backend.services.impl.entity.PartnerVehicleService;
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
@RequestMapping("/api/v1/partner-vehicles")
@RequiredArgsConstructor
public class PartnerVehicleController {

    private final PartnerVehicleService partnerVehicleService;
    private final PartnerVehicleMapper partnerVehicleMapper;

    @GetMapping
    @RequireAuth(roles = {RoleType.ALL}, inWorkspace = true)
    public ResponseData<List<PartnerVehicleResponse>> getAll() {
        List<PartnerVehicleResponse> responses = partnerVehicleService.getAll().stream()
                .map(partnerVehicleMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseData.<List<PartnerVehicleResponse>>builder().status(200).messageCode("SUCCESS").data(responses).build();
    }

    @GetMapping("/{id}")
    @RequireAuth(roles = {RoleType.ALL}, inWorkspace = true)
    public ResponseData<PartnerVehicleResponse> getById(@PathVariable UUID id) {
        PartnerVehicle partnerVehicle = partnerVehicleService.getOne(id).orElseThrow(() -> new RuntimeException("PartnerVehicle not found"));
        return ResponseData.<PartnerVehicleResponse>builder().status(200).messageCode("SUCCESS").data(partnerVehicleMapper.toResponse(partnerVehicle)).build();
    }

    @PostMapping
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<PartnerVehicleResponse> create(@Valid @RequestBody PartnerVehicleRequest request) {
        PartnerVehicle entity = partnerVehicleMapper.toEntity(request);
        PartnerVehicle saved = partnerVehicleService.create(entity);
        return ResponseData.<PartnerVehicleResponse>builder().status(200).messageCode("SUCCESS").data(partnerVehicleMapper.toResponse(saved)).build();
    }

    @PutMapping("/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<PartnerVehicleResponse> update(@PathVariable UUID id, @Valid @RequestBody PartnerVehicleRequest request) {
        PartnerVehicle entity = partnerVehicleService.getOne(id).orElseThrow(() -> new RuntimeException("PartnerVehicle not found"));
        partnerVehicleMapper.updateEntityFromRequest(request, entity);
        PartnerVehicle saved = partnerVehicleService.update(entity);
        return ResponseData.<PartnerVehicleResponse>builder().status(200).messageCode("SUCCESS").data(partnerVehicleMapper.toResponse(saved)).build();
    }

    @DeleteMapping("/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN}, inWorkspace = true)
    public ResponseData<Void> delete(@PathVariable UUID id) {
        partnerVehicleService.delete(id);
        return ResponseData.<Void>builder().status(200).messageCode("SUCCESS").data(null).build();
    }

    @PostMapping("/filter")
    @RequireAuth(roles = {RoleType.ALL}, inWorkspace = true)
    public ResponseData<org.springframework.data.domain.Page<PartnerVehicleResponse>> filter(@Valid @RequestBody com.unitel.fms.backend.dtos.request.BaseFilterRequest filter) {
        org.springframework.data.domain.Page<PartnerVehicleResponse> page = partnerVehicleService.filter(filter).map(partnerVehicleMapper::toResponse);
        return ResponseData.<org.springframework.data.domain.Page<PartnerVehicleResponse>>builder().status(200).messageCode("SUCCESS").data(page).build();
    }

    @PatchMapping("/change-status/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<PartnerVehicleResponse> changeStatus(@PathVariable UUID id, @RequestParam String status) {
        PartnerVehicle updated = partnerVehicleService.changeStatus(id, status);
        return ResponseData.<PartnerVehicleResponse>builder().status(200).messageCode("SUCCESS").data(partnerVehicleMapper.toResponse(updated)).build();
    }

    @DeleteMapping("/soft-delete/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<Void> softDelete(@PathVariable UUID id) {
        partnerVehicleService.changeStatus(id, "deleted");
        return ResponseData.<Void>builder().status(200).messageCode("SUCCESS").build();
    }
}

package com.unitel.fms.backend.controllers;

import com.unitel.fms.backend.customizeanotations.RequireAuth;
import com.unitel.fms.backend.constants.enums.RoleType;
import com.unitel.fms.backend.dtos.request.BaseFilterRequest;
import com.unitel.fms.backend.dtos.request.DriverRequest;
import com.unitel.fms.backend.dtos.response.DriverEligibilityResult;
import com.unitel.fms.backend.dtos.response.DriverResponse;
import com.unitel.fms.backend.dtos.response.ResponseData;
import com.unitel.fms.backend.entities.Driver;
import com.unitel.fms.backend.mappers.DriverMapper;
import com.unitel.fms.backend.services.impl.entity.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;
    private final DriverMapper driverMapper;

    @PostMapping("/driver/create")
    @RequireAuth(roles = {RoleType.HR_LEGAL, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<DriverResponse> create(@Valid @RequestBody DriverRequest request) {
        Driver entity = driverMapper.toEntity(request);
        Driver saved = driverService.create(entity);
        return ResponseData.<DriverResponse>builder().status(200).message("Thành công").data(driverMapper.toResponse(saved)).build();
    }

    @PutMapping("/driver/update/{id}")
    @RequireAuth(roles = {RoleType.HR_LEGAL, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<DriverResponse> update(@PathVariable UUID id, @Valid @RequestBody DriverRequest request) {
        Driver entity = driverMapper.toEntity(request);
        Driver updated = driverService.update(id, entity);
        return ResponseData.<DriverResponse>builder().status(200).message("Thành công").data(driverMapper.toResponse(updated)).build();
    }

    @PatchMapping("/driver/update-partial/{id}")
    @RequireAuth(roles = {RoleType.HR_LEGAL, RoleType.FLEET_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<DriverResponse> updatePartial(@PathVariable UUID id, @RequestBody Map<String, Object> updates) {
        Driver updated = driverService.updateFromMap(id, updates);
        return ResponseData.<DriverResponse>builder().status(200).message("Thành công").data(driverMapper.toResponse(updated)).build();
    }

    @GetMapping("/driver/get-by-id/{id}")
    @RequireAuth(roles = {RoleType.ALL}, inWorkspace = true)
    public ResponseData<DriverResponse> getById(@PathVariable UUID id) {
        Driver entity = driverService.getOne(id).orElse(null);
        if (entity == null) {
            return ResponseData.<DriverResponse>builder().status(404).message("Không tìm thấy").build();
        }
        return ResponseData.<DriverResponse>builder().status(200).message("Thành công").data(driverMapper.toResponse(entity)).build();
    }

    @PostMapping("/drivers/filter")
    @RequireAuth(roles = {RoleType.ALL}, inWorkspace = true)
    public ResponseData<Page<DriverResponse>> filter(@RequestBody BaseFilterRequest filter) {
        Page<DriverResponse> page = driverService.filter(filter).map(driverMapper::toResponse);
        return ResponseData.<Page<DriverResponse>>builder().status(200).message("Thành công").data(page).build();
    }

    @PatchMapping("/driver/change-status/{id}")
    @RequireAuth(roles = {RoleType.HR_LEGAL}, inWorkspace = true)
    public ResponseData<DriverResponse> changeStatus(@PathVariable UUID id, @RequestParam String status) {
        Driver updated = driverService.changeStatus(id, status);
        return ResponseData.<DriverResponse>builder().status(200).message("Thành công").data(driverMapper.toResponse(updated)).build();
    }

    @DeleteMapping("/driver/soft-delete/{id}")
    @RequireAuth(roles = {RoleType.HR_LEGAL}, inWorkspace = true)
    public ResponseData<Void> softDelete(@PathVariable UUID id) {
        driverService.changeStatus(id, "deleted");
        return ResponseData.<Void>builder().status(200).message("Thành công").build();
    }

    @DeleteMapping("/driver/hard-delete/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN})
    public ResponseData<Void> hardDelete(@PathVariable UUID id) {
        driverService.delete(id);
        return ResponseData.<Void>builder().status(200).message("Thành công").build();
    }

    @GetMapping("/drivers/get-available-for-dispatch")
    @RequireAuth(roles = {RoleType.DISPATCHER}, inWorkspace = true)
    public ResponseData<List<DriverResponse>> getAvailableForDispatch() {
        UUID orgId = com.unitel.fms.backend.contexts.SecurityContextHolder.getAuthInfo().getOrgId();
        List<DriverResponse> list = driverService.findAvailableForDispatch(orgId).stream()
                .map(driverMapper::toResponse).toList();
        return ResponseData.<List<DriverResponse>>builder().status(200).message("Thành công").data(list).build();
    }

    @GetMapping("/driver/{id}/check-dispatch-eligibility")
    @RequireAuth(roles = {RoleType.DISPATCHER}, inWorkspace = true)
    public ResponseData<DriverEligibilityResult> checkDispatchEligibility(@PathVariable UUID id) {
        DriverEligibilityResult result = driverService.checkDispatchEligibility(id);
        return ResponseData.<DriverEligibilityResult>builder().status(200).message("Thành công").data(result).build();
    }

    @GetMapping("/driver/me")
    @RequireAuth(roles = {RoleType.DRIVER})
    public ResponseData<DriverResponse> getMe() {
        UUID userId = com.unitel.fms.backend.contexts.SecurityContextHolder.getAuthInfo().getId();
        Driver driver = driverService.getMe(userId);
        return ResponseData.<DriverResponse>builder().status(200).data(driverMapper.toResponse(driver)).message("Thành công").build();
    }
}

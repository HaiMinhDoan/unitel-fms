package com.unitel.fms.backend.controllers;

import com.unitel.fms.backend.customizeanotations.RequireAuth;
import com.unitel.fms.backend.constants.enums.RoleType;
import com.unitel.fms.backend.dtos.request.BaseFilterRequest;
import com.unitel.fms.backend.dtos.request.CustomerRequest;
import com.unitel.fms.backend.dtos.response.CustomerResponse;
import com.unitel.fms.backend.dtos.response.ResponseData;
import com.unitel.fms.backend.entities.Customer;
import com.unitel.fms.backend.mappers.CustomerMapper;
import com.unitel.fms.backend.services.impl.entity.CustomerService;
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
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerMapper customerMapper;

    @PostMapping("/customer/create")
    @RequireAuth(roles = {RoleType.DISPATCHER, RoleType.OPS_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
        Customer entity = customerMapper.toEntity(request);
        Customer saved = customerService.create(entity);
        return ResponseData.<CustomerResponse>builder().status(200).messageCode("SUCCESS").data(customerMapper.toResponse(saved)).build();
    }

    @PutMapping("/customer/update/{id}")
    @RequireAuth(roles = {RoleType.DISPATCHER, RoleType.OPS_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<CustomerResponse> update(@PathVariable UUID id, @Valid @RequestBody CustomerRequest request) {
        Customer entity = customerMapper.toEntity(request);
        Customer updated = customerService.update(id, entity);
        return ResponseData.<CustomerResponse>builder().status(200).messageCode("SUCCESS").data(customerMapper.toResponse(updated)).build();
    }

    @PatchMapping("/customer/update-partial/{id}")
    @RequireAuth(roles = {RoleType.DISPATCHER, RoleType.OPS_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<CustomerResponse> updatePartial(@PathVariable UUID id, @Valid @RequestBody Map<String, Object> updates) {
        Customer updated = customerService.updateFromMap(id, updates);
        return ResponseData.<CustomerResponse>builder().status(200).messageCode("SUCCESS").data(customerMapper.toResponse(updated)).build();
    }

    @GetMapping("/customer/get-by-id/{id}")
    @RequireAuth(roles = {RoleType.ALL}, inWorkspace = true)
    public ResponseData<CustomerResponse> getById(@PathVariable UUID id) {
        Customer entity = customerService.getOne(id).orElse(null);
        if (entity == null) {
            return ResponseData.<CustomerResponse>builder().status(404).messageCode("NOT_FOUND").build();
        }
        return ResponseData.<CustomerResponse>builder().status(200).messageCode("SUCCESS").data(customerMapper.toResponse(entity)).build();
    }

    @GetMapping("/customers/get-all")
    @RequireAuth(roles = {RoleType.DISPATCHER, RoleType.OPS_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<List<CustomerResponse>> getAll() {
        UUID orgId = com.unitel.fms.backend.contexts.SecurityContextHolder.getAuthInfo().getOrgId();
        BaseFilterRequest filter = new BaseFilterRequest();
        filter.getFilters().add(com.unitel.fms.backend.dtos.request.FilterCriteria.builder()
                .fieldName("org.id")
                .operation(com.unitel.fms.backend.constants.enums.FilterOperation.EQUALS)
                .value(orgId.toString())
                .build());
        List<CustomerResponse> list = customerService.filter(filter).getContent().stream()
                .map(customerMapper::toResponse).toList();
        return ResponseData.<List<CustomerResponse>>builder().status(200).messageCode("SUCCESS").data(list).build();
    }

    @PostMapping("/customers/filter")
    @RequireAuth(roles = {RoleType.ALL}, inWorkspace = true)
    public ResponseData<Page<CustomerResponse>> filter(@Valid @RequestBody BaseFilterRequest filter) {
        Page<CustomerResponse> page = customerService.filter(filter).map(customerMapper::toResponse);
        return ResponseData.<Page<CustomerResponse>>builder().status(200).messageCode("SUCCESS").data(page).build();
    }

    @PatchMapping("/customer/change-status/{id}")
    @RequireAuth(roles = {RoleType.OPS_MANAGER}, inWorkspace = true)
    public ResponseData<CustomerResponse> changeStatus(@PathVariable UUID id, @RequestParam String status) {
        Customer updated = customerService.changeStatus(id, status);
        return ResponseData.<CustomerResponse>builder().status(200).messageCode("SUCCESS").data(customerMapper.toResponse(updated)).build();
    }

    @DeleteMapping("/customer/soft-delete/{id}")
    @RequireAuth(roles = {RoleType.OPS_MANAGER}, inWorkspace = true)
    public ResponseData<Void> softDelete(@PathVariable UUID id) {
        customerService.changeStatus(id, "deleted");
        return ResponseData.<Void>builder().status(200).messageCode("SUCCESS").build();
    }

    @DeleteMapping("/customer/hard-delete/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN})
    public ResponseData<Void> hardDelete(@PathVariable UUID id) {
        customerService.delete(id);
        return ResponseData.<Void>builder().status(200).messageCode("SUCCESS").build();
    }
}

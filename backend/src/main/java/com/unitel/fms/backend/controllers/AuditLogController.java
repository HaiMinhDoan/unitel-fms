package com.unitel.fms.backend.controllers;

import com.unitel.fms.backend.dtos.request.BaseFilterRequest;
import com.unitel.fms.backend.dtos.response.ResponseData;
import com.unitel.fms.backend.dtos.response.AuditLogResponse;
import com.unitel.fms.backend.customizeanotations.RequireAuth;
import com.unitel.fms.backend.mappers.AuditLogMapper;
import com.unitel.fms.backend.services.impl.entity.AuditLogService;
import com.unitel.fms.backend.constants.enums.RoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class AuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private AuditLogMapper auditLogMapper;

    @GetMapping("/audit-logs/get-all")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.OPS_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<List<AuditLogResponse>> getAll() {
        List<AuditLogResponse> list = auditLogService.getAll().stream().map(auditLogMapper::toResponse).collect(Collectors.toList());
        return ResponseData.<List<AuditLogResponse>>builder().status(200).messageCode("SUCCESS").data(list).build();
    }

    @PostMapping("/audit-logs/filter")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.OPS_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<Page<AuditLogResponse>> filter(@Valid @RequestBody BaseFilterRequest filter) {
        Page<AuditLogResponse> page = auditLogService.filter(filter).map(auditLogMapper::toResponse);
        return ResponseData.<Page<AuditLogResponse>>builder().status(200).messageCode("SUCCESS").data(page).build();
    }
}

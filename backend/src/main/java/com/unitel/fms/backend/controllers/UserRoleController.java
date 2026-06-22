package com.unitel.fms.backend.controllers;

import com.unitel.fms.backend.dtos.request.BaseFilterRequest;
import com.unitel.fms.backend.dtos.response.ResponseData;
import com.unitel.fms.backend.entities.UserRole;
import com.unitel.fms.backend.customizeanotations.RequireAuth;
import com.unitel.fms.backend.services.impl.entity.UserRoleService;
import com.unitel.fms.backend.constants.enums.RoleType;
import com.unitel.fms.backend.dtos.request.UserRoleRequest;
import com.unitel.fms.backend.dtos.response.UserRoleResponse;
import com.unitel.fms.backend.mappers.UserRoleMapper;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class UserRoleController {

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @PostMapping("/user-role/create")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.OPS_MANAGER}, rolesLogic = RequireAuth.LogicType.OR)
    public ResponseData<UserRoleResponse> create(@RequestBody UserRoleRequest request) {
        UserRole entity = userRoleMapper.toEntity(request);
        UserRole saved = userRoleService.create(entity);
        return ResponseData.<UserRoleResponse>builder().status(200).messageCode("SUCCESS").data(userRoleMapper.toResponse(saved)).build();
    }

    @DeleteMapping("/user-role/hard-delete/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.OPS_MANAGER}, rolesLogic = RequireAuth.LogicType.OR)
    public ResponseData<Void> hardDelete(@PathVariable UUID id) {
        userRoleService.hardDelete(id);
        return ResponseData.<Void>builder().status(200).messageCode("SUCCESS").build();
    }
}

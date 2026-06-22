package com.unitel.fms.backend.controllers;

import com.unitel.fms.backend.dtos.request.BaseFilterRequest;
import com.unitel.fms.backend.dtos.response.ResponseData;
import com.unitel.fms.backend.entities.User;
import com.unitel.fms.backend.customizeanotations.RequireAuth;
import com.unitel.fms.backend.contexts.SecurityContextHolder;
import com.unitel.fms.backend.services.impl.entity.UserService;
import com.unitel.fms.backend.constants.enums.RoleType;
import com.unitel.fms.backend.dtos.request.UserRequest;
import com.unitel.fms.backend.dtos.response.UserResponse;
import com.unitel.fms.backend.mappers.UserMapper;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/user/create")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN})
    public ResponseData<UserResponse> create(@RequestBody UserRequest request) {
        User entity = userMapper.toEntity(request);
        User saved = userService.create(entity);
        return ResponseData.<UserResponse>builder().status(200).messageCode("SUCCESS").data(userMapper.toResponse(saved)).build();
    }

    @PutMapping("/user/update/{id}")
    @RequireAuth(roles = {RoleType.ALL})
    public ResponseData<UserResponse> update(@PathVariable UUID id, @RequestBody UserRequest request) {
        verifySelfOrAdmin(id);
        if (!SecurityContextHolder.getAuthInfo().hasAnyRole(RoleType.SYSTEM_ADMIN)) {
            request.setOrgId(null);
            request.setStatus(null);
        }
        User entity = userMapper.toEntity(request);
        User updated = userService.update(id, entity);
        return ResponseData.<UserResponse>builder().status(200).messageCode("SUCCESS").data(userMapper.toResponse(updated)).build();
    }

    @PatchMapping("/user/update-partial/{id}")
    @RequireAuth(roles = {RoleType.ALL})
    public ResponseData<UserResponse> updatePartial(@PathVariable UUID id, @RequestBody UserRequest request) {
        verifySelfOrAdmin(id);
        if (!SecurityContextHolder.getAuthInfo().hasAnyRole(RoleType.SYSTEM_ADMIN)) {
            request.setOrgId(null);
            request.setStatus(null);
        }
        User existing = userService.getOne(id).orElseThrow(() -> new RuntimeException("Not found"));
        userMapper.updateEntity(request, existing);
        User updated = userService.update(existing);
        return ResponseData.<UserResponse>builder().status(200).messageCode("SUCCESS").data(userMapper.toResponse(updated)).build();
    }

    @GetMapping("/user/get-by-id/{id}")
    @RequireAuth(roles = {RoleType.ALL}, inWorkspace = true)
    public ResponseData<UserResponse> getById(@PathVariable UUID id) {
        User entity = userService.getOne(id).orElse(null);
        return ResponseData.<UserResponse>builder().status(200).messageCode("SUCCESS").data(entity != null ? userMapper.toResponse(entity) : null).build();
    }

    @GetMapping("/users/get-all")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.OPS_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<List<UserResponse>> getAll() {
        List<UserResponse> list = userService.getAll().stream().map(userMapper::toResponse).collect(Collectors.toList());
        return ResponseData.<List<UserResponse>>builder().status(200).messageCode("SUCCESS").data(list).build();
    }

    @PostMapping("/users/filter")
    @RequireAuth(roles = {RoleType.ALL}, inWorkspace = true)
    public ResponseData<Page<UserResponse>> filter(@RequestBody BaseFilterRequest filter) {
        Page<UserResponse> page = userService.filter(filter).map(userMapper::toResponse);
        return ResponseData.<Page<UserResponse>>builder().status(200).messageCode("SUCCESS").data(page).build();
    }

    @PatchMapping("/user/change-status/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN, RoleType.OPS_MANAGER}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    public ResponseData<UserResponse> changeStatus(@PathVariable UUID id, @RequestParam String status) {
        User updated = userService.changeStatus(id, status);
        return ResponseData.<UserResponse>builder().status(200).messageCode("SUCCESS").data(userMapper.toResponse(updated)).build();
    }

    @DeleteMapping("/user/soft-delete/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN})
    public ResponseData<Void> softDelete(@PathVariable UUID id) {
        userService.changeStatus(id, "deleted");
        return ResponseData.<Void>builder().status(200).messageCode("SUCCESS").build();
    }

    @GetMapping("/user/me")
    @RequireAuth(roles = {RoleType.ALL})
    public ResponseData<UserResponse> getMe() {
        UUID myId = SecurityContextHolder.getAuthInfo().getId();
        User entity = userService.getOne(myId).orElse(null);
        return ResponseData.<UserResponse>builder().status(200).messageCode("SUCCESS").data(entity != null ? userMapper.toResponse(entity) : null).build();
    }

    private void verifySelfOrAdmin(UUID targetId) {
        com.unitel.fms.backend.dtos.AuthInfo authInfo = SecurityContextHolder.getAuthInfo();
        if (authInfo.hasAnyRole(RoleType.SYSTEM_ADMIN)) {
            return;
        }
        if (!authInfo.getId().equals(targetId)) {
            throw new AccessDeniedException("Chỉ được phép cập nhật thông tin của chính mình");
        }
    }
}

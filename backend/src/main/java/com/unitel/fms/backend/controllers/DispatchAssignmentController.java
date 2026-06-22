package com.unitel.fms.backend.controllers;

import com.unitel.fms.backend.customizeanotations.RequireAuth;
import com.unitel.fms.backend.dtos.request.DispatchAssignmentRequest;
import com.unitel.fms.backend.dtos.request.OverrideRequest;
import com.unitel.fms.backend.dtos.response.DispatchAssignmentResponse;
import com.unitel.fms.backend.dtos.response.ResponseData;
import com.unitel.fms.backend.entities.DispatchAssignment;
import com.unitel.fms.backend.mappers.DispatchAssignmentMapper;
import com.unitel.fms.backend.services.impl.entity.DispatchAssignmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dispatch-assignments")
public class DispatchAssignmentController {

    @Autowired
    private DispatchAssignmentService dispatchAssignmentService;

    @Autowired
    private DispatchAssignmentMapper dispatchAssignmentMapper;

    @RequireAuth(roles = {"OPS_MANAGER", "DISPATCHER"}, inWorkspace = true)
    @PostMapping
    public ResponseEntity<ResponseData<DispatchAssignmentResponse>> create(@Valid @RequestBody DispatchAssignmentRequest request) {
        DispatchAssignment entity = dispatchAssignmentMapper.toEntity(request);
        DispatchAssignment saved = dispatchAssignmentService.create(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseData.<DispatchAssignmentResponse>builder()
                .status(201).messageCode("CREATED").data(dispatchAssignmentMapper.toResponse(saved)).build());
    }

    @RequireAuth(roles = {"OPS_MANAGER", "DISPATCHER", "SYSTEM_ADMIN", "DRIVER"}, inWorkspace = true)
    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<DispatchAssignmentResponse>> get(@PathVariable UUID id) {
        DispatchAssignment entity = dispatchAssignmentService.getOne(id).orElseThrow();
        return ResponseEntity.ok(ResponseData.<DispatchAssignmentResponse>builder()
                .status(200).messageCode("SUCCESS").data(dispatchAssignmentMapper.toResponse(entity)).build());
    }

    @RequireAuth(roles = {"OPS_MANAGER"}, inWorkspace = true)
    @PostMapping("/{id}/override")
    public ResponseEntity<ResponseData<DispatchAssignmentResponse>> override(@PathVariable UUID id, @Valid @RequestBody OverrideRequest request) {
        DispatchAssignment overridden = dispatchAssignmentService.applyOverride(id, request.getOverrideReason());
        return ResponseEntity.ok(ResponseData.<DispatchAssignmentResponse>builder()
                .status(200).messageCode("OVERRIDDEN").data(dispatchAssignmentMapper.toResponse(overridden)).build());
    }
}

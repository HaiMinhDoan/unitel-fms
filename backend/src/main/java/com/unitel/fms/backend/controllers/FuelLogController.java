package com.unitel.fms.backend.controllers;

import com.unitel.fms.backend.constants.enums.EntityType;
import com.unitel.fms.backend.customizeanotations.RequireAuth;
import com.unitel.fms.backend.dtos.request.FuelLogRequest;
import com.unitel.fms.backend.dtos.response.FuelLogResponse;
import com.unitel.fms.backend.dtos.response.ResponseData;
import com.unitel.fms.backend.entities.FuelLog;
import com.unitel.fms.backend.mappers.FuelLogMapper;
import com.unitel.fms.backend.services.impl.entity.FuelLogService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/fuel-logs")
public class FuelLogController {

    @Autowired
    private FuelLogService fuelLogService;

    @Autowired
    private FuelLogMapper fuelLogMapper;

    @Autowired
    private com.unitel.fms.backend.services.impl.entity.FileAttachmentService fileAttachmentService;

    @RequireAuth(roles = {"DRIVER", "DISPATCHER", "FLEET_MANAGER"}, inWorkspace = true)
    @PostMapping(consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseData<FuelLogResponse>> create(@Valid @ModelAttribute FuelLogRequest request) {
        FuelLog entity = fuelLogMapper.toEntity(request);
        FuelLog saved = fuelLogService.create(entity);
        
        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            for (org.springframework.web.multipart.MultipartFile file : request.getFiles()) {
                fileAttachmentService.upload(file, EntityType.FUEL_LOG, saved.getId());
            }
        }
        
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseData.<FuelLogResponse>builder()
                .status(201).messageCode("CREATED").data(fuelLogMapper.toResponse(saved)).build());
    }

    @RequireAuth(roles = {"OPS_MANAGER", "DISPATCHER", "FLEET_MANAGER", "SYSTEM_ADMIN"}, inWorkspace = true)
    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<FuelLogResponse>> get(@PathVariable UUID id) {
        FuelLog entity = fuelLogService.getOne(id).orElseThrow();
        return ResponseEntity.ok(ResponseData.<FuelLogResponse>builder()
                .status(200).messageCode("SUCCESS").data(fuelLogMapper.toResponse(entity)).build());
    }

    @RequireAuth(roles = {"OPS_MANAGER", "DISPATCHER", "FLEET_MANAGER", "SYSTEM_ADMIN", "DRIVER"}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    @PostMapping("/filter")
    public ResponseEntity<ResponseData<org.springframework.data.domain.Page<FuelLogResponse>>> filter(@Valid @RequestBody com.unitel.fms.backend.dtos.request.BaseFilterRequest filterRequest) {
        org.springframework.data.domain.Page<FuelLogResponse> page = fuelLogService.filter(filterRequest).map(fuelLogMapper::toResponse);
        return ResponseEntity.ok(ResponseData.<org.springframework.data.domain.Page<FuelLogResponse>>builder()
                .status(200).messageCode("SUCCESS").data(page).build());
    }
}

package com.unitel.fms.backend.controllers;

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

    @RequireAuth(roles = {"DRIVER", "DISPATCHER", "FLEET_MANAGER"}, inWorkspace = true)
    @PostMapping
    public ResponseEntity<ResponseData<FuelLogResponse>> create(@Valid @RequestBody FuelLogRequest request) {
        FuelLog entity = fuelLogMapper.toEntity(request);
        FuelLog saved = fuelLogService.create(entity);
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
}

package com.unitel.fms.backend.controllers;

import com.unitel.fms.backend.customizeanotations.RequireAuth;
import com.unitel.fms.backend.dtos.request.TripStopRequest;
import com.unitel.fms.backend.dtos.response.ResponseData;
import com.unitel.fms.backend.dtos.response.TripStopResponse;
import com.unitel.fms.backend.entities.TripStop;
import com.unitel.fms.backend.mappers.TripStopMapper;
import com.unitel.fms.backend.services.impl.entity.TripStopService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/trip-stops")
public class TripStopController {

    @Autowired
    private TripStopService tripStopService;

    @Autowired
    private TripStopMapper tripStopMapper;

    @RequireAuth(roles = {"OPS_MANAGER", "DISPATCHER"}, inWorkspace = true)
    @PostMapping
    public ResponseEntity<ResponseData<TripStopResponse>> create(@Valid @RequestBody TripStopRequest request) {
        TripStop entity = tripStopMapper.toEntity(request);
        TripStop saved = tripStopService.create(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseData.<TripStopResponse>builder()
                .status(201).messageCode("CREATED").data(tripStopMapper.toResponse(saved)).build());
    }

    @RequireAuth(roles = {"OPS_MANAGER", "DISPATCHER", "DRIVER", "SYSTEM_ADMIN"}, inWorkspace = true)
    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<TripStopResponse>> get(@PathVariable UUID id) {
        TripStop entity = tripStopService.getOne(id).orElseThrow();
        return ResponseEntity.ok(ResponseData.<TripStopResponse>builder()
                .status(200).messageCode("SUCCESS").data(tripStopMapper.toResponse(entity)).build());
    }

    @RequireAuth(roles = {"OPS_MANAGER", "DISPATCHER"}, inWorkspace = true)
    @PutMapping("/{id}")
    public ResponseEntity<ResponseData<TripStopResponse>> update(@PathVariable UUID id, @Valid @RequestBody TripStopRequest request) {
        TripStop entity = tripStopService.getOne(id).orElseThrow();
        tripStopMapper.updateEntity(request, entity);
        TripStop updated = tripStopService.update(id, entity);
        return ResponseEntity.ok(ResponseData.<TripStopResponse>builder()
                .status(200).messageCode("UPDATED").data(tripStopMapper.toResponse(updated)).build());
    }

    @RequireAuth(roles = {"OPS_MANAGER", "DISPATCHER"}, inWorkspace = true)
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseData<Void>> delete(@PathVariable UUID id) {
        tripStopService.delete(id);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(200).messageCode("DELETED").build());
    }

    @RequireAuth(roles = {"DRIVER"}, inWorkspace = true)
    @PostMapping("/{id}/epod")
    public ResponseEntity<ResponseData<TripStopResponse>> submitEpod(
            @PathVariable UUID id,
            @RequestParam("photo") MultipartFile photo,
            @RequestParam("signature") MultipartFile signature) {
        TripStop submitted = tripStopService.submitEpod(id, photo, signature);
        return ResponseEntity.ok(ResponseData.<TripStopResponse>builder()
                .status(200).messageCode("EPOD_SUBMITTED").data(tripStopMapper.toResponse(submitted)).build());
    }

    @RequireAuth(roles = {"OPS_MANAGER", "DISPATCHER", "DRIVER", "SYSTEM_ADMIN", "FLEET_MANAGER"}, rolesLogic = RequireAuth.LogicType.OR, inWorkspace = true)
    @PostMapping("/filter")
    public ResponseEntity<ResponseData<org.springframework.data.domain.Page<TripStopResponse>>> filter(@Valid @RequestBody com.unitel.fms.backend.dtos.request.BaseFilterRequest filterRequest) {
        org.springframework.data.domain.Page<TripStopResponse> page = tripStopService.filter(filterRequest).map(tripStopMapper::toResponse);
        return ResponseEntity.ok(ResponseData.<org.springframework.data.domain.Page<TripStopResponse>>builder()
                .status(200).messageCode("SUCCESS").data(page).build());
    }
}

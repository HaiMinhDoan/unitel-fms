package com.unitel.fms.backend.controllers;

import com.unitel.fms.backend.customizeanotations.RequireAuth;
import com.unitel.fms.backend.dtos.request.TripIncidentRequest;
import com.unitel.fms.backend.dtos.response.ResponseData;
import com.unitel.fms.backend.dtos.response.TripIncidentResponse;
import com.unitel.fms.backend.entities.TripIncident;
import com.unitel.fms.backend.mappers.TripIncidentMapper;
import com.unitel.fms.backend.services.impl.entity.TripIncidentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/trip-incidents")
public class TripIncidentController {

    @Autowired
    private TripIncidentService tripIncidentService;

    @Autowired
    private TripIncidentMapper tripIncidentMapper;

    @RequireAuth(roles = {"DRIVER", "DISPATCHER"}, inWorkspace = true)
    @PostMapping
    public ResponseEntity<ResponseData<TripIncidentResponse>> create(@Valid @RequestBody TripIncidentRequest request) {
        TripIncident entity = tripIncidentMapper.toEntity(request);
        TripIncident saved = tripIncidentService.create(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseData.<TripIncidentResponse>builder()
                .status(201).messageCode("CREATED").data(tripIncidentMapper.toResponse(saved)).build());
    }

    @RequireAuth(roles = {"OPS_MANAGER", "DISPATCHER", "DRIVER", "SYSTEM_ADMIN"}, inWorkspace = true)
    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<TripIncidentResponse>> get(@PathVariable UUID id) {
        TripIncident entity = tripIncidentService.getOne(id).orElseThrow();
        return ResponseEntity.ok(ResponseData.<TripIncidentResponse>builder()
                .status(200).messageCode("SUCCESS").data(tripIncidentMapper.toResponse(entity)).build());
    }

    @RequireAuth(roles = {"OPS_MANAGER", "DISPATCHER"}, inWorkspace = true)
    @PostMapping("/{id}/resolve")
    public ResponseEntity<ResponseData<TripIncidentResponse>> resolve(
            @PathVariable UUID id,
            @RequestParam String resolutionNotes) {
        TripIncident resolved = tripIncidentService.resolve(id, resolutionNotes);
        return ResponseEntity.ok(ResponseData.<TripIncidentResponse>builder()
                .status(200).messageCode("RESOLVED").data(tripIncidentMapper.toResponse(resolved)).build());
    }
}

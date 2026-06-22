package com.unitel.fms.backend.controllers;

import com.unitel.fms.backend.customizeanotations.RequireAuth;
import com.unitel.fms.backend.dtos.response.GeofenceDto;
import com.unitel.fms.backend.mappers.GeofenceMapper;
import com.unitel.fms.backend.services.impl.entity.GeofenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/geofences")
@RequiredArgsConstructor
public class GeofenceController {

    private final GeofenceService geofenceService;
    private final GeofenceMapper geofenceMapper;

    @GetMapping("/get-all/{orgId}")
    @RequireAuth
    public ResponseEntity<List<GeofenceDto>> getAllByOrg(@PathVariable UUID orgId) {
        return ResponseEntity.ok(geofenceMapper.toDtoList(geofenceService.getAllCachedByOrg(orgId)));
    }
}

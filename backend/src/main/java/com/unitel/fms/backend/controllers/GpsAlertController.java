package com.unitel.fms.backend.controllers;

import com.unitel.fms.backend.customizeanotations.RequireAuth;
import com.unitel.fms.backend.dtos.response.GpsAlertDto;
import com.unitel.fms.backend.mappers.GpsAlertMapper;
import com.unitel.fms.backend.services.impl.entity.GpsAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/gps-alerts")
@RequiredArgsConstructor
public class GpsAlertController {

    private final GpsAlertService gpsAlertService;
    private final GpsAlertMapper gpsAlertMapper;

    @GetMapping("/get-open/{orgId}")
    @RequireAuth(roles = {"DISPATCHER", "OPS_MANAGER", "SYSTEM_ADMIN"})
    public ResponseEntity<List<GpsAlertDto>> getOpenAlerts(@PathVariable UUID orgId) {
        return ResponseEntity.ok(gpsAlertMapper.toDtoList(gpsAlertService.getOpenAlerts(orgId)));
    }

    @PatchMapping("/{id}/acknowledge")
    @RequireAuth(roles = {"DISPATCHER", "OPS_MANAGER", "SYSTEM_ADMIN"})
    public ResponseEntity<GpsAlertDto> acknowledge(@PathVariable UUID id) {
        return ResponseEntity.ok(gpsAlertMapper.toDto(gpsAlertService.acknowledge(id)));
    }
}

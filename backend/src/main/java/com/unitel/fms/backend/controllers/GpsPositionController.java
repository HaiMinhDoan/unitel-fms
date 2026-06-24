package com.unitel.fms.backend.controllers;

import com.unitel.fms.backend.customizeanotations.RequireAuth;
import com.unitel.fms.backend.dtos.request.GpsIngestRequest;
import com.unitel.fms.backend.dtos.response.GpsPositionDto;
import com.unitel.fms.backend.services.impl.entity.GpsPositionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/gps-position")
@RequiredArgsConstructor
public class GpsPositionController {

    private final GpsPositionService gpsPositionService;

    @PostMapping("/ingest")
    @RequireAuth(roles = {"DRIVER", "SYSTEM_ADMIN", "OPS_MANAGER"})
    public ResponseEntity<String> ingest(@Valid @RequestBody GpsIngestRequest request) {
        gpsPositionService.ingest(request);
        return ResponseEntity.accepted().body("Đã tiếp nhận");
    }

    @GetMapping("/get-current/{vehicleId}")
    @RequireAuth
    public ResponseEntity<GpsPositionDto> getCurrentPosition(@PathVariable UUID vehicleId) {
        return ResponseEntity.ok(gpsPositionService.getCurrentPosition(vehicleId));
    }

    @GetMapping("/get-current-all/{orgId}")
    @RequireAuth(roles = {"DISPATCHER", "OPS_MANAGER", "SYSTEM_ADMIN"})
    public ResponseEntity<List<GpsPositionDto>> getCurrentAllInOrg(@PathVariable UUID orgId) {
        return ResponseEntity.ok(gpsPositionService.getCurrentAllInOrg(orgId));
    }
}

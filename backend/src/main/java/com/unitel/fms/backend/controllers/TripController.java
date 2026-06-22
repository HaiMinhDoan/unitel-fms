package com.unitel.fms.backend.controllers;

import com.unitel.fms.backend.customizeanotations.RequireAuth;
import com.unitel.fms.backend.dtos.response.ResponseData;
import com.unitel.fms.backend.dtos.response.TripResponse;
import com.unitel.fms.backend.entities.GpsPosition;
import com.unitel.fms.backend.entities.Trip;
import com.unitel.fms.backend.mappers.TripMapper;
import com.unitel.fms.backend.services.impl.entity.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/trips")
public class TripController {

    @Autowired
    private TripService tripService;

    @Autowired
    private TripMapper tripMapper;

    @RequireAuth(roles = {"OPS_MANAGER", "DISPATCHER", "DRIVER", "SYSTEM_ADMIN"}, inWorkspace = true)
    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<TripResponse>> get(@PathVariable UUID id) {
        Trip entity = tripService.getOne(id).orElseThrow();
        return ResponseEntity.ok(ResponseData.<TripResponse>builder()
                .status(200).message("Success").data(tripMapper.toResponse(entity)).build());
    }

    @RequireAuth(roles = {"OPS_MANAGER", "DISPATCHER", "DRIVER"}, inWorkspace = true)
    @PutMapping("/{id}/status")
    public ResponseEntity<ResponseData<TripResponse>> updateStatus(@PathVariable UUID id, @RequestParam String status) {
        Trip updated = tripService.changeStatus(id, status);
        return ResponseEntity.ok(ResponseData.<TripResponse>builder()
                .status(200).message("Status updated").data(tripMapper.toResponse(updated)).build());
    }

    @RequireAuth(roles = {"OPS_MANAGER", "DISPATCHER", "SYSTEM_ADMIN"}, inWorkspace = true)
    @GetMapping("/{id}/replay")
    public ResponseEntity<ResponseData<List<GpsPosition>>> replay(@PathVariable UUID id) {
        List<GpsPosition> positions = tripService.replay(id);
        return ResponseEntity.ok(ResponseData.<List<GpsPosition>>builder()
                .status(200).message("Success").data(positions).build());
    }
}

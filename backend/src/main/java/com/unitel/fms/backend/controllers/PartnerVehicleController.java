package com.unitel.fms.backend.controllers;

import com.unitel.fms.backend.dtos.request.PartnerVehicleRequest;
import com.unitel.fms.backend.dtos.response.PartnerVehicleResponse;
import com.unitel.fms.backend.dtos.response.ResponseData;
import com.unitel.fms.backend.entities.PartnerVehicle;
import com.unitel.fms.backend.mappers.PartnerVehicleMapper;
import com.unitel.fms.backend.services.impl.entity.PartnerVehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/partner-vehicles")
@RequiredArgsConstructor
public class PartnerVehicleController {

    private final PartnerVehicleService partnerVehicleService;
    private final PartnerVehicleMapper partnerVehicleMapper;

    @GetMapping
    public ResponseData<List<PartnerVehicleResponse>> getAll() {
        List<PartnerVehicleResponse> responses = partnerVehicleService.getAll().stream()
                .map(partnerVehicleMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseData.<List<PartnerVehicleResponse>>builder().status(200).message("Thành công").data(responses).build();
    }

    @GetMapping("/{id}")
    public ResponseData<PartnerVehicleResponse> getById(@PathVariable UUID id) {
        PartnerVehicle partnerVehicle = partnerVehicleService.getOne(id).orElseThrow(() -> new RuntimeException("PartnerVehicle not found"));
        return ResponseData.<PartnerVehicleResponse>builder().status(200).message("Thành công").data(partnerVehicleMapper.toResponse(partnerVehicle)).build();
    }

    @PostMapping
    public ResponseData<PartnerVehicleResponse> create(@RequestBody @Valid PartnerVehicleRequest request) {
        PartnerVehicle entity = partnerVehicleMapper.toEntity(request);
        PartnerVehicle saved = partnerVehicleService.create(entity);
        return ResponseData.<PartnerVehicleResponse>builder().status(200).message("Thành công").data(partnerVehicleMapper.toResponse(saved)).build();
    }

    @PutMapping("/{id}")
    public ResponseData<PartnerVehicleResponse> update(@PathVariable UUID id, @RequestBody @Valid PartnerVehicleRequest request) {
        PartnerVehicle entity = partnerVehicleService.getOne(id).orElseThrow(() -> new RuntimeException("PartnerVehicle not found"));
        partnerVehicleMapper.updateEntityFromRequest(request, entity);
        PartnerVehicle saved = partnerVehicleService.update(entity);
        return ResponseData.<PartnerVehicleResponse>builder().status(200).message("Thành công").data(partnerVehicleMapper.toResponse(saved)).build();
    }

    @DeleteMapping("/{id}")
    public ResponseData<Void> delete(@PathVariable UUID id) {
        partnerVehicleService.delete(id);
        return ResponseData.<Void>builder().status(200).message("Thành công").data(null).build();
    }
}

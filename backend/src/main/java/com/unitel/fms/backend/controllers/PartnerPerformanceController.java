package com.unitel.fms.backend.controllers;

import com.unitel.fms.backend.dtos.request.PartnerPerformanceRequest;
import com.unitel.fms.backend.dtos.response.PartnerPerformanceResponse;
import com.unitel.fms.backend.dtos.response.ResponseData;
import com.unitel.fms.backend.entities.PartnerPerformance;
import com.unitel.fms.backend.mappers.PartnerPerformanceMapper;
import com.unitel.fms.backend.services.impl.entity.PartnerPerformanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/partner-performances")
@RequiredArgsConstructor
public class PartnerPerformanceController {

    private final PartnerPerformanceService partnerPerformanceService;
    private final PartnerPerformanceMapper partnerPerformanceMapper;

    @GetMapping
    public ResponseData<List<PartnerPerformanceResponse>> getAll() {
        List<PartnerPerformanceResponse> responses = partnerPerformanceService.getAll().stream()
                .map(partnerPerformanceMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseData.<List<PartnerPerformanceResponse>>builder().status(200).message("Thành công").data(responses).build();
    }

    @GetMapping("/{id}")
    public ResponseData<PartnerPerformanceResponse> getById(@PathVariable UUID id) {
        PartnerPerformance partnerPerformance = partnerPerformanceService.getOne(id).orElseThrow(() -> new RuntimeException("PartnerPerformance not found"));
        return ResponseData.<PartnerPerformanceResponse>builder().status(200).message("Thành công").data(partnerPerformanceMapper.toResponse(partnerPerformance)).build();
    }

    @PostMapping
    public ResponseData<PartnerPerformanceResponse> create(@RequestBody @Valid PartnerPerformanceRequest request) {
        PartnerPerformance entity = partnerPerformanceMapper.toEntity(request);
        PartnerPerformance saved = partnerPerformanceService.create(entity);
        return ResponseData.<PartnerPerformanceResponse>builder().status(200).message("Thành công").data(partnerPerformanceMapper.toResponse(saved)).build();
    }

    @PutMapping("/{id}")
    public ResponseData<PartnerPerformanceResponse> update(@PathVariable UUID id, @RequestBody @Valid PartnerPerformanceRequest request) {
        PartnerPerformance entity = partnerPerformanceService.getOne(id).orElseThrow(() -> new RuntimeException("PartnerPerformance not found"));
        partnerPerformanceMapper.updateEntityFromRequest(request, entity);
        PartnerPerformance saved = partnerPerformanceService.update(entity);
        return ResponseData.<PartnerPerformanceResponse>builder().status(200).message("Thành công").data(partnerPerformanceMapper.toResponse(saved)).build();
    }

    @DeleteMapping("/{id}")
    public ResponseData<Void> delete(@PathVariable UUID id) {
        partnerPerformanceService.delete(id);
        return ResponseData.<Void>builder().status(200).message("Thành công").data(null).build();
    }

    @PostMapping("/recalculate")
    public ResponseData<PartnerPerformanceResponse> recalculate(@RequestParam UUID partnerId, @RequestParam String periodMonth) {
        PartnerPerformance pp = partnerPerformanceService.recalculateForPartnerAndMonth(partnerId, periodMonth);
        return ResponseData.<PartnerPerformanceResponse>builder().status(200).message("Thành công").data(partnerPerformanceMapper.toResponse(pp)).build();
    }
}

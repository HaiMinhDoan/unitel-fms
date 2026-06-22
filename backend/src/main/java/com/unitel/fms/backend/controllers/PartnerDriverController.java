package com.unitel.fms.backend.controllers;

import com.unitel.fms.backend.dtos.request.PartnerDriverRequest;
import com.unitel.fms.backend.dtos.response.PartnerDriverResponse;
import com.unitel.fms.backend.dtos.response.ResponseData;
import com.unitel.fms.backend.entities.PartnerDriver;
import com.unitel.fms.backend.mappers.PartnerDriverMapper;
import com.unitel.fms.backend.services.impl.entity.PartnerDriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/partner-drivers")
@RequiredArgsConstructor
public class PartnerDriverController {

    private final PartnerDriverService partnerDriverService;
    private final PartnerDriverMapper partnerDriverMapper;

    @GetMapping
    public ResponseData<List<PartnerDriverResponse>> getAll() {
        List<PartnerDriverResponse> responses = partnerDriverService.getAll().stream()
                .map(partnerDriverMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseData.<List<PartnerDriverResponse>>builder().status(200).messageCode("SUCCESS").data(responses).build();
    }

    @GetMapping("/{id}")
    public ResponseData<PartnerDriverResponse> getById(@PathVariable UUID id) {
        PartnerDriver partnerDriver = partnerDriverService.getOne(id).orElseThrow(() -> new RuntimeException("PartnerDriver not found"));
        return ResponseData.<PartnerDriverResponse>builder().status(200).messageCode("SUCCESS").data(partnerDriverMapper.toResponse(partnerDriver)).build();
    }

    @PostMapping
    public ResponseData<PartnerDriverResponse> create(@RequestBody @Valid PartnerDriverRequest request) {
        PartnerDriver entity = partnerDriverMapper.toEntity(request);
        PartnerDriver saved = partnerDriverService.create(entity);
        return ResponseData.<PartnerDriverResponse>builder().status(200).messageCode("SUCCESS").data(partnerDriverMapper.toResponse(saved)).build();
    }

    @PutMapping("/{id}")
    public ResponseData<PartnerDriverResponse> update(@PathVariable UUID id, @RequestBody @Valid PartnerDriverRequest request) {
        PartnerDriver entity = partnerDriverService.getOne(id).orElseThrow(() -> new RuntimeException("PartnerDriver not found"));
        partnerDriverMapper.updateEntityFromRequest(request, entity);
        PartnerDriver saved = partnerDriverService.update(entity);
        return ResponseData.<PartnerDriverResponse>builder().status(200).messageCode("SUCCESS").data(partnerDriverMapper.toResponse(saved)).build();
    }

    @DeleteMapping("/{id}")
    public ResponseData<Void> delete(@PathVariable UUID id) {
        partnerDriverService.delete(id);
        return ResponseData.<Void>builder().status(200).messageCode("SUCCESS").data(null).build();
    }
}

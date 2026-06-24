package com.unitel.fms.backend.controllers;

import com.unitel.fms.backend.constants.enums.EntityType;
import com.unitel.fms.backend.customizeanotations.RequireAuth;
import com.unitel.fms.backend.constants.enums.RoleType;
import com.unitel.fms.backend.dtos.request.BaseFilterRequest;
import com.unitel.fms.backend.dtos.request.DriverDocumentRequest;
import com.unitel.fms.backend.dtos.response.DriverDocumentResponse;
import com.unitel.fms.backend.dtos.response.ResponseData;
import com.unitel.fms.backend.entities.DriverDocument;
import com.unitel.fms.backend.mappers.DriverDocumentMapper;
import com.unitel.fms.backend.services.impl.entity.DriverDocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DriverDocumentController {

    private final DriverDocumentService driverDocumentService;
    private final DriverDocumentMapper driverDocumentMapper;
    private final com.unitel.fms.backend.services.impl.entity.FileAttachmentService fileAttachmentService;

    @PostMapping(value = "/driver-document/create", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequireAuth(roles = {RoleType.HR_LEGAL}, inWorkspace = true)
    public ResponseData<DriverDocumentResponse> create(@Valid @ModelAttribute DriverDocumentRequest request) {
        DriverDocument entity = driverDocumentMapper.toEntity(request);
        DriverDocument saved = driverDocumentService.create(entity);
        
        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            for (org.springframework.web.multipart.MultipartFile file : request.getFiles()) {
                fileAttachmentService.upload(file, EntityType.DRIVER_DOCUMENT, saved.getId());
            }
        }
        
        return ResponseData.<DriverDocumentResponse>builder().status(200).messageCode("SUCCESS").data(driverDocumentMapper.toResponse(saved)).build();
    }

    @PutMapping(value = "/driver-document/update/{id}", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequireAuth(roles = {RoleType.HR_LEGAL}, inWorkspace = true)
    public ResponseData<DriverDocumentResponse> update(@PathVariable UUID id, @Valid @ModelAttribute DriverDocumentRequest request) {
        DriverDocument entity = driverDocumentMapper.toEntity(request);
        DriverDocument updated = driverDocumentService.update(id, entity);
        
        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            for (org.springframework.web.multipart.MultipartFile file : request.getFiles()) {
                fileAttachmentService.upload(file, EntityType.DRIVER_DOCUMENT, updated.getId());
            }
        }
        
        return ResponseData.<DriverDocumentResponse>builder().status(200).messageCode("SUCCESS").data(driverDocumentMapper.toResponse(updated)).build();
    }

    @PatchMapping("/driver-document/update-partial/{id}")
    @RequireAuth(roles = {RoleType.HR_LEGAL}, inWorkspace = true)
    public ResponseData<DriverDocumentResponse> updatePartial(@PathVariable UUID id, @Valid @RequestBody Map<String, Object> updates) {
        DriverDocument updated = driverDocumentService.updateFromMap(id, updates);
        return ResponseData.<DriverDocumentResponse>builder().status(200).messageCode("SUCCESS").data(driverDocumentMapper.toResponse(updated)).build();
    }

    @GetMapping("/driver-document/get-by-id/{id}")
    @RequireAuth(roles = {RoleType.ALL}, inWorkspace = true)
    public ResponseData<DriverDocumentResponse> getById(@PathVariable UUID id) {
        DriverDocument entity = driverDocumentService.getOne(id).orElse(null);
        if (entity == null) {
            return ResponseData.<DriverDocumentResponse>builder().status(404).messageCode("NOT_FOUND").build();
        }
        return ResponseData.<DriverDocumentResponse>builder().status(200).messageCode("SUCCESS").data(driverDocumentMapper.toResponse(entity)).build();
    }

    @PostMapping("/driver-documents/filter")
    @RequireAuth(roles = {RoleType.HR_LEGAL}, inWorkspace = true)
    public ResponseData<Page<DriverDocumentResponse>> filter(@Valid @RequestBody BaseFilterRequest filter) {
        Page<DriverDocumentResponse> page = driverDocumentService.filter(filter).map(driverDocumentMapper::toResponse);
        return ResponseData.<Page<DriverDocumentResponse>>builder().status(200).messageCode("SUCCESS").data(page).build();
    }

    @PatchMapping("/driver-document/change-status/{id}")
    @RequireAuth(roles = {RoleType.HR_LEGAL}, inWorkspace = true)
    public ResponseData<DriverDocumentResponse> changeStatus(@PathVariable UUID id, @RequestParam String status) {
        DriverDocument updated = driverDocumentService.changeStatus(id, status);
        return ResponseData.<DriverDocumentResponse>builder().status(200).messageCode("SUCCESS").data(driverDocumentMapper.toResponse(updated)).build();
    }

    @DeleteMapping("/driver-document/soft-delete/{id}")
    @RequireAuth(roles = {RoleType.HR_LEGAL}, inWorkspace = true)
    public ResponseData<Void> softDelete(@PathVariable UUID id) {
        driverDocumentService.changeStatus(id, "deleted");
        return ResponseData.<Void>builder().status(200).messageCode("SUCCESS").build();
    }

    @DeleteMapping("/driver-document/hard-delete/{id}")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN})
    public ResponseData<Void> hardDelete(@PathVariable UUID id) {
        driverDocumentService.delete(id);
        return ResponseData.<Void>builder().status(200).messageCode("SUCCESS").build();
    }

    @GetMapping("/driver-documents/get-by-driver/{driverId}")
    @RequireAuth(roles = {RoleType.ALL}, inWorkspace = true)
    public ResponseData<List<DriverDocumentResponse>> getByDriver(@PathVariable UUID driverId) {
        BaseFilterRequest filter = new BaseFilterRequest();
        filter.getFilters().add(com.unitel.fms.backend.dtos.request.FilterCriteria.builder()
                .fieldName("driver.id")
                .operation(com.unitel.fms.backend.constants.enums.FilterOperation.EQUALS)
                .value(driverId.toString())
                .build());
        List<DriverDocumentResponse> list = driverDocumentService.filter(filter).getContent().stream()
                .map(driverDocumentMapper::toResponse).toList();
        return ResponseData.<List<DriverDocumentResponse>>builder().status(200).messageCode("SUCCESS").data(list).build();
    }

    @GetMapping("/driver-documents/get-expiring-soon")
    @RequireAuth(roles = {RoleType.HR_LEGAL}, inWorkspace = true)
    public ResponseData<List<DriverDocumentResponse>> getExpiringSoon() {
        UUID orgId = com.unitel.fms.backend.contexts.SecurityContextHolder.getAuthInfo().getOrgId();
        List<DriverDocumentResponse> list = driverDocumentService.getExpiringSoon(orgId).stream()
                .map(driverDocumentMapper::toResponse).toList();
        return ResponseData.<List<DriverDocumentResponse>>builder().status(200).messageCode("SUCCESS").data(list).build();
    }
}

package com.unitel.fms.backend.controllers;

import com.unitel.fms.backend.customizeanotations.RequireAuth;
import com.unitel.fms.backend.dtos.request.BulkUpdateItem;
import com.unitel.fms.backend.dtos.request.DispatchRequestRequest;
import com.unitel.fms.backend.dtos.response.DispatchRequestResponse;
import com.unitel.fms.backend.dtos.response.ResponseData;
import com.unitel.fms.backend.entities.DispatchRequest;
import com.unitel.fms.backend.mappers.DispatchRequestMapper;
import com.unitel.fms.backend.services.impl.entity.DispatchRequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/dispatch-requests")
public class DispatchRequestController {

    @Autowired
    private DispatchRequestService dispatchRequestService;

    @Autowired
    private DispatchRequestMapper dispatchRequestMapper;

    @RequireAuth(roles = {"OPS_MANAGER", "DISPATCHER"}, inWorkspace = true)
    @PostMapping
    public ResponseEntity<ResponseData<DispatchRequestResponse>> create(@Valid @RequestBody DispatchRequestRequest request) {
        DispatchRequest entity = dispatchRequestMapper.toEntity(request);
        DispatchRequest saved = dispatchRequestService.create(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseData.<DispatchRequestResponse>builder()
                .status(201).message("Created").data(dispatchRequestMapper.toResponse(saved)).build());
    }

    @RequireAuth(roles = {"OPS_MANAGER", "DISPATCHER", "SYSTEM_ADMIN"}, inWorkspace = true)
    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<DispatchRequestResponse>> get(@PathVariable UUID id) {
        DispatchRequest entity = dispatchRequestService.getOne(id).orElseThrow();
        return ResponseEntity.ok(ResponseData.<DispatchRequestResponse>builder()
                .status(200).message("Success").data(dispatchRequestMapper.toResponse(entity)).build());
    }

    @RequireAuth(roles = {"OPS_MANAGER", "DISPATCHER"}, inWorkspace = true)
    @PutMapping("/{id}")
    public ResponseEntity<ResponseData<DispatchRequestResponse>> update(@PathVariable UUID id, @Valid @RequestBody DispatchRequestRequest request) {
        DispatchRequest entity = dispatchRequestService.getOne(id).orElseThrow();
        dispatchRequestMapper.updateEntity(request, entity);
        DispatchRequest updated = dispatchRequestService.update(id, entity);
        return ResponseEntity.ok(ResponseData.<DispatchRequestResponse>builder()
                .status(200).message("Updated").data(dispatchRequestMapper.toResponse(updated)).build());
    }

    @RequireAuth(roles = {"OPS_MANAGER", "DISPATCHER"}, inWorkspace = true)
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseData<Void>> delete(@PathVariable UUID id) {
        dispatchRequestService.delete(id);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(200).message("Deleted").build());
    }

    @RequireAuth(roles = {"OPS_MANAGER", "DISPATCHER"}, inWorkspace = true)
    @GetMapping("/queue")
    public ResponseEntity<ResponseData<List<DispatchRequestResponse>>> getQueue(@RequestParam UUID orgId) {
        List<DispatchRequest> queue = dispatchRequestService.getQueue(orgId);
        List<DispatchRequestResponse> data = queue.stream().map(dispatchRequestMapper::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ResponseData.<List<DispatchRequestResponse>>builder()
                .status(200).message("Success").data(data).build());
    }

    @RequireAuth(roles = {"OPS_MANAGER", "DISPATCHER"}, inWorkspace = true)
    @PostMapping("/bulk-update")
    public ResponseEntity<ResponseData<List<DispatchRequestResponse>>> bulkUpdate(@RequestBody List<BulkUpdateItem> items) {
        List<DispatchRequest> updated = dispatchRequestService.bulkUpdate(items);
        List<DispatchRequestResponse> data = updated.stream().map(dispatchRequestMapper::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ResponseData.<List<DispatchRequestResponse>>builder()
                .status(200).message("Updated").data(data).build());
    }
}

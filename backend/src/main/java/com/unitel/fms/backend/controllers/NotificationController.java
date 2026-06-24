package com.unitel.fms.backend.controllers;

import com.unitel.fms.backend.dtos.request.BaseFilterRequest;
import com.unitel.fms.backend.dtos.request.NotificationRequest;
import com.unitel.fms.backend.dtos.response.ResponseData;
import com.unitel.fms.backend.dtos.response.NotificationResponse;
import com.unitel.fms.backend.entities.Notification;
import com.unitel.fms.backend.customizeanotations.RequireAuth;
import com.unitel.fms.backend.mappers.NotificationMapper;
import com.unitel.fms.backend.services.impl.entity.NotificationService;
import com.unitel.fms.backend.constants.enums.RoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationMapper notificationMapper;

    @PostMapping("/notification/create")
    @RequireAuth(roles = {RoleType.SYSTEM_ADMIN})
    public ResponseData<NotificationResponse> create(@Valid @RequestBody NotificationRequest request) {
        Notification entity = notificationMapper.toEntity(request);
        Notification saved = notificationService.create(entity);
        return ResponseData.<NotificationResponse>builder().status(200).messageCode("SUCCESS").data(notificationMapper.toResponse(saved)).build();
    }

    @GetMapping("/notification/get-by-id/{id}")
    @RequireAuth(roles = {RoleType.ALL})
    public ResponseData<NotificationResponse> getById(@PathVariable UUID id) {
        Notification entity = notificationService.getOne(id).orElse(null);
        return ResponseData.<NotificationResponse>builder().status(200).messageCode("SUCCESS").data(entity != null ? notificationMapper.toResponse(entity) : null).build();
    }

    @PatchMapping("/notification/mark-read/{id}")
    @RequireAuth(roles = {RoleType.ALL})
    public ResponseData<NotificationResponse> markRead(@PathVariable UUID id) {
        Notification entity = notificationService.getOne(id).orElseThrow(() -> new RuntimeException("Not found"));
        // TODO: Validate that the notification belongs to the current user
        entity.setIsRead(true);
        Notification updated = notificationService.update(entity);
        return ResponseData.<NotificationResponse>builder().status(200).messageCode("SUCCESS").data(notificationMapper.toResponse(updated)).build();
    }

    @GetMapping("/notifications/my")
    @RequireAuth(roles = {RoleType.ALL})
    public ResponseData<List<NotificationResponse>> getMyNotifications() {
        // Mock implementation, should call service with user ID
        UUID userId = com.unitel.fms.backend.contexts.SecurityContextHolder.getAuthInfo().getId();
        return ResponseData.<List<NotificationResponse>>builder().status(200).messageCode("SUCCESS").data(List.of()).build();
    }

    @PostMapping("/notifications/my-filter")
    @RequireAuth(roles = {RoleType.ALL})
    public ResponseData<Page<NotificationResponse>> filterMyNotifications(@Valid @RequestBody BaseFilterRequest filter) {
        // Mock implementation
        UUID userId = com.unitel.fms.backend.contexts.SecurityContextHolder.getAuthInfo().getId();
        return ResponseData.<Page<NotificationResponse>>builder().status(200).messageCode("SUCCESS").data(Page.empty()).build();
    }
}

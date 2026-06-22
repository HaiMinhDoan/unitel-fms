package com.unitel.fms.backend.controllers;

import com.unitel.fms.backend.customizeanotations.RequireAuth;
import com.unitel.fms.backend.dtos.response.FileAttachmentResponse;
import com.unitel.fms.backend.entities.FileAttachment;
import com.unitel.fms.backend.mappers.FileAttachmentMapper;
import com.unitel.fms.backend.services.impl.entity.FileAttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileAttachmentController {

    private final FileAttachmentService fileAttachmentService;
    private final FileAttachmentMapper fileAttachmentMapper;

    @PostMapping("/upload")
    @RequireAuth
    public ResponseEntity<FileAttachmentResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "attachmentType", defaultValue = "general") String attachmentType,
            @RequestParam(value = "referenceId", required = false) UUID referenceId) {
        
        if (referenceId == null) {
            referenceId = UUID.randomUUID(); // Fallback if no reference
        }
        
        FileAttachment attachment = fileAttachmentService.upload(file, attachmentType, referenceId);
        return ResponseEntity.ok(fileAttachmentMapper.toResponse(attachment));
    }

    @GetMapping("/{id}")
    @RequireAuth
    public ResponseEntity<FileAttachmentResponse> getFileMetadata(@PathVariable UUID id) {
        return fileAttachmentService.getOne(id)
                .map(fileAttachmentMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @RequireAuth(roles = {"SYSTEM_ADMIN", "OPS_MANAGER", "FLEET_MANAGER"})
    public ResponseEntity<Void> deleteFile(@PathVariable UUID id) {
        fileAttachmentService.deleteAttachment(id);
        return ResponseEntity.ok().build();
    }
}

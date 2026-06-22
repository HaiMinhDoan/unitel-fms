package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.FileAttachment;
import com.unitel.fms.backend.repositories.FileAttachmentRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FileAttachmentService extends BaseServiceImpl<FileAttachment, UUID> {

    private final FileAttachmentRepository fileAttachmentRepository = (FileAttachmentRepository) getRepository();

    @Autowired
    private EntityManager entityManager;

    public FileAttachmentService(FileAttachmentRepository repository) {
        super(repository);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    @Autowired
    private com.unitel.fms.backend.services.MinioService minioService;
    
    @Autowired
    private com.unitel.fms.backend.services.impl.entity.AuditLogService auditLogService;

    public FileAttachment upload(org.springframework.web.multipart.MultipartFile file, String attachmentType, UUID referenceId) {
        try {
            String objectName = attachmentType + "/" + referenceId + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
            String objectKey = minioService.upload(file, objectName);
            
            FileAttachment attachment = new FileAttachment();
            attachment.setBucket(minioService.getBucketName());
            attachment.setObjectKey(objectKey);
            attachment.setOriginalName(file.getOriginalFilename());
            attachment.setMimeType(file.getContentType());
            attachment.setExtension(org.springframework.util.StringUtils.getFilenameExtension(file.getOriginalFilename()));
            attachment.setSizeBytes(file.getSize());
            attachment.setEntityType(attachmentType);
            attachment.setEntityId(referenceId);
            attachment.setStatus("active");
            
            com.unitel.fms.backend.dtos.AuthInfo auth = com.unitel.fms.backend.contexts.SecurityContextHolder.getAuthInfo();
            if (auth != null && auth.getId() != null) {
                attachment.setUploadedBy(auth.getId());
            }

            FileAttachment saved = super.create(attachment);
            auditLogService.record("UPLOAD", "FileAttachment", saved.getId(), null, saved);
            
            return saved;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to MinIO", e);
        }
    }

    public void deleteAttachment(UUID id) {
        FileAttachment attachment = getOne(id).orElseThrow();
        try {
            minioService.delete(attachment.getBucket(), attachment.getObjectKey());
            super.delete(id);
            auditLogService.record("DELETE", "FileAttachment", id, null, attachment);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file from MinIO", e);
        }
    }

    @org.springframework.transaction.annotation.Transactional
    public void deleteAllByEntity(String entityType, UUID entityId) {
        java.util.List<FileAttachment> attachments = fileAttachmentRepository.findByEntityTypeAndEntityId(entityType, entityId);
        for (FileAttachment attachment : attachments) {
            deleteAttachment(attachment.getId());
        }
    }
}

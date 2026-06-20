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
}

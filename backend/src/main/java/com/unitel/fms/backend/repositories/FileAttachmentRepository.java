package com.unitel.fms.backend.repositories;

import com.unitel.fms.backend.entities.FileAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FileAttachmentRepository extends JpaRepository<FileAttachment, UUID>, JpaSpecificationExecutor<FileAttachment> {

    @Query("""
            select f from FileAttachment f
                        where f.entityType ilike :entityType
                                     and f.entityId = :entityId
                                                 and f.status = 'active'
            """)
    java.util.List<FileAttachment> findByEntityTypeAndEntityId(String entityType, UUID entityId);
}

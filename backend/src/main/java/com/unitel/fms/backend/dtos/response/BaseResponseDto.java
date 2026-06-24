package com.unitel.fms.backend.dtos.response;

import com.unitel.fms.backend.mappers.FileAttachmentMapper;
import com.unitel.fms.backend.services.impl.entity.FileAttachmentService;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

@Component
public abstract class BaseResponseDto {

    @Autowired
    private FileAttachmentService fileAttachmentService;
    @Autowired
    private FileAttachmentMapper fileAttachmentMapper;

    public List<FileAttachmentResponse> getFileAttachments() {
        Class<?> clazz = this.getClass();

        Table table = clazz.getAnnotation(Table.class);
        String tableName = (table != null) ? table.name() : clazz.getSimpleName().toLowerCase();

        // Lấy value của field có @Id
        UUID id = null;
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                field.setAccessible(true);
                try {
                    id = (UUID) field.get(this);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Cannot access @Id field", e);
                }
                break;
            }
        }

        return fileAttachmentService.getByEntity(tableName, id)
                .stream().map(
                        fileAttachmentMapper::toResponse
                ).toList();
    }
}

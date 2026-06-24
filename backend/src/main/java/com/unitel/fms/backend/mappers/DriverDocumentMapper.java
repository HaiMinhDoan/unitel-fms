package com.unitel.fms.backend.mappers;

import com.unitel.fms.backend.dtos.request.DriverDocumentRequest;
import com.unitel.fms.backend.dtos.response.DriverDocumentResponse;
import com.unitel.fms.backend.entities.DriverDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = org.mapstruct.NullValueCheckStrategy.ALWAYS)
public abstract class DriverDocumentMapper {

    @org.springframework.beans.factory.annotation.Autowired
    protected com.unitel.fms.backend.services.impl.entity.FileAttachmentService fileAttachmentService;
    
    @org.springframework.beans.factory.annotation.Autowired
    protected FileAttachmentMapper fileAttachmentMapper;

    @Mapping(target = "driver.id", source = "driverId")
    public abstract DriverDocument toEntity(DriverDocumentRequest request);

    @Mapping(target = "driverId", source = "driver.id")
    public abstract DriverDocumentResponse toResponse(DriverDocument entity);

    @org.mapstruct.AfterMapping
    protected void linkAttachments(DriverDocument entity, @MappingTarget DriverDocumentResponse response) {
        if (entity.getId() != null && fileAttachmentService != null && fileAttachmentMapper != null) {
            java.util.List<com.unitel.fms.backend.entities.FileAttachment> files = fileAttachmentService.getByEntity("driver_documents", entity.getId());
            response.setAttachments(files.stream().map(fileAttachmentMapper::toResponse).collect(java.util.stream.Collectors.toList()));
        }
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "driver.id", source = "driverId")
    public abstract void updateEntity(DriverDocumentRequest request, @MappingTarget DriverDocument entity);
}

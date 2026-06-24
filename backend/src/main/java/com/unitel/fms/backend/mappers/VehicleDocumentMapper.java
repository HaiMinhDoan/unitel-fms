package com.unitel.fms.backend.mappers;

import com.unitel.fms.backend.dtos.request.VehicleDocumentRequest;
import com.unitel.fms.backend.dtos.response.VehicleDocumentResponse;
import com.unitel.fms.backend.entities.VehicleDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = org.mapstruct.NullValueCheckStrategy.ALWAYS)
public abstract class VehicleDocumentMapper {

    @org.springframework.beans.factory.annotation.Autowired
    protected com.unitel.fms.backend.services.impl.entity.FileAttachmentService fileAttachmentService;
    
    @org.springframework.beans.factory.annotation.Autowired
    protected FileAttachmentMapper fileAttachmentMapper;

    @Mapping(target = "vehicle.id", source = "vehicleId")
    public abstract VehicleDocument toEntity(VehicleDocumentRequest request);

    @Mapping(target = "vehicleId", source = "vehicle.id")
    public abstract VehicleDocumentResponse toResponse(VehicleDocument entity);

    @org.mapstruct.AfterMapping
    protected void linkAttachments(VehicleDocument entity, @MappingTarget VehicleDocumentResponse response) {
        if (entity.getId() != null && fileAttachmentService != null && fileAttachmentMapper != null) {
            java.util.List<com.unitel.fms.backend.entities.FileAttachment> files = fileAttachmentService.getByEntity("vehicle_documents", entity.getId());
            response.setAttachments(files.stream().map(fileAttachmentMapper::toResponse).collect(java.util.stream.Collectors.toList()));
        }
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehicle.id", source = "vehicleId")
    public abstract void updateEntity(VehicleDocumentRequest request, @MappingTarget VehicleDocument entity);
}

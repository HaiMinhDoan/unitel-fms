package com.unitel.fms.backend.mappers;

import com.unitel.fms.backend.dtos.request.TripIncidentRequest;
import com.unitel.fms.backend.dtos.response.TripIncidentResponse;
import com.unitel.fms.backend.entities.TripIncident;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = org.mapstruct.NullValueCheckStrategy.ALWAYS)
public abstract class TripIncidentMapper {

    @org.springframework.beans.factory.annotation.Autowired
    protected com.unitel.fms.backend.services.impl.entity.FileAttachmentService fileAttachmentService;

    @org.springframework.beans.factory.annotation.Autowired
    protected FileAttachmentMapper fileAttachmentMapper;

    @Mapping(target = "trip.id", source = "tripId")
    @Mapping(target = "reportedBy.id", source = "reportedBy")
    @Mapping(target = "resolvedBy.id", source = "resolvedBy")
    @Mapping(target = "metadata", ignore = true)
    public abstract TripIncident toEntity(TripIncidentRequest request);

    @Mapping(target = "tripId", source = "trip.id")
    @Mapping(target = "reportedBy", source = "reportedBy.id")
    @Mapping(target = "resolvedBy", source = "resolvedBy.id")
    @Mapping(target = "resolutionNotes", ignore = true)
    @Mapping(target = "severity", ignore = true)
    @Mapping(target = "reportedAt", source = "createdAt")
    public abstract TripIncidentResponse toResponse(TripIncident entity);

    @org.mapstruct.AfterMapping
    protected void linkAttachments(TripIncident entity, @MappingTarget TripIncidentResponse response) {
        if (entity.getId() != null && fileAttachmentService != null && fileAttachmentMapper != null) {
            java.util.List<com.unitel.fms.backend.entities.FileAttachment> files = fileAttachmentService.getByEntity("trip_incidents", entity.getId());
            response.setAttachments(files.stream().map(fileAttachmentMapper::toResponse).collect(java.util.stream.Collectors.toList()));
        }
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trip.id", source = "tripId")
    @Mapping(target = "reportedBy.id", source = "reportedBy")
    @Mapping(target = "resolvedBy.id", source = "resolvedBy")
    @Mapping(target = "metadata", ignore = true)
    public abstract void updateEntity(TripIncidentRequest request, @MappingTarget TripIncident entity);
}

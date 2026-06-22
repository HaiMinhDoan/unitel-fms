package com.unitel.fms.backend.mappers;

import com.unitel.fms.backend.dtos.request.TripIncidentRequest;
import com.unitel.fms.backend.dtos.response.TripIncidentResponse;
import com.unitel.fms.backend.entities.TripIncident;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TripIncidentMapper {

    @Mapping(target = "trip.id", source = "tripId")
    @Mapping(target = "reportedBy.id", source = "reportedBy")
    @Mapping(target = "resolvedBy.id", source = "resolvedBy")
    @Mapping(target = "metadata", ignore = true)
    TripIncident toEntity(TripIncidentRequest request);

    @Mapping(target = "tripId", source = "trip.id")
    @Mapping(target = "reportedBy", source = "reportedBy.id")
    @Mapping(target = "resolvedBy", source = "resolvedBy.id")
    @Mapping(target = "resolutionNotes", ignore = true)
    @Mapping(target = "severity", ignore = true)
    @Mapping(target = "reportedAt", source = "createdAt")
    TripIncidentResponse toResponse(TripIncident entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trip.id", source = "tripId")
    @Mapping(target = "reportedBy.id", source = "reportedBy")
    @Mapping(target = "resolvedBy.id", source = "resolvedBy")
    @Mapping(target = "metadata", ignore = true)
    void updateEntity(TripIncidentRequest request, @MappingTarget TripIncident entity);
}

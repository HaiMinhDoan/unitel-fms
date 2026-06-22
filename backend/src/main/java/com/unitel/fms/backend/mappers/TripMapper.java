package com.unitel.fms.backend.mappers;

import com.unitel.fms.backend.dtos.request.TripRequest;
import com.unitel.fms.backend.dtos.response.TripResponse;
import com.unitel.fms.backend.entities.Trip;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TripMapper {

    @Mapping(target = "dispatchAssignment.id", source = "dispatchAssignmentId")
    Trip toEntity(TripRequest request);

    @Mapping(target = "orgId", source = "dispatchAssignment.dispatchRequest.org.id")
    @Mapping(target = "dispatchAssignmentId", source = "dispatchAssignment.id")
    TripResponse toResponse(Trip entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dispatchAssignment.id", source = "dispatchAssignmentId")
    void updateEntity(TripRequest request, @MappingTarget Trip entity);
}

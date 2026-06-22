package com.unitel.fms.backend.mappers;

import com.unitel.fms.backend.dtos.request.TripStopRequest;
import com.unitel.fms.backend.dtos.response.TripStopResponse;
import com.unitel.fms.backend.entities.TripStop;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TripStopMapper {

    @Mapping(target = "trip.id", source = "tripId")
    TripStop toEntity(TripStopRequest request);

    @Mapping(target = "tripId", source = "trip.id")
    TripStopResponse toResponse(TripStop entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trip.id", source = "tripId")
    void updateEntity(TripStopRequest request, @MappingTarget TripStop entity);
}

package com.unitel.fms.backend.mappers;

import com.unitel.fms.backend.dtos.response.GeofenceDto;
import com.unitel.fms.backend.entities.Geofence;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface GeofenceMapper {
    @Mapping(target = "orgId", source = "org.id")
    GeofenceDto toDto(Geofence entity);

    List<GeofenceDto> toDtoList(List<Geofence> entities);
}

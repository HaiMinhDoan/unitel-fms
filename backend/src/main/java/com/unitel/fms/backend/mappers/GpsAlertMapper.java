package com.unitel.fms.backend.mappers;

import com.unitel.fms.backend.dtos.response.GpsAlertDto;
import com.unitel.fms.backend.entities.GpsAlert;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface GpsAlertMapper {

    @Mapping(target = "vehicleId", source = "vehicle.id")
    @Mapping(target = "tripId", source = "trip.id")
    @Mapping(target = "acknowledgedBy", source = "acknowledgedBy.id")
    GpsAlertDto toDto(GpsAlert entity);

    List<GpsAlertDto> toDtoList(List<GpsAlert> entities);
}

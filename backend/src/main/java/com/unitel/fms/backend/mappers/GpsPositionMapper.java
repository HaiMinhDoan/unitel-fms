package com.unitel.fms.backend.mappers;

import com.unitel.fms.backend.dtos.request.GpsIngestRequest;
import com.unitel.fms.backend.dtos.response.GpsPositionDto;
import com.unitel.fms.backend.entities.GpsPosition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface GpsPositionMapper {

    @Mapping(target = "id.vehicleId", source = "vehicleId")
    @Mapping(target = "id.recordedAt", source = "recordedAt")
    @Mapping(target = "vehicle.id", source = "vehicleId")
    @Mapping(target = "trip.id", source = "tripId")
    GpsPosition toEntity(GpsIngestRequest request);

    @Mapping(target = "vehicleId", source = "id.vehicleId")
    @Mapping(target = "recordedAt", source = "id.recordedAt")
    @Mapping(target = "tripId", source = "trip.id")
    GpsPositionDto toDto(GpsPosition entity);
}

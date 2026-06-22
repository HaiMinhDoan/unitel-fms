package com.unitel.fms.backend.mappers;

import com.unitel.fms.backend.dtos.request.VehicleHealthRequest;
import com.unitel.fms.backend.dtos.response.VehicleHealthResponse;
import com.unitel.fms.backend.entities.VehicleHealth;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface VehicleHealthMapper {

    @Mapping(target = "vehicle.id", source = "vehicleId")
    @Mapping(target = "assessedBy.id", source = "assessedById")
    VehicleHealth toEntity(VehicleHealthRequest request);

    @Mapping(target = "vehicleId", source = "vehicle.id")
    @Mapping(target = "assessedById", source = "assessedBy.id")
    VehicleHealthResponse toResponse(VehicleHealth entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehicle.id", source = "vehicleId")
    @Mapping(target = "assessedBy.id", source = "assessedById")
    void updateEntity(VehicleHealthRequest request, @MappingTarget VehicleHealth entity);
}

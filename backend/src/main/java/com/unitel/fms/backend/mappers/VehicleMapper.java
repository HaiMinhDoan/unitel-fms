package com.unitel.fms.backend.mappers;

import com.unitel.fms.backend.dtos.request.VehicleRequest;
import com.unitel.fms.backend.dtos.response.VehicleResponse;
import com.unitel.fms.backend.entities.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface VehicleMapper {

    @Mapping(target = "org.id", source = "orgId")
    @Mapping(target = "vehicleType.id", source = "vehicleTypeId")
    Vehicle toEntity(VehicleRequest request);

    @Mapping(target = "orgId", source = "org.id")
    @Mapping(target = "vehicleTypeId", source = "vehicleType.id")
    @Mapping(target = "vehicleTypeName", source = "vehicleType.name")
    VehicleResponse toResponse(Vehicle entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "org.id", source = "orgId")
    @Mapping(target = "vehicleType.id", source = "vehicleTypeId")
    void updateEntity(VehicleRequest request, @MappingTarget Vehicle entity);
}

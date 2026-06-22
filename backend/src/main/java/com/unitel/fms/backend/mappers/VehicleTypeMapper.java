package com.unitel.fms.backend.mappers;

import com.unitel.fms.backend.dtos.request.VehicleTypeRequest;
import com.unitel.fms.backend.dtos.response.VehicleTypeResponse;
import com.unitel.fms.backend.entities.VehicleType;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface VehicleTypeMapper {
    VehicleType toEntity(VehicleTypeRequest request);

    VehicleTypeResponse toResponse(VehicleType entity);

    @org.mapstruct.Mapping(target = "id", ignore = true)
    void updateEntity(VehicleTypeRequest request, @MappingTarget VehicleType entity);
}

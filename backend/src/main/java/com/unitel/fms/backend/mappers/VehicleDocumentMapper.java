package com.unitel.fms.backend.mappers;

import com.unitel.fms.backend.dtos.request.VehicleDocumentRequest;
import com.unitel.fms.backend.dtos.response.VehicleDocumentResponse;
import com.unitel.fms.backend.entities.VehicleDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface VehicleDocumentMapper {

    @Mapping(target = "vehicle.id", source = "vehicleId")
    VehicleDocument toEntity(VehicleDocumentRequest request);

    @Mapping(target = "vehicleId", source = "vehicle.id")
    VehicleDocumentResponse toResponse(VehicleDocument entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehicle.id", source = "vehicleId")
    void updateEntity(VehicleDocumentRequest request, @MappingTarget VehicleDocument entity);
}

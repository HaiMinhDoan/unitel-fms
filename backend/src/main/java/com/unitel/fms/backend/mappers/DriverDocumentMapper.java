package com.unitel.fms.backend.mappers;

import com.unitel.fms.backend.dtos.request.DriverDocumentRequest;
import com.unitel.fms.backend.dtos.response.DriverDocumentResponse;
import com.unitel.fms.backend.entities.DriverDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DriverDocumentMapper {

    @Mapping(target = "driver.id", source = "driverId")
    DriverDocument toEntity(DriverDocumentRequest request);

    @Mapping(target = "driverId", source = "driver.id")
    DriverDocumentResponse toResponse(DriverDocument entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "driver.id", source = "driverId")
    void updateEntity(DriverDocumentRequest request, @MappingTarget DriverDocument entity);
}

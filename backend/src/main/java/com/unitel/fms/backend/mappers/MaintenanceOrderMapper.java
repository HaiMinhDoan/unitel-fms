package com.unitel.fms.backend.mappers;

import com.unitel.fms.backend.dtos.request.MaintenanceOrderRequest;
import com.unitel.fms.backend.dtos.response.MaintenanceOrderResponse;
import com.unitel.fms.backend.entities.MaintenanceOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MaintenanceOrderMapper {

    @Mapping(target = "vehicle.id", source = "vehicleId")
    @Mapping(target = "createdBy.id", source = "createdById")
    MaintenanceOrder toEntity(MaintenanceOrderRequest request);

    @Mapping(target = "vehicleId", source = "vehicle.id")
    @Mapping(target = "createdById", source = "createdBy.id")
    MaintenanceOrderResponse toResponse(MaintenanceOrder entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehicle.id", source = "vehicleId")
    @Mapping(target = "createdBy.id", source = "createdById")
    void updateEntity(MaintenanceOrderRequest request, @MappingTarget MaintenanceOrder entity);
}

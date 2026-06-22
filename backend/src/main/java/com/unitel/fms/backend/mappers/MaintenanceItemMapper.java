package com.unitel.fms.backend.mappers;

import com.unitel.fms.backend.dtos.request.MaintenanceItemRequest;
import com.unitel.fms.backend.dtos.response.MaintenanceItemResponse;
import com.unitel.fms.backend.entities.MaintenanceItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MaintenanceItemMapper {

    @Mapping(target = "maintenanceOrder.id", source = "maintenanceOrderId")
    MaintenanceItem toEntity(MaintenanceItemRequest request);

    @Mapping(target = "maintenanceOrderId", source = "maintenanceOrder.id")
    MaintenanceItemResponse toResponse(MaintenanceItem entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "maintenanceOrder.id", source = "maintenanceOrderId")
    void updateEntity(MaintenanceItemRequest request, @MappingTarget MaintenanceItem entity);
}

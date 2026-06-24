package com.unitel.fms.backend.mappers;

import com.unitel.fms.backend.dtos.request.FuelLogRequest;
import com.unitel.fms.backend.dtos.response.FuelLogResponse;
import com.unitel.fms.backend.entities.FuelLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = org.mapstruct.NullValueCheckStrategy.ALWAYS)
public abstract class FuelLogMapper {

    @org.springframework.beans.factory.annotation.Autowired
    protected com.unitel.fms.backend.services.impl.entity.FileAttachmentService fileAttachmentService;

    @org.springframework.beans.factory.annotation.Autowired
    protected FileAttachmentMapper fileAttachmentMapper;

    @Mapping(target = "vehicle.id", source = "vehicleId")
    @Mapping(target = "litersAdded", source = "volumeLiters")
    @Mapping(target = "fuelCost", source = "totalCost")
    @Mapping(target = "odometerAtFill", source = "odometerReading")
    public abstract FuelLog toEntity(FuelLogRequest request);

    @Mapping(target = "vehicleId", source = "vehicle.id")
    @Mapping(target = "volumeLiters", source = "litersAdded")
    @Mapping(target = "totalCost", source = "fuelCost")
    @Mapping(target = "odometerReading", source = "odometerAtFill")
    public abstract FuelLogResponse toResponse(FuelLog entity);

    @org.mapstruct.AfterMapping
    protected void linkAttachments(FuelLog entity, @MappingTarget FuelLogResponse response) {
        if (entity.getId() != null && fileAttachmentService != null && fileAttachmentMapper != null) {
            java.util.List<com.unitel.fms.backend.entities.FileAttachment> files = fileAttachmentService.getByEntity("fuel_logs", entity.getId());
            response.setAttachments(files.stream().map(fileAttachmentMapper::toResponse).collect(java.util.stream.Collectors.toList()));
        }
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehicle.id", source = "vehicleId")
    @Mapping(target = "litersAdded", source = "volumeLiters")
    @Mapping(target = "fuelCost", source = "totalCost")
    @Mapping(target = "odometerAtFill", source = "odometerReading")
    public abstract void updateEntity(FuelLogRequest request, @MappingTarget FuelLog entity);
}

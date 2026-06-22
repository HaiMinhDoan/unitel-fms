package com.unitel.fms.backend.mappers;

import com.unitel.fms.backend.dtos.request.FuelLogRequest;
import com.unitel.fms.backend.dtos.response.FuelLogResponse;
import com.unitel.fms.backend.entities.FuelLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FuelLogMapper {

    @Mapping(target = "vehicle.id", source = "vehicleId")
    @Mapping(target = "litersAdded", source = "volumeLiters")
    @Mapping(target = "fuelCost", source = "totalCost")
    @Mapping(target = "odometerAtFill", source = "odometerReading")
    FuelLog toEntity(FuelLogRequest request);

    @Mapping(target = "vehicleId", source = "vehicle.id")
    @Mapping(target = "volumeLiters", source = "litersAdded")
    @Mapping(target = "totalCost", source = "fuelCost")
    @Mapping(target = "odometerReading", source = "odometerAtFill")
    FuelLogResponse toResponse(FuelLog entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehicle.id", source = "vehicleId")
    @Mapping(target = "litersAdded", source = "volumeLiters")
    @Mapping(target = "fuelCost", source = "totalCost")
    @Mapping(target = "odometerAtFill", source = "odometerReading")
    void updateEntity(FuelLogRequest request, @MappingTarget FuelLog entity);
}

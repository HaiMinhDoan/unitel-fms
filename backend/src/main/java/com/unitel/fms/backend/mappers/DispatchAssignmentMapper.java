package com.unitel.fms.backend.mappers;

import com.unitel.fms.backend.dtos.request.DispatchAssignmentRequest;
import com.unitel.fms.backend.dtos.response.DispatchAssignmentResponse;
import com.unitel.fms.backend.entities.DispatchAssignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DispatchAssignmentMapper {

    @Mapping(target = "dispatchRequest.id", source = "dispatchRequestId")
    @Mapping(target = "vehicle.id", source = "vehicleId")
    @Mapping(target = "driver.id", source = "driverId")
    @Mapping(target = "partnerVehicle.id", source = "partnerVehicleId")
    @Mapping(target = "partnerDriver.id", source = "partnerDriverId")
    @Mapping(target = "assignedBy.id", source = "assignedBy")
    @Mapping(target = "overrideBy.id", source = "overrideBy")
    DispatchAssignment toEntity(DispatchAssignmentRequest request);

    @Mapping(target = "orgId", source = "dispatchRequest.org.id")
    @Mapping(target = "dispatchRequestId", source = "dispatchRequest.id")
    @Mapping(target = "vehicleId", source = "vehicle.id")
    @Mapping(target = "driverId", source = "driver.id")
    @Mapping(target = "partnerVehicleId", source = "partnerVehicle.id")
    @Mapping(target = "partnerDriverId", source = "partnerDriver.id")
    @Mapping(target = "assignedBy", source = "assignedBy.id")
    @Mapping(target = "overrideBy", source = "overrideBy.id")
    DispatchAssignmentResponse toResponse(DispatchAssignment entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dispatchRequest.id", source = "dispatchRequestId")
    @Mapping(target = "vehicle.id", source = "vehicleId")
    @Mapping(target = "driver.id", source = "driverId")
    @Mapping(target = "partnerVehicle.id", source = "partnerVehicleId")
    @Mapping(target = "partnerDriver.id", source = "partnerDriverId")
    @Mapping(target = "assignedBy.id", source = "assignedBy")
    @Mapping(target = "overrideBy.id", source = "overrideBy")
    void updateEntity(DispatchAssignmentRequest request, @MappingTarget DispatchAssignment entity);
}

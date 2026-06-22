package com.unitel.fms.backend.mappers;

import com.unitel.fms.backend.dtos.request.DispatchRequestRequest;
import com.unitel.fms.backend.dtos.response.DispatchRequestResponse;
import com.unitel.fms.backend.entities.DispatchRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DispatchRequestMapper {

    @Mapping(target = "org.id", source = "orgId")
    @Mapping(target = "customer.id", source = "customerId")
    DispatchRequest toEntity(DispatchRequestRequest request);

    @Mapping(target = "orgId", source = "org.id")
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "createdBy", source = "createdBy.id")
    DispatchRequestResponse toResponse(DispatchRequest entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "org.id", source = "orgId")
    @Mapping(target = "customer.id", source = "customerId")
    void updateEntity(DispatchRequestRequest request, @MappingTarget DispatchRequest entity);
}

package com.unitel.fms.backend.mappers;

import com.unitel.fms.backend.dtos.request.CustomerRequest;
import com.unitel.fms.backend.dtos.response.CustomerResponse;
import com.unitel.fms.backend.entities.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CustomerMapper {

    @Mapping(target = "org.id", source = "orgId")
    Customer toEntity(CustomerRequest request);

    @Mapping(target = "orgId", source = "org.id")
    CustomerResponse toResponse(Customer entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "org.id", source = "orgId")
    void updateEntity(CustomerRequest request, @MappingTarget Customer entity);
}

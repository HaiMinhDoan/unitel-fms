package com.unitel.fms.backend.mappers;

import com.unitel.fms.backend.dtos.request.DriverRequest;
import com.unitel.fms.backend.dtos.response.DriverResponse;
import com.unitel.fms.backend.entities.Driver;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DriverMapper {

    @Mapping(target = "org.id", source = "orgId")
    @Mapping(target = "user.id", source = "userId")
    Driver toEntity(DriverRequest request);

    @Mapping(target = "orgId", source = "org.id")
    @Mapping(target = "userId", source = "user.id")
    DriverResponse toResponse(Driver entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "org.id", source = "orgId")
    @Mapping(target = "user.id", source = "userId")
    void updateEntity(DriverRequest request, @MappingTarget Driver entity);
}

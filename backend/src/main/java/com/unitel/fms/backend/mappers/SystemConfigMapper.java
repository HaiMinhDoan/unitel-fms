package com.unitel.fms.backend.mappers;

import com.unitel.fms.backend.dtos.request.SystemConfigRequest;
import com.unitel.fms.backend.dtos.response.SystemConfigResponse;
import com.unitel.fms.backend.entities.SystemConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SystemConfigMapper {
    SystemConfig toEntity(SystemConfigRequest request);
    
    @Mapping(target = "updatedById", source = "updatedBy.id")
    SystemConfigResponse toResponse(SystemConfig entity);
    
    @Mapping(target = "id", ignore = true)
    void updateEntity(SystemConfigRequest request, @MappingTarget SystemConfig entity);
}

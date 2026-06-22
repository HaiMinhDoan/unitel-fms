package com.unitel.fms.backend.mappers;

import com.unitel.fms.backend.dtos.request.RoleRequest;
import com.unitel.fms.backend.dtos.response.RoleResponse;
import com.unitel.fms.backend.entities.Role;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoleMapper {
    Role toEntity(RoleRequest request);
    RoleResponse toResponse(Role entity);
    void updateEntity(RoleRequest request, @MappingTarget Role entity);
}

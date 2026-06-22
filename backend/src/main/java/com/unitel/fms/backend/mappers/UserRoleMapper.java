package com.unitel.fms.backend.mappers;

import com.unitel.fms.backend.dtos.request.UserRoleRequest;
import com.unitel.fms.backend.dtos.response.UserRoleResponse;
import com.unitel.fms.backend.entities.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserRoleMapper {

    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "role.id", source = "roleId")
    @Mapping(target = "org.id", source = "orgId")
    UserRole toEntity(UserRoleRequest request);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "roleId", source = "role.id")
    @Mapping(target = "orgId", source = "org.id")
    @Mapping(target = "grantedById", source = "grantedBy.id")
    UserRoleResponse toResponse(UserRole entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "role.id", source = "roleId")
    @Mapping(target = "org.id", source = "orgId")
    void updateEntity(UserRoleRequest request, @MappingTarget UserRole entity);
}

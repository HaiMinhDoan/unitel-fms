package com.unitel.fms.backend.mappers;

import com.unitel.fms.backend.dtos.request.UserRequest;
import com.unitel.fms.backend.dtos.response.UserResponse;
import com.unitel.fms.backend.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @Mapping(target = "org.id", source = "orgId")
    User toEntity(UserRequest request);

    @Mapping(target = "orgId", source = "org.id")
    UserResponse toResponse(User entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "org.id", source = "orgId")
    void updateEntity(UserRequest request, @MappingTarget User entity);
}

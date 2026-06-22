package com.unitel.fms.backend.mappers;

import com.unitel.fms.backend.dtos.request.OrganizationRequest;
import com.unitel.fms.backend.dtos.response.OrganizationResponse;
import com.unitel.fms.backend.entities.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrganizationMapper {

    @Mapping(target = "parent.id", source = "parentId")
    Organization toEntity(OrganizationRequest request);

    @Mapping(target = "parentId", source = "parent.id")
    OrganizationResponse toResponse(Organization entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parent.id", source = "parentId")
    void updateEntity(OrganizationRequest request, @MappingTarget Organization entity);
}

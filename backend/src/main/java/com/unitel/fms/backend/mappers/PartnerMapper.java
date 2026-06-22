package com.unitel.fms.backend.mappers;

import com.unitel.fms.backend.dtos.request.PartnerRequest;
import com.unitel.fms.backend.dtos.response.PartnerResponse;
import com.unitel.fms.backend.entities.Partner;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PartnerMapper {

    @Mapping(target = "orgId", source = "org.id")
    PartnerResponse toResponse(Partner entity);

    @Mapping(target = "org", ignore = true)
    Partner toEntity(PartnerRequest request);

    @Mapping(target = "org", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(PartnerRequest request, @MappingTarget Partner entity);
}

package com.unitel.fms.backend.mappers;

import com.unitel.fms.backend.dtos.request.PartnerDriverRequest;
import com.unitel.fms.backend.dtos.response.PartnerDriverResponse;
import com.unitel.fms.backend.entities.PartnerDriver;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PartnerDriverMapper {

    @Mapping(target = "partnerId", source = "partner.id")
    PartnerDriverResponse toResponse(PartnerDriver entity);

    @Mapping(target = "partner", ignore = true)
    PartnerDriver toEntity(PartnerDriverRequest request);

    @Mapping(target = "partner", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(PartnerDriverRequest request, @MappingTarget PartnerDriver entity);
}

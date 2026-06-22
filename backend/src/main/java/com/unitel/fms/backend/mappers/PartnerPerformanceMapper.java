package com.unitel.fms.backend.mappers;

import com.unitel.fms.backend.dtos.request.PartnerPerformanceRequest;
import com.unitel.fms.backend.dtos.response.PartnerPerformanceResponse;
import com.unitel.fms.backend.entities.PartnerPerformance;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PartnerPerformanceMapper {

    @Mapping(target = "partnerId", source = "partner.id")
    PartnerPerformanceResponse toResponse(PartnerPerformance entity);

    @Mapping(target = "partner", ignore = true)
    PartnerPerformance toEntity(PartnerPerformanceRequest request);

    @Mapping(target = "partner", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(PartnerPerformanceRequest request, @MappingTarget PartnerPerformance entity);
}

package com.unitel.fms.backend.mappers;

import com.unitel.fms.backend.dtos.request.PartnerVehicleRequest;
import com.unitel.fms.backend.dtos.response.PartnerVehicleResponse;
import com.unitel.fms.backend.entities.PartnerVehicle;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PartnerVehicleMapper {

    @Mapping(target = "partnerId", source = "partner.id")
    @Mapping(target = "vehicleTypeId", source = "vehicleType.id")
    PartnerVehicleResponse toResponse(PartnerVehicle entity);

    @Mapping(target = "partner", ignore = true)
    @Mapping(target = "vehicleType", ignore = true)
    PartnerVehicle toEntity(PartnerVehicleRequest request);

    @Mapping(target = "partner", ignore = true)
    @Mapping(target = "vehicleType", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(PartnerVehicleRequest request, @MappingTarget PartnerVehicle entity);
}

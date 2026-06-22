package com.unitel.fms.backend.mappers;

import com.unitel.fms.backend.dtos.response.AuditLogResponse;
import com.unitel.fms.backend.entities.AuditLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AuditLogMapper {
    @Mapping(target = "userId", source = "user.id")
    AuditLogResponse toResponse(AuditLog entity);
}

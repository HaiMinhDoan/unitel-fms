package com.unitel.fms.backend.mappers;

import com.unitel.fms.backend.dtos.request.NotificationRequest;
import com.unitel.fms.backend.dtos.response.NotificationResponse;
import com.unitel.fms.backend.entities.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NotificationMapper {

    @Mapping(target = "user.id", source = "userId")
    Notification toEntity(NotificationRequest request);

    @Mapping(target = "userId", source = "user.id")
    NotificationResponse toResponse(Notification entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user.id", source = "userId")
    void updateEntity(NotificationRequest request, @MappingTarget Notification entity);
}

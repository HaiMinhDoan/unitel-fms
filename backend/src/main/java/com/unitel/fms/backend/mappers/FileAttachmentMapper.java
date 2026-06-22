package com.unitel.fms.backend.mappers;

import com.unitel.fms.backend.dtos.response.FileAttachmentResponse;
import com.unitel.fms.backend.entities.FileAttachment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FileAttachmentMapper {

    @Mapping(target = "publicUrl", expression = "java(entity.getPublicUrl())")
    FileAttachmentResponse toResponse(FileAttachment entity);

    List<FileAttachmentResponse> toResponseList(List<FileAttachment> entities);
}

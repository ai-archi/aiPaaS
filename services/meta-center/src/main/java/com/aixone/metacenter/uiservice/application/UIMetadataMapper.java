package com.aixone.metacenter.uiservice.application;

import com.aixone.metacenter.uiservice.application.dto.UIMetadataDTO;
import com.aixone.metacenter.uiservice.domain.UIMetadata;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UIMetadataMapper {
    UIMetadataDTO toDTO(UIMetadata uiMetadata);
    UIMetadata toEntity(UIMetadataDTO uiMetadataDTO);
    void updateEntityFromDTO(UIMetadataDTO uiMetadataDTO, @MappingTarget UIMetadata uiMetadata);
}

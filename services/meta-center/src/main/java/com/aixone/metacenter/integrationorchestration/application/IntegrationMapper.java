package com.aixone.metacenter.integrationorchestration.application;

import com.aixone.metacenter.integrationorchestration.application.dto.IntegrationDTO;
import com.aixone.metacenter.integrationorchestration.domain.Integration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface IntegrationMapper {
    IntegrationDTO toDTO(Integration integration);
    Integration toEntity(IntegrationDTO integrationDTO);
    void updateEntityFromDTO(IntegrationDTO integrationDTO, @MappingTarget Integration integration);
}

package com.aixone.directory.organization.application;

import org.mapstruct.Mapper;

import com.aixone.directory.organization.application.dto.PositionDto;
import com.aixone.directory.organization.domain.aggregate.Position;

@Mapper(componentModel = "spring")
public interface PositionDtoMapper {
    PositionDto toDto(Position position);
} 
package com.aixone.directory.organization.infrastructure.persistence;

import org.mapstruct.Mapper;

import com.aixone.directory.organization.application.dto.PositionDto;
import com.aixone.directory.organization.domain.aggregate.Position;
import com.aixone.directory.organization.infrastructure.persistence.dbo.PositionDbo;

@Mapper(componentModel = "spring")
public interface PositionMapper {

    PositionDto toDto(Position position);

    Position toDomain(PositionDbo dbo);

    PositionDbo toDbo(Position domain);
} 
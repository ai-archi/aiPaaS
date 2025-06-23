package com.aixone.directory.organization.infrastructure.persistence;

import com.aixone.directory.organization.domain.aggregate.Organization;
import com.aixone.directory.organization.infrastructure.persistence.dbo.OrganizationDbo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {

    @Mapping(target = "departments", ignore = true)
    @Mapping(target = "positions", ignore = true)
    Organization toDomain(OrganizationDbo dbo);

    OrganizationDbo toDbo(Organization domain);
} 
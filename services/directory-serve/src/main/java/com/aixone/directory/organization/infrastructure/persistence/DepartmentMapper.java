package com.aixone.directory.organization.infrastructure.persistence;

import org.mapstruct.Mapper;

import com.aixone.directory.organization.application.dto.DepartmentDto;
import com.aixone.directory.organization.domain.aggregate.Department;
import com.aixone.directory.organization.infrastructure.persistence.dbo.DepartmentDbo;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    DepartmentDto toDto(Department department);

    Department toDomain(DepartmentDbo dbo);

    DepartmentDbo toDbo(Department domain);
} 
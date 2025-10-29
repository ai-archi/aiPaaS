package com.aixone.directory.organization.application;

import org.mapstruct.Mapper;

import com.aixone.directory.organization.application.dto.DepartmentDto;
import com.aixone.directory.organization.domain.aggregate.Department;

@Mapper(componentModel = "spring")
public interface DepartmentDtoMapper {
    DepartmentDto toDto(Department department);
} 
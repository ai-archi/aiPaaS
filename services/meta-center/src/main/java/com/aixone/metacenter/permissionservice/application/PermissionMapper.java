package com.aixone.metacenter.permissionservice.application;

import com.aixone.metacenter.permissionservice.application.dto.PermissionDTO;
import com.aixone.metacenter.permissionservice.domain.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    PermissionDTO toDTO(Permission permission);
    Permission toEntity(PermissionDTO permissionDTO);
    void updateEntityFromDTO(PermissionDTO permissionDTO, @MappingTarget Permission permission);
}

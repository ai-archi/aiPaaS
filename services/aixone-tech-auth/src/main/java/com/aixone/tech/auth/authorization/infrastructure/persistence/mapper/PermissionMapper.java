package com.aixone.tech.auth.authorization.infrastructure.persistence.mapper;

import com.aixone.tech.auth.authorization.domain.model.Permission;
import com.aixone.tech.auth.authorization.infrastructure.persistence.entity.PermissionEntity;
import org.mapstruct.Mapper;

/**
 * Permission 映射器
 */
@Mapper(componentModel = "spring")
public interface PermissionMapper {
    
    PermissionEntity toEntity(Permission permission);
    
    Permission toDomain(PermissionEntity entity);
}

package com.aixone.tech.auth.authorization.infrastructure.persistence.mapper;

import com.aixone.tech.auth.authorization.domain.model.Role;
import com.aixone.tech.auth.authorization.infrastructure.persistence.entity.RoleEntity;
import org.mapstruct.Mapper;

/**
 * Role 映射器
 */
@Mapper(componentModel = "spring")
public interface RoleMapper {
    
    RoleEntity toEntity(Role role);
    
    Role toDomain(RoleEntity entity);
}

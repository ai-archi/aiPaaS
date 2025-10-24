package com.aixone.tech.auth.authorization.infrastructure.persistence.mapper;

import com.aixone.tech.auth.authorization.domain.model.UserRole;
import com.aixone.tech.auth.authorization.infrastructure.persistence.entity.UserRoleEntity;
import org.mapstruct.Mapper;

/**
 * UserRole 映射器
 */
@Mapper(componentModel = "spring")
public interface UserRoleMapper {
    
    UserRoleEntity toEntity(UserRole userRole);
    
    UserRole toDomain(UserRoleEntity entity);
}

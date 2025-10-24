package com.aixone.tech.auth.authentication.infrastructure.persistence.mapper;

import com.aixone.tech.auth.authentication.domain.model.TokenBlacklist;
import com.aixone.tech.auth.authentication.infrastructure.persistence.entity.TokenBlacklistEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * TokenBlacklist 映射器
 */
@Mapper(componentModel = "spring")
public interface TokenBlacklistMapper {
    
    @Mapping(target = "id", ignore = true)
    TokenBlacklistEntity toEntity(TokenBlacklist tokenBlacklist);
    
    TokenBlacklist toDomain(TokenBlacklistEntity entity);
}

package com.aixone.tech.auth.authentication.infrastructure.persistence.mapper;

import com.aixone.tech.auth.authentication.domain.model.Token;
import com.aixone.tech.auth.authentication.infrastructure.persistence.entity.TokenEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TokenMapper {

    TokenMapper INSTANCE = Mappers.getMapper(TokenMapper.class);

    @Mapping(source = "token", target = "token")
    @Mapping(source = "tenantId", target = "tenantId")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "clientId", target = "clientId")
    @Mapping(source = "expiresAt", target = "expiresAt")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "createdAt", target = "createdAt")
    TokenEntity toEntity(Token token);

    @Mapping(source = "token", target = "token")
    @Mapping(source = "tenantId", target = "tenantId")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "clientId", target = "clientId")
    @Mapping(source = "expiresAt", target = "expiresAt")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "createdAt", target = "createdAt")
    Token toDomain(TokenEntity tokenEntity);
}
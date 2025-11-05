package com.aixone.tech.auth.authentication.infrastructure.persistence.mapper;

import com.aixone.tech.auth.authentication.domain.model.Token;
import com.aixone.tech.auth.authentication.infrastructure.persistence.entity.TokenEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TokenMapper {

    TokenMapper INSTANCE = Mappers.getMapper(TokenMapper.class);

    @Mapping(target = "id", ignore = true) // Entity的id由数据库自动生成，忽略映射
    @Mapping(source = "token", target = "token")
    @Mapping(source = "tenantId", target = "tenantId")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "clientId", target = "clientId")
    @Mapping(source = "expiresAt", target = "expiresAt")
    @Mapping(source = "type", target = "type", qualifiedByName = "tokenTypeToString")
    @Mapping(source = "createdAt", target = "createdAt")
    TokenEntity toEntity(Token token);

    @Mapping(source = "token", target = "token")
    @Mapping(source = "tenantId", target = "tenantId")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "clientId", target = "clientId")
    @Mapping(source = "expiresAt", target = "expiresAt")
    @Mapping(source = "type", target = "type", qualifiedByName = "stringToTokenType")
    @Mapping(source = "createdAt", target = "createdAt")
    Token toDomain(TokenEntity tokenEntity);

    /**
     * 将TokenType枚举转换为String
     */
    @Named("tokenTypeToString")
    default String tokenTypeToString(Token.TokenType type) {
        return type != null ? type.name() : null;
    }

    /**
     * 将String转换为TokenType枚举
     */
    @Named("stringToTokenType")
    default Token.TokenType stringToTokenType(String type) {
        if (type == null || type.isEmpty()) {
            return null;
        }
        try {
            return Token.TokenType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
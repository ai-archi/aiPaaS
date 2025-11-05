package com.aixone.tech.auth.authentication.infrastructure.persistence.mapper;

import com.aixone.tech.auth.authentication.domain.model.Client;
import com.aixone.tech.auth.authentication.infrastructure.persistence.entity.ClientEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    ClientMapper INSTANCE = Mappers.getMapper(ClientMapper.class);

    @Mapping(target = "id", ignore = true) // Entity的id由数据库自动生成，忽略映射
    ClientEntity toEntity(Client client);

    // Domain模型没有id字段，直接映射clientId
    Client toDomain(ClientEntity clientEntity);
}
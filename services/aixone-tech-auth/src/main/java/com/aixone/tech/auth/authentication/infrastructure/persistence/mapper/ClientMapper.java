package com.aixone.tech.auth.authentication.infrastructure.persistence.mapper;

import com.aixone.tech.auth.authentication.domain.model.Client;
import com.aixone.tech.auth.authentication.infrastructure.persistence.entity.ClientEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    ClientMapper INSTANCE = Mappers.getMapper(ClientMapper.class);

    @Mapping(source = "clientId", target = "id")
    ClientEntity toEntity(Client client);

    @Mapping(source = "id", target = "clientId")
    Client toDomain(ClientEntity clientEntity);
}
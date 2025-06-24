package com.aixone.directory.user.application;

import com.aixone.directory.user.domain.aggregate.User;
import com.aixone.directory.user.application.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserDtoMapper {

    UserDtoMapper INSTANCE = Mappers.getMapper(UserDtoMapper.class);

    @Mapping(source = "profile.username", target = "username")
    @Mapping(source = "profile.avatarUrl", target = "avatarUrl")
    @Mapping(source = "profile.bio", target = "bio")
    UserDto toDto(User user);
} 
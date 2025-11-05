package com.aixone.tech.auth.authentication.infrastructure.persistence.mapper;

import com.aixone.tech.auth.authentication.domain.model.User;
import com.aixone.tech.auth.authentication.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

/**
 * 用户映射器
 */
@Component
public class UserMapper {
    
    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        
        User user = new User();
        user.setId(entity.getId());
        user.setUsername(entity.getUsername());
        user.setHashedPassword(entity.getHashedPassword());
        user.setEmail(entity.getEmail());
        user.setAvatarUrl(entity.getAvatarUrl());
        user.setBio(entity.getBio());
        user.setStatus(entity.getStatus());
        user.setTenantId(entity.getTenantId());
        user.setCreatedAt(entity.getCreatedAt());
        user.setUpdatedAt(entity.getUpdatedAt());
        
        return user;
    }
    
    public UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }
        
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setUsername(user.getUsername());
        entity.setHashedPassword(user.getHashedPassword());
        entity.setEmail(user.getEmail());
        entity.setAvatarUrl(user.getAvatarUrl());
        entity.setBio(user.getBio());
        entity.setStatus(user.getStatus());
        entity.setTenantId(user.getTenantId());
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());
        
        return entity;
    }
}

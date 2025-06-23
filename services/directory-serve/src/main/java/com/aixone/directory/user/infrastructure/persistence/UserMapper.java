package com.aixone.directory.user.infrastructure.persistence;

import org.springframework.stereotype.Component;

import com.aixone.directory.user.domain.aggregate.Profile;
import com.aixone.directory.user.domain.aggregate.User;
import com.aixone.directory.user.infrastructure.persistence.dbo.UserDbo;

@Component
public class UserMapper {

    public User toDomain(UserDbo dbo) {
        if (dbo == null) {
            return null;
        }
        Profile profile = new Profile(dbo.getUsername(), dbo.getAvatarUrl(), dbo.getBio());

        return new User(
            dbo.getId(),
            dbo.getTenantId(),
            dbo.getEmail(),
            dbo.getHashedPassword(),
            profile,
            dbo.getStatus(),
            dbo.getCreatedAt(),
            dbo.getUpdatedAt()
        );
    }

    public UserDbo toDbo(User domain) {
        if (domain == null) {
            return null;
        }
        UserDbo dbo = new UserDbo();
        dbo.setId(domain.getId());
        dbo.setTenantId(domain.getTenantId());
        dbo.setEmail(domain.getEmail());
        dbo.setHashedPassword(domain.getHashedPassword());
        dbo.setStatus(domain.getStatus());
        dbo.setCreatedAt(domain.getCreatedAt());
        dbo.setUpdatedAt(domain.getUpdatedAt());

        if (domain.getProfile() != null) {
            dbo.setUsername(domain.getProfile().getUsername());
            dbo.setAvatarUrl(domain.getProfile().getAvatarUrl());
            dbo.setBio(domain.getProfile().getBio());
        }
        return dbo;
    }
} 
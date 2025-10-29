package com.aixone.directory.user.infrastructure.persistence;

import org.springframework.stereotype.Component;

import com.aixone.directory.user.domain.aggregate.Profile;
import com.aixone.directory.user.domain.aggregate.User;
import com.aixone.directory.user.infrastructure.persistence.dbo.UserDbo;

import java.util.HashSet;

@Component
public class UserMapper {

    public User toDomain(UserDbo dbo) {
        if (dbo == null) {
            return null;
        }
        Profile profile = new Profile(dbo.getUsername(), dbo.getAvatarUrl(), dbo.getBio());

        // Note: roleIds and groupIds are not stored in UserDbo, they are managed
        // in their respective tables (user_roles, group_members).
        // They should be populated by the application service if needed.
        return new User(
            dbo.getId(),
            dbo.getTenantId(),
            dbo.getEmail(),
            dbo.getHashedPassword(),
            profile,
            dbo.getStatus(),
            new HashSet<>(), // roleIds
            new HashSet<>(), // groupIds
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
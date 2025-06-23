package com.aixone.directory.role.infrastructure.persistence;

import java.util.HashSet;

import org.springframework.stereotype.Component;

import com.aixone.directory.role.domain.aggregate.Role;
import com.aixone.directory.role.infrastructure.persistence.dbo.RoleDbo;

@Component
public class RoleMapper {

    public Role toDomain(RoleDbo dbo) {
        if (dbo == null) return null;
        return Role.builder()
                .id(dbo.getId())
                .tenantId(dbo.getTenantId())
                .name(dbo.getName())
                .members(new HashSet<>(dbo.getMembers())) // Create a mutable copy
                .createdAt(dbo.getCreatedAt())
                .updatedAt(dbo.getUpdatedAt())
                .build();
    }

    public RoleDbo toDbo(Role domain) {
        if (domain == null) return null;
        RoleDbo dbo = new RoleDbo();
        dbo.setId(domain.getId());
        dbo.setTenantId(domain.getTenantId());
        dbo.setName(domain.getName());
        dbo.setMembers(new HashSet<>(domain.getMembers())); // Create a mutable copy
        dbo.setCreatedAt(domain.getCreatedAt());
        dbo.setUpdatedAt(domain.getUpdatedAt());
        return dbo;
    }
} 
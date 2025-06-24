package com.aixone.directory.role.infrastructure.persistence;

import java.util.HashSet;

import org.springframework.stereotype.Component;

import com.aixone.directory.role.domain.aggregate.Role;
import com.aixone.directory.role.infrastructure.persistence.dbo.RoleDbo;

@Component
public class RoleMapper {

    public Role toDomain(RoleDbo dbo) {
        if (dbo == null) return null;
        // The 'members' are part of the DBO's state but not directly part of the
        // core Role domain object's construction. The domain object is simpler.
        return new Role(
                dbo.getId(),
                dbo.getTenantId(),
                dbo.getName(),
                dbo.getCreatedAt(),
                dbo.getUpdatedAt()
        );
    }

    public RoleDbo toDbo(Role domain) {
        if (domain == null) return null;
        RoleDbo dbo = new RoleDbo();
        dbo.setId(domain.getId());
        dbo.setTenantId(domain.getTenantId());
        dbo.setName(domain.getName());
        // 'members' are managed at the persistence level, so we don't map them from the domain object.
        // The Dbo is typically loaded, modified, and then saved.
        // If creating a new RoleDbo, the members set would be initialized as empty.
        dbo.setMembers(new HashSet<>());
        dbo.setCreatedAt(domain.getCreatedAt());
        dbo.setUpdatedAt(domain.getUpdatedAt());
        return dbo;
    }
} 
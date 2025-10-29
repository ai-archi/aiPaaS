package com.aixone.directory.tenant.infrastructure.persistence;

import com.aixone.directory.tenant.domain.aggregate.Tenant;
import com.aixone.directory.tenant.infrastructure.persistence.dbo.TenantDbo;
import org.springframework.stereotype.Component;

@Component
public class TenantMapper {

    public Tenant toDomain(TenantDbo dbo) {
        if (dbo == null) {
            return null;
        }
        // This reflective instantiation is not ideal, but we made the constructor public for this purpose.
        return new Tenant(
                dbo.getId(),
                dbo.getName(),
                dbo.getGroupId(),
                dbo.getStatus(),
                dbo.getCreatedAt(),
                dbo.getUpdatedAt()
        );
    }

    public TenantDbo toDbo(Tenant domain) {
        if (domain == null) {
            return null;
        }
        TenantDbo dbo = new TenantDbo();
        dbo.setId(domain.getId());
        dbo.setName(domain.getName());
        dbo.setGroupId(domain.getGroupId());
        dbo.setStatus(domain.getStatus());
        dbo.setCreatedAt(domain.getCreatedAt());
        dbo.setUpdatedAt(domain.getUpdatedAt());
        return dbo;
    }
} 
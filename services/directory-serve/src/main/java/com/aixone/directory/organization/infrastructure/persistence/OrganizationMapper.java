package com.aixone.directory.organization.infrastructure.persistence;

import org.springframework.stereotype.Component;
import com.aixone.directory.organization.domain.aggregate.Organization;
import com.aixone.directory.organization.infrastructure.persistence.dbo.OrganizationDbo;

@Component
public class OrganizationMapper {

    /**
     * 只映射简单字段（id/tenantId/name/createdAt/updatedAt），集合字段（departments/positions）需由Service层手动set。
     */
    public Organization toDomainSimple(OrganizationDbo dbo) {
        if (dbo == null) return null;
        return Organization.builder()
                .id(dbo.getId())
                .tenantId(dbo.getTenantId())
                .name(dbo.getName())
                .createdAt(dbo.getCreatedAt())
                .updatedAt(dbo.getUpdatedAt())
                .build();
    }

    public OrganizationDbo toDboSimple(Organization domain) {
        if (domain == null) return null;
        OrganizationDbo dbo = new OrganizationDbo();
        dbo.setId(domain.getId());
        dbo.setTenantId(domain.getTenantId());
        dbo.setName(domain.getName());
        dbo.setCreatedAt(domain.getCreatedAt());
        dbo.setUpdatedAt(domain.getUpdatedAt());
        return dbo;
    }
} 
package com.aixone.directory.group.infrastructure.persistence;

import com.aixone.directory.group.domain.aggregate.Group;
import com.aixone.directory.group.infrastructure.persistence.dbo.GroupDbo;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class GroupMapper {
/*
    public Group toDomain(GroupDbo dbo) {
        if (dbo == null) return null;
        return Group.builder()
                .id(dbo.getId())
                .tenantId(dbo.getTenantId())
                .name(dbo.getName())
                .members(new HashSet<>(dbo.getMembers())) // Create a mutable copy
                .createdAt(dbo.getCreatedAt())
                .updatedAt(dbo.getUpdatedAt())
                .build();
    }

    public GroupDbo toDbo(Group domain) {
        if (domain == null) return null;
        GroupDbo dbo = new GroupDbo();
        dbo.setId(domain.getId());
        dbo.setTenantId(domain.getTenantId());
        dbo.setName(domain.getName());
        dbo.setMembers(new HashSet<>(domain.getMembers()));
        dbo.setCreatedAt(domain.getCreatedAt());
        dbo.setUpdatedAt(domain.getUpdatedAt());
        return dbo;
    }
    */
} 
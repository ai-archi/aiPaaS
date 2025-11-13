package com.aixone.directory.group.infrastructure.persistence;

import com.aixone.directory.group.domain.aggregate.Group;
import com.aixone.directory.group.infrastructure.persistence.dbo.GroupDbo;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.stream.Collectors;

@Component
public class GroupMapper {
    public Group toDomain(GroupDbo dbo) {
        if (dbo == null) return null;
        // 从 users 关联中提取成员ID
        HashSet<String> memberIds = dbo.getUsers() != null 
            ? dbo.getUsers().stream()
                .map(user -> user.getId())
                .collect(Collectors.toCollection(HashSet::new))
            : new HashSet<>();
        
        // 从 roles 关联中提取角色ID
        HashSet<String> roleIds = dbo.getRoles() != null
            ? dbo.getRoles().stream()
                .map(role -> role.getId())
                .collect(Collectors.toCollection(HashSet::new))
            : new HashSet<>();
        
        return Group.builder()
                .id(dbo.getId())
                .tenantId(dbo.getTenantId())
                .name(dbo.getName())
                .members(memberIds)
                .roles(roleIds)
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
        // members 和 roles 通过关联表管理，不需要在这里设置
        dbo.setCreatedAt(domain.getCreatedAt());
        dbo.setUpdatedAt(domain.getUpdatedAt());
        return dbo;
    }
} 
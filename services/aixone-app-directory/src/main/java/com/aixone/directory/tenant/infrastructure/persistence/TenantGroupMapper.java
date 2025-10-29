package com.aixone.directory.tenant.infrastructure.persistence;

import com.aixone.directory.tenant.domain.aggregate.TenantGroup;
import com.aixone.directory.tenant.infrastructure.persistence.dbo.TenantGroupEntity;
import org.springframework.stereotype.Component;

/**
 * 租户组实体映射器
 */
@Component
public class TenantGroupMapper {

    /**
     * 领域模型转实体
     */
    public TenantGroupEntity toEntity(TenantGroup tenantGroup) {
        if (tenantGroup == null) return null;
        
        return TenantGroupEntity.builder()
                .id(tenantGroup.getId())
                .name(tenantGroup.getName())
                .description(tenantGroup.getDescription())
                .parentId(tenantGroup.getParentId())
                .sortOrder(tenantGroup.getSortOrder())
                .status(tenantGroup.getStatus())
                .createdAt(tenantGroup.getCreatedAt())
                .updatedAt(tenantGroup.getUpdatedAt())
                .build();
    }

    /**
     * 实体转领域模型
     */
    public TenantGroup toDomain(TenantGroupEntity entity) {
        if (entity == null) return null;
        
        return TenantGroup.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .parentId(entity.getParentId())
                .sortOrder(entity.getSortOrder())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}


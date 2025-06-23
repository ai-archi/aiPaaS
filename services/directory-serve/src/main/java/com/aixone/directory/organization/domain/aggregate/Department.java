package com.aixone.directory.organization.domain.aggregate;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

/**
 * 部门实体
 */
@Getter
@Builder
public class Department {

    private final UUID id;
    private final UUID tenantId;
    private final UUID orgId;
    private String name;
    private UUID parentId; // 上级部门ID，可为空
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Department create(UUID tenantId, UUID orgId, String name, UUID parentId) {
        return Department.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .orgId(orgId)
                .name(name)
                .parentId(parentId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public void updateName(String newName) {
        // Add validation for newName if necessary
        this.name = newName;
    }

    public void changeParent(UUID newParentId) {
        // Business logic for moving a department
        this.parentId = newParentId;
    }
} 
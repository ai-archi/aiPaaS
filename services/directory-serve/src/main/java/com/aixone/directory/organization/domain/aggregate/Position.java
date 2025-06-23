package com.aixone.directory.organization.domain.aggregate;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

/**
 * 岗位实体
 */
@Getter
@Builder
public class Position {

    private final UUID id;
    private final UUID tenantId;
    private final UUID orgId;
    private String name;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Position create(UUID tenantId, UUID orgId, String name) {
        return Position.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .orgId(orgId)
                .name(name)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public void updateName(String newName) {
        // Add validation for newName if necessary
        this.name = newName;
    }
} 
package com.aixone.directory.organization.domain.aggregate;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 岗位实体
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Position {

    private String id;
    private String tenantId;
    private String orgId;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Builder.Default
    private Set<String> users = new HashSet<>();

    public static Position create(String tenantId, String orgId, String name) {
        return Position.builder()
                .id(java.util.UUID.randomUUID().toString())
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

    public void addUser(String userId) {
        Objects.requireNonNull(userId, "userId不能为空");
        this.users.add(userId);
        this.updatedAt = LocalDateTime.now();
    }

    public void removeUser(String userId) {
        Objects.requireNonNull(userId, "userId不能为空");
        this.users.remove(userId);
        this.updatedAt = LocalDateTime.now();
    }
} 
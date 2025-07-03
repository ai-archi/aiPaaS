package com.aixone.directory.role.domain.aggregate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Role {

    private String id;
    private String tenantId;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Getter
    private Set<String> users = new HashSet<>();
    @Getter
    private Set<String> groups = new HashSet<>();

    // Constructor for creating a new role
    public Role(String tenantId, String name) {
        this.id = java.util.UUID.randomUUID().toString();
        this.tenantId = tenantId;
        this.name = name;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor for reconstructing from persistence
    public Role(String id, String tenantId, String name, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Role create(String tenantId, String name) {
        return new Role(tenantId, name);
    }

    public void updateName(String newName) {
        this.name = newName;
        this.updatedAt = LocalDateTime.now();
    }

    public void grantUser(String userId) {
        Objects.requireNonNull(userId, "userId不能为空");
        this.users.add(userId);
        this.updatedAt = LocalDateTime.now();
    }

    public void revokeUser(String userId) {
        Objects.requireNonNull(userId, "userId不能为空");
        this.users.remove(userId);
        this.updatedAt = LocalDateTime.now();
    }

    public void grantGroup(String groupId) {
        Objects.requireNonNull(groupId, "groupId不能为空");
        this.groups.add(groupId);
        this.updatedAt = LocalDateTime.now();
    }

    public void revokeGroup(String groupId) {
        Objects.requireNonNull(groupId, "groupId不能为空");
        this.groups.remove(groupId);
        this.updatedAt = LocalDateTime.now();
    }
} 
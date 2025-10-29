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
 * 部门实体
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Department {

    private String id;
    private String tenantId;
    private String orgId;
    private String name;
    private String parentId; // 上级部门ID，可为空
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Builder.Default
    private Set<String> users = new HashSet<>();

    public static Department create(String tenantId, String orgId, String name, String parentId) {
        return Department.builder()
                .id(java.util.UUID.randomUUID().toString())
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

    public void changeParent(String newParentId) {
        // Business logic for moving a department
        this.parentId = newParentId;
    }

    public void addUser(String userId) {
        System.out.println("[DEBUG][Department.addUser] userId=" + userId + ", users(before)=" + this.users);
        Objects.requireNonNull(userId, "userId不能为空");
        this.users.add(userId);
        this.updatedAt = LocalDateTime.now();
        System.out.println("[DEBUG][Department.addUser] users(after)=" + this.users);
    }

    public void removeUser(String userId) {
        System.out.println("[DEBUG][Department.removeUser] userId=" + userId + ", users(before)=" + this.users);
        Objects.requireNonNull(userId, "userId不能为空");
        this.users.remove(userId);
        this.updatedAt = LocalDateTime.now();
        System.out.println("[DEBUG][Department.removeUser] users(after)=" + this.users);
    }
} 
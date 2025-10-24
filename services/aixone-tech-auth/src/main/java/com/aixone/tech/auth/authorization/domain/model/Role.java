package com.aixone.tech.auth.authorization.domain.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色领域模型
 */
public class Role {
    private String roleId;
    private String tenantId;
    private String name;
    private String description;
    private List<String> permissionIds;
    private List<String> permissions; // 为了兼容测试代码
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Role() {}

    public Role(String roleId, String tenantId, String name, String description, List<String> permissionIds) {
        this.roleId = roleId;
        this.tenantId = tenantId;
        this.name = name;
        this.description = description;
        this.permissionIds = permissionIds;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(List<String> permissionIds) {
        this.permissionIds = permissionIds;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}

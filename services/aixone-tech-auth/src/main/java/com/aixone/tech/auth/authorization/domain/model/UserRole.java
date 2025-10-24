package com.aixone.tech.auth.authorization.domain.model;

import java.time.LocalDateTime;

/**
 * 用户角色关联领域模型
 */
public class UserRole {
    private String userRoleId;
    private String tenantId;
    private String userId;
    private String roleId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserRole() {}

    public UserRole(String userRoleId, String tenantId, String userId, String roleId) {
        this.userRoleId = userRoleId;
        this.tenantId = tenantId;
        this.userId = userId;
        this.roleId = roleId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public UserRole(String userRoleId, String tenantId, String userId, String roleId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userRoleId = userRoleId;
        this.tenantId = tenantId;
        this.userId = userId;
        this.roleId = roleId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public String getUserRoleId() {
        return userRoleId;
    }

    public void setUserRoleId(String userRoleId) {
        this.userRoleId = userRoleId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
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
}

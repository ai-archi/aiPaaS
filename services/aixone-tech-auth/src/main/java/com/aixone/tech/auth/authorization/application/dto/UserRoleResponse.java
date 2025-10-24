package com.aixone.tech.auth.authorization.application.dto;

import java.time.LocalDateTime;

/**
 * 用户角色响应
 */
public class UserRoleResponse {
    
    private String userRoleId;
    private String tenantId;
    private String userId;
    private String roleId;
    private String roleName;
    private String roleDescription;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserRoleResponse() {}

    public UserRoleResponse(String userRoleId, String tenantId, String userId, String roleId, 
                           String roleName, String roleDescription, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userRoleId = userRoleId;
        this.tenantId = tenantId;
        this.userId = userId;
        this.roleId = roleId;
        this.roleName = roleName;
        this.roleDescription = roleDescription;
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

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleDescription() {
        return roleDescription;
    }

    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
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

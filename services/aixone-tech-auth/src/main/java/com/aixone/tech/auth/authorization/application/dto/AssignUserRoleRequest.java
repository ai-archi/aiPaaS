package com.aixone.tech.auth.authorization.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 分配用户角色请求
 */
public class AssignUserRoleRequest {
    
    @NotBlank(message = "租户ID不能为空")
    private String tenantId;
    
    @NotBlank(message = "用户ID不能为空")
    private String userId;
    
    @NotBlank(message = "角色ID不能为空")
    private String roleId;

    public AssignUserRoleRequest() {}

    public AssignUserRoleRequest(String tenantId, String userId, String roleId) {
        this.tenantId = tenantId;
        this.userId = userId;
        this.roleId = roleId;
    }

    // Getters and Setters
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
}

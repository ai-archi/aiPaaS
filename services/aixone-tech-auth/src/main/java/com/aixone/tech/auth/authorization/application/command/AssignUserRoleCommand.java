package com.aixone.tech.auth.authorization.application.command;

/**
 * 分配用户角色命令
 */
public class AssignUserRoleCommand {
    
    private String tenantId;
    private String userId;
    private String roleId;

    public AssignUserRoleCommand() {}

    public AssignUserRoleCommand(String tenantId, String userId, String roleId) {
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

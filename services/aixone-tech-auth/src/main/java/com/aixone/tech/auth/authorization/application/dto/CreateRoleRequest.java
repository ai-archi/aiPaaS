package com.aixone.tech.auth.authorization.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 创建角色请求
 */
public class CreateRoleRequest {
    
    @NotBlank(message = "租户ID不能为空")
    private String tenantId;
    
    @NotBlank(message = "角色名称不能为空")
    private String name;
    
    private String description;
    
    @NotNull(message = "权限列表不能为空")
    private List<String> permissionIds;

    public CreateRoleRequest() {}

    public CreateRoleRequest(String tenantId, String name, String description, List<String> permissionIds) {
        this.tenantId = tenantId;
        this.name = name;
        this.description = description;
        this.permissionIds = permissionIds;
    }

    // Getters and Setters
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
}

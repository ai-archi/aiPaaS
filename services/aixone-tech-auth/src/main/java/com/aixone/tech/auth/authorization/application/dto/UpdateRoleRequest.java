package com.aixone.tech.auth.authorization.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 更新角色请求
 */
public class UpdateRoleRequest {
    
    @NotBlank(message = "角色名称不能为空")
    private String name;
    
    private String description;
    
    @NotNull(message = "权限列表不能为空")
    private List<String> permissionIds;

    public UpdateRoleRequest() {}

    public UpdateRoleRequest(String name, String description, List<String> permissionIds) {
        this.name = name;
        this.description = description;
        this.permissionIds = permissionIds;
    }

    // Getters and Setters
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

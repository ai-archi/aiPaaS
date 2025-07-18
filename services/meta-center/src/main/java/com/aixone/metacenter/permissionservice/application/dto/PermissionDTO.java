package com.aixone.metacenter.permissionservice.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class PermissionDTO {
    private Long id;
    private String name;
    private String displayName;
    private String description;
    private String permissionType;
    private String resource;
    private String action;
    private Long roleId;
    private Long userId;
    private String tenantId;
    private String status;
    private Map<String, Object> extensionFields = new HashMap<>();
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    // Getter and Setter methods
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPermissionType() { return permissionType; }
    public void setPermissionType(String permissionType) { this.permissionType = permissionType; }
    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Map<String, Object> getExtensionFields() { return extensionFields; }
    public void setExtensionFields(Map<String, Object> extensionFields) { this.extensionFields = extensionFields; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
    public LocalDateTime getUpdatedTime() { return updatedTime; }
    public void setUpdatedTime(LocalDateTime updatedTime) { this.updatedTime = updatedTime; }
}

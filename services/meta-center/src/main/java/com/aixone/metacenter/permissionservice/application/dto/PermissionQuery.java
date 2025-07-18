package com.aixone.metacenter.permissionservice.application.dto;

import java.util.List;

public class PermissionQuery {
    private String tenantId;
    private String name;
    private String displayName;
    private String description;
    private String permissionType;
    private String resource;
    private String action;
    private Long roleId;
    private Long userId;
    private List<String> statuses;
    private Integer page = 0;
    private Integer size = 20;
    private String sortBy = "createdTime";
    private String sortDirection = "desc";

    // Getter and Setter methods
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
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
    public List<String> getStatuses() { return statuses; }
    public void setStatuses(List<String> statuses) { this.statuses = statuses; }
    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }
    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }
    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
    public String getSortDirection() { return sortDirection; }
    public void setSortDirection(String sortDirection) { this.sortDirection = sortDirection; }
}

package com.aixone.metacenter.uiservice.application.dto;

import java.util.List;

public class UIMetadataQuery {
    private String tenantId;
    private String name;
    private String displayName;
    private String description;
    private String pageType;
    private String componentType;
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
    public String getPageType() { return pageType; }
    public void setPageType(String pageType) { this.pageType = pageType; }
    public String getComponentType() { return componentType; }
    public void setComponentType(String componentType) { this.componentType = componentType; }
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

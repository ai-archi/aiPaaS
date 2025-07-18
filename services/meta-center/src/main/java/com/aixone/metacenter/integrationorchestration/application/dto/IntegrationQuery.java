package com.aixone.metacenter.integrationorchestration.application.dto;

import java.util.List;

public class IntegrationQuery {
    private String tenantId;
    private String name;
    private String displayName;
    private String description;
    private String integrationType;
    private String sourceSystem;
    private String targetSystem;
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
    public String getIntegrationType() { return integrationType; }
    public void setIntegrationType(String integrationType) { this.integrationType = integrationType; }
    public String getSourceSystem() { return sourceSystem; }
    public void setSourceSystem(String sourceSystem) { this.sourceSystem = sourceSystem; }
    public String getTargetSystem() { return targetSystem; }
    public void setTargetSystem(String targetSystem) { this.targetSystem = targetSystem; }
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

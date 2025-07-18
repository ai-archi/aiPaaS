package com.aixone.metacenter.uiservice.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class UIMetadataDTO {
    private Long id;
    private String name;
    private String displayName;
    private String description;
    private String pageType;
    private String componentType;
    private String layout;
    private String configuration;
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
    public String getPageType() { return pageType; }
    public void setPageType(String pageType) { this.pageType = pageType; }
    public String getComponentType() { return componentType; }
    public void setComponentType(String componentType) { this.componentType = componentType; }
    public String getLayout() { return layout; }
    public void setLayout(String layout) { this.layout = layout; }
    public String getConfiguration() { return configuration; }
    public void setConfiguration(String configuration) { this.configuration = configuration; }
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

package com.aixone.metacenter.integrationorchestration.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class IntegrationDTO {
    private Long id;
    private String name;
    private String displayName;
    private String description;
    private String integrationType;
    private String sourceSystem;
    private String targetSystem;
    private String configuration;
    private String status;
    private String tenantId;
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
    public String getIntegrationType() { return integrationType; }
    public void setIntegrationType(String integrationType) { this.integrationType = integrationType; }
    public String getSourceSystem() { return sourceSystem; }
    public void setSourceSystem(String sourceSystem) { this.sourceSystem = sourceSystem; }
    public String getTargetSystem() { return targetSystem; }
    public void setTargetSystem(String targetSystem) { this.targetSystem = targetSystem; }
    public String getConfiguration() { return configuration; }
    public void setConfiguration(String configuration) { this.configuration = configuration; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public Map<String, Object> getExtensionFields() { return extensionFields; }
    public void setExtensionFields(Map<String, Object> extensionFields) { this.extensionFields = extensionFields; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
    public LocalDateTime getUpdatedTime() { return updatedTime; }
    public void setUpdatedTime(LocalDateTime updatedTime) { this.updatedTime = updatedTime; }
}

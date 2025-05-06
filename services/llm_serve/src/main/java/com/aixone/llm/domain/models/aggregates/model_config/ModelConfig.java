package com.aixone.llm.domain.models.aggregates.model_config;

import lombok.Data;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;
import com.aixone.llm.domain.models.values.config.ProviderInfo;
import com.aixone.llm.domain.models.values.config.ModelCapability;
import com.aixone.llm.domain.models.values.config.RuntimeConfig;
import com.aixone.llm.domain.models.values.config.BillingRule;

@Data
@Builder
@Table("model_configs")
public class ModelConfig {
    @Id
    private String id;
    
    @Version
    private Long version;
    
    private String modelId;
    private String name;
    private String description;
    private ProviderInfo providerInfo;
    private ModelCapability capability;
    private RuntimeConfig runtimeConfig;
    private BillingRule billingRule;
    private boolean active;
    private Long createdAt;
    private Long updatedAt;
    
    public void validate() {
        if (providerInfo == null || !providerInfo.isValid()) {
            throw new IllegalStateException("Provider information must be complete and valid");
        }
        if (runtimeConfig != null && !runtimeConfig.isValid()) {
            throw new IllegalStateException("Runtime configuration parameters must be within valid ranges");
        }
        if (billingRule == null || !billingRule.isValid()) {
            throw new IllegalStateException("Billing rules must be clearly defined");
        }
    }
    
    public boolean isValid() {
        return name != null && !name.isEmpty();
    }
    
    public boolean isActive() {
        return active;
    }
    
    public boolean isAvailable() {
        return active;
    }
    
    public void activate() {
        this.active = true;
    }
    
    public void deactivate() {
        this.active = false;
    }
    
    public void updateRuntimeConfig(RuntimeConfig newConfig) {
        if (newConfig != null && newConfig.isValid()) {
            this.runtimeConfig = newConfig;
        } else {
            throw new IllegalArgumentException("Invalid runtime configuration");
        }
    }
    
    public String getProviderName() {
        return providerInfo != null ? providerInfo.getProviderName() : null;
    }
    
    public String getModelName() {
        return name;
    }
} 
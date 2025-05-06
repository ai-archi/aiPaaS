package com.aixone.llm.domain.models.values.config;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Column;

@Data
@Builder
public class ProviderInfo {
    private String providerId;
    private String providerName;
    private String apiEndpoint;
    private String apiVersion;
    private String apiKey;
    
    @Column("provider_config")
    private String providerConfig; // JSON format for additional provider-specific configuration
    
    public boolean isValid() {
        return providerId != null && !providerId.isBlank() &&
               providerName != null && !providerName.isBlank() &&
               apiEndpoint != null && !apiEndpoint.isBlank() &&
               apiKey != null && !apiKey.isBlank();
    }
} 
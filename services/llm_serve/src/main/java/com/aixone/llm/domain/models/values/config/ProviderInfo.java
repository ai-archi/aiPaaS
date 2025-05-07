package com.aixone.llm.domain.models.values.config;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProviderInfo {
    private String providerId;
    private String providerName;
    private String apiEndpoint;
    private String apiVersion;
    private String apiKey;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String tenantId;
    private boolean deleted;

    @Column("provider_config")
    private String providerConfig; // JSON format for additional provider-specific configuration
    
    public boolean isValid() {
        return providerId != null && !providerId.isBlank() &&
               providerName != null && !providerName.isBlank() &&
               apiEndpoint != null && !apiEndpoint.isBlank() &&
               apiKey != null && !apiKey.isBlank();
    }
} 
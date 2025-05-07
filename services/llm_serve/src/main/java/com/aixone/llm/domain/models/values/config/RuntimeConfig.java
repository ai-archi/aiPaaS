package com.aixone.llm.domain.models.values.config;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RuntimeConfig {
    @Builder.Default
    private double temperature = 1.0;
    
    @Builder.Default
    private double topP = 1.0;
    
    @Builder.Default
    private int maxOutputTokens = 2048;
    
    @Builder.Default
    private double presencePenalty = 0.0;
    
    @Builder.Default
    private double frequencyPenalty = 0.0;
    
    @Builder.Default
    private int numCompletions = 1;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String tenantId;
    private boolean deleted;
    
    public boolean isValid() {
        return temperature >= 0.0 && temperature <= 2.0 &&
               topP >= 0.0 && topP <= 1.0 &&
               maxOutputTokens > 0 &&
               presencePenalty >= -2.0 && presencePenalty <= 2.0 &&
               frequencyPenalty >= -2.0 && frequencyPenalty <= 2.0 &&
               numCompletions > 0 && numCompletions <= 10;
    }
} 
package com.aixone.llm.domain.models.values.config;

import lombok.Builder;
import lombok.Data;
import java.util.Set;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelCapability {
    private Set<CapabilityType> capabilityTypes;
    private Set<String> supportedTasks; // chat, completion, embedding, etc.
    private int maxTokens;
    private boolean streamingSupported;
    private boolean functionCallSupported;
    private Set<String> supportedLanguages;
    
    @Builder.Default
    private int contextWindow = 4096; // Default context window size
    
    private double temperature;
    private int topP;
    private int topK;
    private double presencePenalty;
    private double frequencyPenalty;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String tenantId;
    private boolean deleted;

    public boolean supportsTask(String task) {
        return supportedTasks != null && supportedTasks.contains(task);
    }
    
    public boolean supportsLanguage(String language) {
        return supportedLanguages != null && supportedLanguages.contains(language);
    }
} 
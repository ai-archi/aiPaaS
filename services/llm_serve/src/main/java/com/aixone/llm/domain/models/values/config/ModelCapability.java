package com.aixone.llm.domain.models.values.config;

import lombok.Builder;
import lombok.Data;
import java.util.Set;

@Data
@Builder
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
    
    public boolean supportsTask(String task) {
        return supportedTasks != null && supportedTasks.contains(task);
    }
    
    public boolean supportsLanguage(String language) {
        return supportedLanguages != null && supportedLanguages.contains(language);
    }
} 
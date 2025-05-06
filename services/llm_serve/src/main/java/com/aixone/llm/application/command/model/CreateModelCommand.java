package com.aixone.llm.application.command.model;

import com.aixone.llm.domain.models.aggregates.model_config.ModelConfig;
import com.aixone.llm.domain.models.values.config.ProviderInfo;
import com.aixone.llm.domain.models.values.config.ModelCapability;
import com.aixone.llm.domain.models.values.config.RuntimeConfig;
import com.aixone.llm.domain.models.values.config.BillingRule;
import com.aixone.llm.domain.models.values.config.CapabilityType;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

@Data
public class CreateModelCommand {
    @NotBlank
    private String modelId;
    
    @NotNull
    private ProviderInfo providerInfo;
    
    @NotNull
    private Set<CapabilityType> capabilityTypes;
    
    private int maxTokens;
    private double temperature;
    private int topP;
    private int topK;
    private double presencePenalty;
    private double frequencyPenalty;
    
    @NotNull
    private RuntimeConfig runtimeConfig;
    
    @NotNull
    private BillingRule billingRule;
    
    public ModelConfig toModelConfig() {
        return ModelConfig.builder()
            .modelId(modelId)
            .providerInfo(providerInfo)
            .capability(ModelCapability.builder()
                .capabilityTypes(capabilityTypes)
                .maxTokens(maxTokens)
                .temperature(temperature)
                .topP(topP)
                .topK(topK)
                .presencePenalty(presencePenalty)
                .frequencyPenalty(frequencyPenalty)
                .build())
            .runtimeConfig(runtimeConfig)
            .billingRule(billingRule)
            .active(false)
            .createdAt(System.currentTimeMillis())
            .updatedAt(System.currentTimeMillis())
            .build();
    }
} 
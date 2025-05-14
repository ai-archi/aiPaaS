package com.aixone.llm.application.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.aixone.llm.domain.models.model.ModelConfig;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateModelCommand {
    @NotBlank
    private String id;
    @NotBlank
    private String name;
    @NotBlank
    private String endpoint;
    @NotBlank
    private String apiKey;
    @NotNull
    private Integer maxTokens;
    @NotNull
    private BigDecimal minInputPrice;
    @NotNull
    private BigDecimal minOutputPrice;
    private boolean supportTextGeneration;
    private boolean supportImageGeneration;
    private boolean supportSpeechGeneration;
    private boolean supportVideoGeneration;
    private boolean supportVector;
    private boolean active;
    private String description;
    private String tenantId;
    private boolean isSystemPreset;

    public ModelConfig toModelConfig() {
        return ModelConfig.builder()
            .id(id)
            .name(name)
            .endpoint(endpoint)
            .maxTokens(maxTokens)
            .minInputPrice(minInputPrice)
            .minOutputPrice(minOutputPrice)
            .supportTextGeneration(supportTextGeneration)
            .supportImageGeneration(supportImageGeneration)
            .supportSpeechGeneration(supportSpeechGeneration)
            .supportVideoGeneration(supportVideoGeneration)
            .supportVector(supportVector)
            .active(active)
            .description(description)
            .tenantId(tenantId)
            .isSystemPreset(isSystemPreset)
            .updatedAt(LocalDateTime.now())
            .build();
    }
} 
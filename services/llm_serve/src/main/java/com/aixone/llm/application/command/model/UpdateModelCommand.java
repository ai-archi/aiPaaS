package com.aixone.llm.application.command.model;

import com.aixone.llm.domain.models.aggregates.model_config.ModelConfig;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
            .apiKey(apiKey)
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
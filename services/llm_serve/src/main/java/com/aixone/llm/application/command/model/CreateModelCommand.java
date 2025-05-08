package com.aixone.llm.application.command.model;

import com.aixone.llm.domain.models.aggregates.model_config.ModelConfig;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Data
public class CreateModelCommand {
    @NotBlank
    private String name;
    @NotBlank
    private String endpoint;
    @NotBlank
    private String apiKey;
    @NotNull
    private Integer maxTokens;
    @NotNull
    private boolean active;

    private String description;
    private String tenantId;
    private boolean isSystemPreset;
    private Long version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BigDecimal minInputPrice;
    private BigDecimal minOutputPrice;

    // 能力描述
    @NotNull
    private boolean supportTextGeneration;
    @NotNull
    private boolean supportImageGeneration;
    @NotNull
    private boolean supportSpeechGeneration;
    @NotNull
    private boolean supportVideoGeneration;
    @NotNull
    private boolean supportVector;

    // 可选扩展
    private String providerName;   // 厂商名
    private String modelCode;      // 型号
    private String priceUnit;      // 计费单位
    private String currency;       // 币种
    private Integer qpsLimit;      // QPS限制
    private String region;         // 区域
    private List<String> tags;     // 标签
    private String status;  

    public ModelConfig toModelConfig() {
        return ModelConfig.builder()
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
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .providerName(providerName)
            .modelCode(modelCode)
            .priceUnit(priceUnit)
            .currency(currency)
            .qpsLimit(qpsLimit)
            .region(region)
            .tags(tags)
            .status(status)
            .build();
    }
} 
package com.aixone.llm.infrastructure.entity;

import com.aixone.llm.domain.models.aggregates.model_config.ModelConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("model_configs")
public class ModelConfigEntity {
    @Id
    private String id;
    private String name;
    private String endpoint;
    private String apiKey;
    private Integer maxTokens;
    private Boolean active;
    private String description;
    private String tenantId;
    @Column("is_system_preset")
    private Boolean isSystemPreset;
    @Column("version")
    private Long version;
    @Column("created_at")
    private LocalDateTime createdAt;
    @Column("updated_at")
    private LocalDateTime updatedAt;
    @Column("min_input_price")
    private BigDecimal minInputPrice;
    @Column("min_output_price")
    private BigDecimal minOutputPrice;
    @Column("support_text_generation")
    private Boolean supportTextGeneration;
    @Column("support_image_generation")
    private Boolean supportImageGeneration;
    @Column("support_speech_generation")
    private Boolean supportSpeechGeneration;
    @Column("support_video_generation")
    private Boolean supportVideoGeneration;
    @Column("support_vector")
    private Boolean supportVector;

    public static ModelConfigEntity fromDomain(ModelConfig modelConfig) {
        return ModelConfigEntity.builder()
                .id(modelConfig.getId())
                .name(modelConfig.getName())
                .endpoint(modelConfig.getEndpoint())
                .apiKey(modelConfig.getApiKey())
                .maxTokens(modelConfig.getMaxTokens())
                .active(modelConfig.isActive())
                .description(modelConfig.getDescription())
                .tenantId(modelConfig.getTenantId())
                .isSystemPreset(modelConfig.isSystemPreset())
                .version(modelConfig.getVersion())
                .createdAt(modelConfig.getCreatedAt())
                .updatedAt(modelConfig.getUpdatedAt())
                .minInputPrice(modelConfig.getMinInputPrice())
                .minOutputPrice(modelConfig.getMinOutputPrice())
                .supportTextGeneration(modelConfig.isSupportTextGeneration())
                .supportImageGeneration(modelConfig.isSupportImageGeneration())
                .supportSpeechGeneration(modelConfig.isSupportSpeechGeneration())
                .supportVideoGeneration(modelConfig.isSupportVideoGeneration())
                .supportVector(modelConfig.isSupportVector())
                .build();
    }

    public ModelConfig toDomain() {
        return ModelConfig.builder()
                .id(this.id)
                .name(this.name)
                .endpoint(this.endpoint)
                .apiKey(this.apiKey)
                .maxTokens(this.maxTokens)
                .active(this.active != null && this.active)
                .description(this.description)
                .tenantId(this.tenantId)
                .isSystemPreset(this.isSystemPreset != null && this.isSystemPreset)
                .version(this.version)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .minInputPrice(this.minInputPrice)
                .minOutputPrice(this.minOutputPrice)
                .supportTextGeneration(this.supportTextGeneration != null && this.supportTextGeneration)
                .supportImageGeneration(this.supportImageGeneration != null && this.supportImageGeneration)
                .supportSpeechGeneration(this.supportSpeechGeneration != null && this.supportSpeechGeneration)
                .supportVideoGeneration(this.supportVideoGeneration != null && this.supportVideoGeneration)
                .supportVector(this.supportVector != null && this.supportVector)
                .build();
    }
} 
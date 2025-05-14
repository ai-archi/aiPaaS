package com.aixone.llm.infrastructure.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.aixone.llm.domain.models.model.ModelConfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Column("charge_type")
    private String chargeType;
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
    @Column("provider_name")
    private String providerName;
    @Column("price_unit")
    private String priceUnit;
    @Column("currency")
    private String currency;
    @Column("qps_limit")
    private Integer qpsLimit;
    @Column("region")
    private String region;
    @Column("tags")
    private String tags;
    @Column("status")
    private String status;

    public static ModelConfigEntity fromDomain(ModelConfig modelConfig) {
        return ModelConfigEntity.builder()
                .id(modelConfig.getId())
                .name(modelConfig.getName())
                .endpoint(modelConfig.getEndpoint())
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
                .chargeType(modelConfig.getChargeType())
                .supportTextGeneration(modelConfig.isSupportTextGeneration())
                .supportImageGeneration(modelConfig.isSupportImageGeneration())
                .supportSpeechGeneration(modelConfig.isSupportSpeechGeneration())
                .supportVideoGeneration(modelConfig.isSupportVideoGeneration())
                .supportVector(modelConfig.isSupportVector())
                .providerName(modelConfig.getProviderName())
                .priceUnit(modelConfig.getPriceUnit())
                .currency(modelConfig.getCurrency())
                .qpsLimit(modelConfig.getQpsLimit())
                .region(modelConfig.getRegion())
                .tags(modelConfig.getTags() != null ? String.join(",", modelConfig.getTags()) : null)
                .status(modelConfig.getStatus())
                .build();
    }

    public ModelConfig toDomain() {
        return ModelConfig.builder()
                .id(this.id)
                .name(this.name)
                .endpoint(this.endpoint)
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
                .chargeType(this.chargeType)
                .supportTextGeneration(this.supportTextGeneration != null && this.supportTextGeneration)
                .supportImageGeneration(this.supportImageGeneration != null && this.supportImageGeneration)
                .supportSpeechGeneration(this.supportSpeechGeneration != null && this.supportSpeechGeneration)
                .supportVideoGeneration(this.supportVideoGeneration != null && this.supportVideoGeneration)
                .supportVector(this.supportVector != null && this.supportVector)
                .providerName(this.providerName)
                .priceUnit(this.priceUnit)
                .currency(this.currency)
                .qpsLimit(this.qpsLimit)
                .region(this.region)
                .tags(this.tags != null ? Arrays.asList(this.tags.split(",")) : null)
                .status(this.status)
                .build();
    }
} 
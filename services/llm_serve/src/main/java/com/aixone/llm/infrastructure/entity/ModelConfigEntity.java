package com.aixone.llm.infrastructure.entity;

import com.aixone.llm.domain.models.aggregates.model_config.ModelConfig;
import com.aixone.llm.domain.models.values.config.ProviderInfo;
import com.aixone.llm.domain.models.values.config.ModelCapability;
import com.aixone.llm.domain.models.values.config.RuntimeConfig;
import com.aixone.llm.domain.models.values.config.BillingRule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.relational.core.mapping.Column;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("model_configs")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelConfigEntity {
    @Id
    private String id;
    private String name;
    private Boolean active;
    private String tenantId;
    private Boolean deleted;
    @Column("version")
    private Long version;
    @Column("created_at")
    private Long createdAt;
    @Column("updated_at")
    private Long updatedAt;
    @Column("provider_info_json")
    private String providerInfoJson;
    @Column("capability_json")
    private String capabilityJson;
    @Column("runtime_config_json")
    private String runtimeConfigJson;
    @Column("billing_rule_json")
    private String billingRuleJson;
    @Column("description")
    private String description;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static ModelConfigEntity fromDomain(ModelConfig modelConfig) {
        try {
            return ModelConfigEntity.builder()
                    .id(modelConfig.getId())
                    .name(modelConfig.getName())
                    .active(modelConfig.isActive())
                    .tenantId(modelConfig.getTenantId())
                    .deleted(modelConfig.isDeleted())
                    .description(modelConfig.getDescription()==null?null:modelConfig.getDescription())
                    .version(modelConfig.getVersion()==null?0:modelConfig.getVersion())
                    .createdAt(modelConfig.getCreatedAt() == null ? null : modelConfig.getCreatedAt().toEpochSecond(java.time.ZoneOffset.UTC))
                    .updatedAt(modelConfig.getUpdatedAt() == null ? null : modelConfig.getUpdatedAt().toEpochSecond(java.time.ZoneOffset.UTC))
                    .providerInfoJson(modelConfig.getProviderInfo() == null ? null : objectMapper.writeValueAsString(modelConfig.getProviderInfo()))
                    .capabilityJson(modelConfig.getCapability() == null ? null : objectMapper.writeValueAsString(modelConfig.getCapability()))
                    .runtimeConfigJson(modelConfig.getRuntimeConfig() == null ? null : objectMapper.writeValueAsString(modelConfig.getRuntimeConfig()))
                    .billingRuleJson(modelConfig.getBillingRule() == null ? null : objectMapper.writeValueAsString(modelConfig.getBillingRule()))
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("ModelConfigEntity.fromDomain序列化失败", e);
        }
    }

    public ModelConfig toDomain() {
        try {
            return ModelConfig.builder()
                    .id(this.id)
                    .name(this.name)
                    .active(this.active != null && this.active)
                    .tenantId(this.tenantId)
                    .deleted(this.deleted != null && this.deleted)
                    .description(this.description)
                    .version(this.version)
                    .createdAt(this.createdAt == null ? null : java.time.LocalDateTime.ofEpochSecond(this.createdAt, 0, java.time.ZoneOffset.UTC))
                    .updatedAt(this.updatedAt == null ? null : java.time.LocalDateTime.ofEpochSecond(this.updatedAt, 0, java.time.ZoneOffset.UTC))
                    .providerInfo(isValidJson(this.providerInfoJson) ? objectMapper.readValue(this.providerInfoJson, ProviderInfo.class) : null)
                    .capability(isValidJson(this.capabilityJson) ? objectMapper.readValue(this.capabilityJson, ModelCapability.class) : null)
                    .runtimeConfig(isValidJson(this.runtimeConfigJson) ? objectMapper.readValue(this.runtimeConfigJson, RuntimeConfig.class) : null)
                    .billingRule(isValidJson(this.billingRuleJson) ? objectMapper.readValue(this.billingRuleJson, BillingRule.class) : null)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("ModelConfigEntity.toDomain反序列化失败", e);
        }
    }

    private boolean isValidJson(String json) {
        return json != null && !json.trim().isEmpty() && !"null".equals(json.trim());
    }
} 
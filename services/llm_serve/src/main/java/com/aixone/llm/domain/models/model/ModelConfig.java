package com.aixone.llm.domain.models.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ModelConfig {
    @Id
    private String id;
    private String name;
    private String endpoint;
    private Integer maxTokens;
    private boolean active;
    private String description;
    private String tenantId;
    private boolean isSystemPreset;
    private Long version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BigDecimal minInputPrice;
    private BigDecimal minOutputPrice;
    private String chargeType; // "token" or "count"

    // 能力描述
    private boolean supportTextGeneration;
    private boolean supportImageGeneration;
    private boolean supportSpeechGeneration;
    private boolean supportVideoGeneration;
    private boolean supportVector;

    // 可选扩展
    private String providerName;   // 厂商名
    private String priceUnit;      // 计费单位
    private String currency;       // 币种
    private Integer qpsLimit;      // QPS限制
    private String region;         // 区域
    private List<String> tags;     // 标签
    private String status;         // 状态
}
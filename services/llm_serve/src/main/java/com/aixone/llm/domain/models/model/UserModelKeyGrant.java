package com.aixone.llm.domain.models.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户Key授权关系，支持自定义计费方式（按次/按token）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserModelKeyGrant {
    @Id
    private String id;
    private String keyId;        // 关联UserModelKey.id
    private String granteeId;    // 被授权用户
    private String chargeType;   // "count" 或 "token"
    private BigDecimal price;    // 授权价格
    private String priceUnit;    // 计费单位（如CNY/次、CNY/1k tokens）
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String description;  // 授权说明
} 
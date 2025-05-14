package com.aixone.llm.infrastructure.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("user_model_key_grants")
public class UserModelKeyGrantEntity {
    @Id
    private String id;
    private String keyId;
    private String granteeId;
    private String chargeType;
    private BigDecimal price;
    private String priceUnit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String description;

    public static UserModelKeyGrantEntity fromDomain(com.aixone.llm.domain.models.model.UserModelKeyGrant domain) {
        return UserModelKeyGrantEntity.builder()
                .id(domain.getId())
                .keyId(domain.getKeyId())
                .granteeId(domain.getGranteeId())
                .chargeType(domain.getChargeType())
                .price(domain.getPrice())
                .priceUnit(domain.getPriceUnit())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .description(domain.getDescription())
                .build();
    }

    public com.aixone.llm.domain.models.model.UserModelKeyGrant toDomain() {
        return com.aixone.llm.domain.models.model.UserModelKeyGrant.builder()
                .id(this.id)
                .keyId(this.keyId)
                .granteeId(this.granteeId)
                .chargeType(this.chargeType)
                .price(this.price)
                .priceUnit(this.priceUnit)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .description(this.description)
                .build();
    }
} 
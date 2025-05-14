package com.aixone.llm.infrastructure.entity;

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
@Table("user_model_keys")
public class UserModelKeyEntity {
    @Id
    private String id;
    private String ownerId;
    private String modelName;
    private String apiKey;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String description;

    public static UserModelKeyEntity fromDomain(com.aixone.llm.domain.models.model.UserModelKey domain) {
        return UserModelKeyEntity.builder()
                .id(domain.getId())
                .ownerId(domain.getUserId())
                .modelName(domain.getModelName())
                .apiKey(domain.getApiKey())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .description(domain.getDescription())
                .build();
    }

    public com.aixone.llm.domain.models.model.UserModelKey toDomain() {
        return com.aixone.llm.domain.models.model.UserModelKey.builder()
                .id(this.id)
                .userId(this.ownerId)
                .modelName(this.modelName)
                .apiKey(this.apiKey)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .description(this.description)
                .build();
    }
} 
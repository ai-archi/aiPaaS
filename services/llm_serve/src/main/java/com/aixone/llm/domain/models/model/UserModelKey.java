package com.aixone.llm.domain.models.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户-模型-APIKey三元关系聚合根
 * userId+modelName唯一，支持用户自定义key和系统默认key
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserModelKey {
    @Id
    private String id;
    private String userId;      // 归属用户，"system"为平台默认key
    private String modelName;   // 关联模型名
    private String provider;     // 厂商名
    private String apiKey;      // 用户自己的key
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String description;
}
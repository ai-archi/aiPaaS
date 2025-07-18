package com.aixone.metacenter.integrationorchestration.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 集成编排实体
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "integrations", indexes = {
    @Index(name = "idx_integrations_tenant_id", columnList = "tenant_id"),
    @Index(name = "idx_integrations_name", columnList = "name"),
    @Index(name = "idx_integrations_type", columnList = "type"),
    @Index(name = "idx_integrations_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
public class Integration {

    /** 唯一标识 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 集成名称 */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /** 集成描述 */
    @Column(name = "description", length = 500)
    private String description;

    /** 集成类型 */
    @Column(name = "type", nullable = false, length = 50)
    private String type;

    /** 集成配置 */
    @Column(name = "config", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> config = new HashMap<>();

    /** 集成状态 */
    @Column(name = "status", nullable = false, length = 20)
    private String status = "draft";

    /** 版本号 */
    @Column(name = "version", nullable = false)
    private Integer version = 1;

    /** 租户ID */
    @Column(name = "tenant_id", nullable = false, length = 50)
    private String tenantId;

    /** 扩展字段 */
    @Column(name = "extension_fields", columnDefinition = "jsonb")
    private Map<String, Object> extensionFields = new HashMap<>();

    /** 创建人 */
    @Column(name = "created_by", length = 100)
    private String createdBy;

    /** 创建时间 */
    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /** 更新人 */
    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    /** 更新时间 */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 检查是否为草稿状态
     * 
     * @return 是否为草稿状态
     */
    public boolean isDraft() {
        return "draft".equals(status);
    }

    /**
     * 检查是否为已发布状态
     * 
     * @return 是否为已发布状态
     */
    public boolean isPublished() {
        return "published".equals(status);
    }

    /**
     * 增加版本号
     */
    public void incrementVersion() {
        this.version++;
    }

    /**
     * 获取配置值
     * 
     * @param key 配置键
     * @return 配置值
     */
    public Object getConfigValue(String key) {
        return config.get(key);
    }

    /**
     * 设置配置值
     * 
     * @param key 配置键
     * @param value 配置值
     */
    public void setConfigValue(String key, Object value) {
        config.put(key, value);
    }
} 
package com.aixone.metacenter.metamanagement.domain;

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
 * 元数据扩展点实体
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "meta_extensions", indexes = {
    @Index(name = "idx_meta_extensions_meta_object_id", columnList = "meta_object_id"),
    @Index(name = "idx_meta_extensions_extension_type", columnList = "extension_type"),
    @Index(name = "idx_meta_extensions_key", columnList = "extension_key")
})
@EntityListeners(AuditingEntityListener.class)
public class MetaExtension {

    /** 唯一标识 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 扩展点标识 */
    @Column(name = "extension_key", nullable = false, length = 100)
    private String extensionKey;

    /** 扩展类型 */
    @Column(name = "extension_type", nullable = false, length = 50)
    private String extensionType;

    /** 扩展值 */
    @Column(name = "extension_value", columnDefinition = "jsonb")
    private Object extensionValue;

    /** 扩展处理器 */
    @Column(name = "handler", length = 200)
    private String handler;

    /** 作用域 */
    @Column(name = "scope", length = 50)
    private String scope;

    /** 是否启用 */
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    /** 版本号 */
    @Column(name = "version")
    private String version;

    /** 扩展字段 */
    @Column(name = "extension_fields", columnDefinition = "jsonb")
    private Map<String, Object> extensionFields = new HashMap<>();

    /** 创建时间 */
    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /** 关联的元数据对象 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meta_object_id", nullable = false)
    private MetaObject metaObject;

    /**
     * 检查是否为启用状态
     * 
     * @return 是否为启用状态
     */
    public boolean isEnabled() {
        return Boolean.TRUE.equals(enabled);
    }

    /**
     * 获取扩展配置
     * 
     * @return 扩展配置
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getExtensionConfig() {
        if (extensionValue instanceof Map) {
            return (Map<String, Object>) extensionValue;
        }
        return new HashMap<>();
    }

    /**
     * 设置扩展配置
     * 
     * @param config 扩展配置
     */
    public void setExtensionConfig(Map<String, Object> config) {
        this.extensionValue = config;
    }
} 
package com.aixone.metacenter.uiservice.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UI元数据实体
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "ui_metadata", indexes = {
    @Index(name = "idx_ui_metadata_tenant_id", columnList = "tenant_id"),
    @Index(name = "idx_ui_metadata_page_id", columnList = "page_id"),
    @Index(name = "idx_ui_metadata_type", columnList = "type"),
    @Index(name = "idx_ui_metadata_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
public class UIMetadata {

    /** 唯一标识 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 页面ID */
    @Column(name = "page_id", nullable = false, length = 100)
    private String pageId;

    /** 页面标题 */
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /** 页面描述 */
    @Column(name = "description", length = 500)
    private String description;

    /** UI类型 */
    @Column(name = "type", nullable = false, length = 50)
    private String type;

    /** 页面元数据 */
    @Column(name = "page_metadata", columnDefinition = "jsonb")
    private Map<String, Object> pageMetadata = new HashMap<>();

    /** 组件元数据列表 */
    @Column(name = "component_metadata", columnDefinition = "jsonb")
    private List<Map<String, Object>> componentMetadata = new ArrayList<>();

    /** 主题 */
    @Column(name = "theme", length = 50)
    private String theme = "default";

    /** 版本号 */
    @Column(name = "version", nullable = false)
    private Integer version = 1;

    /** 状态 */
    @Column(name = "status", nullable = false, length = 20)
    private String status = "draft";

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
     * 获取页面元数据值
     * 
     * @param key 键
     * @return 值
     */
    public Object getPageMetadataValue(String key) {
        return pageMetadata.get(key);
    }

    /**
     * 设置页面元数据值
     * 
     * @param key 键
     * @param value 值
     */
    public void setPageMetadataValue(String key, Object value) {
        pageMetadata.put(key, value);
    }
} 
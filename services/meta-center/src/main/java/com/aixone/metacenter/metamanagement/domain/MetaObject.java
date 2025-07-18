package com.aixone.metacenter.metamanagement.domain;

import com.aixone.metacenter.common.constant.MetaConstants;
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
 * 元数据聚合根实体
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "meta_objects", indexes = {
    @Index(name = "idx_meta_objects_tenant_id", columnList = "tenant_id"),
    @Index(name = "idx_meta_objects_type", columnList = "type"),
    @Index(name = "idx_meta_objects_name", columnList = "name"),
    @Index(name = "idx_meta_objects_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
public class MetaObject {

    /** 唯一标识 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 元数据名称 */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /** 对象类型 */
    @Column(name = "object_type", nullable = false, length = 50)
    private String objectType;

    /** 主类型（业务/技术/管理/参考） */
    @Column(name = "type", nullable = false, length = 50)
    private String type;

    /** 子类型 */
    @Column(name = "sub_type", length = 50)
    private String subType;

    /** 描述 */
    @Column(name = "description", length = 500)
    private String description;

    /** 标签 */
    @Column(name = "tags", length = 200)
    private String tags;

    /** 生命周期状态 */
    @Column(name = "lifecycle", nullable = false, length = 20)
    private String lifecycle = MetaConstants.LifecycleStatus.DRAFT;

    /** 运行状态 */
    @Column(name = "status", nullable = false, length = 20)
    private String status = MetaConstants.RuntimeStatus.ENABLED;

    /** 版本号 */
    @Column(name = "version", nullable = false)
    private Integer version = 1;

    /** 责任人 */
    @Column(name = "owner", length = 100)
    private String owner;

    /** 质量分数 */
    @Column(name = "quality_score")
    private Integer qualityScore;

    /** 合规等级 */
    @Column(name = "compliance_level", length = 20)
    private String complianceLevel;

    /** 租户ID */
    @Column(name = "tenant_id", nullable = false, length = 50)
    private String tenantId;

    /** 多语言支持 */
    @Column(name = "i18n", columnDefinition = "jsonb")
    private Map<String, String> i18n = new HashMap<>();

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

    /** 属性列表 */
    @OneToMany(mappedBy = "metaObject", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MetaAttribute> attributes = new ArrayList<>();

    /** 关系列表 */
    @OneToMany(mappedBy = "sourceObject", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MetaRelation> relations = new ArrayList<>();

    /** 规则列表 */
    @OneToMany(mappedBy = "metaObject", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MetaRule> rules = new ArrayList<>();

    /** 扩展点列表 */
    @OneToMany(mappedBy = "metaObject", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MetaExtension> extensions = new ArrayList<>();

    /**
     * 添加属性
     * 
     * @param attribute 属性
     */
    public void addAttribute(MetaAttribute attribute) {
        attributes.add(attribute);
        attribute.setMetaObject(this);
    }

    /**
     * 移除属性
     * 
     * @param attribute 属性
     */
    public void removeAttribute(MetaAttribute attribute) {
        attributes.remove(attribute);
        attribute.setMetaObject(null);
    }

    /**
     * 添加关系
     * 
     * @param relation 关系
     */
    public void addRelation(MetaRelation relation) {
        relations.add(relation);
        // 设置源对象
        relation.setSourceObject(this);
    }

    /**
     * 移除关系
     * 
     * @param relation 关系
     */
    public void removeRelation(MetaRelation relation) {
        relations.remove(relation);
        relation.setSourceObject(null);
    }

    /**
     * 添加规则
     * 
     * @param rule 规则
     */
    public void addRule(MetaRule rule) {
        rules.add(rule);
        rule.setMetaObject(this);
    }

    /**
     * 移除规则
     * 
     * @param rule 规则
     */
    public void removeRule(MetaRule rule) {
        rules.remove(rule);
        rule.setMetaObject(null);
    }

    /**
     * 添加扩展点
     * 
     * @param extension 扩展点
     */
    public void addExtension(MetaExtension extension) {
        extensions.add(extension);
        extension.setMetaObject(this);
    }

    /**
     * 移除扩展点
     * 
     * @param extension 扩展点
     */
    public void removeExtension(MetaExtension extension) {
        extensions.remove(extension);
        extension.setMetaObject(null);
    }

    /**
     * 检查是否为草稿状态
     * 
     * @return 是否为草稿状态
     */
    public boolean isDraft() {
        return MetaConstants.LifecycleStatus.DRAFT.equals(lifecycle);
    }

    /**
     * 检查是否为已发布状态
     * 
     * @return 是否为已发布状态
     */
    public boolean isPublished() {
        return MetaConstants.LifecycleStatus.PUBLISHED.equals(lifecycle);
    }

    /**
     * 检查是否为启用状态
     * 
     * @return 是否为启用状态
     */
    public boolean isEnabled() {
        return MetaConstants.RuntimeStatus.ENABLED.equals(status);
    }

    /**
     * 增加版本号
     */
    public void incrementVersion() {
        this.version++;
    }
} 
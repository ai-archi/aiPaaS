package com.aixone.metacenter.metamanagement.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 元数据关系实体
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "meta_relations", indexes = {
    @Index(name = "idx_meta_relations_source_id", columnList = "source_meta_object_id"),
    @Index(name = "idx_meta_relations_target_id", columnList = "target_meta_object_id"),
    @Index(name = "idx_meta_relations_type", columnList = "relation_type")
})
@EntityListeners(AuditingEntityListener.class)
public class MetaRelation {

    /** 唯一标识 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 关系名称 */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /** 关系类型 */
    @Column(name = "relation_type", nullable = false, length = 50)
    private String relationType;

    /** 方向性 */
    @Column(name = "direction", nullable = false, length = 20)
    private String direction = "bidirectional";

    /** 路径性 */
    @Column(name = "path", length = 200)
    private String path;

    /** 是否可导航 */
    @Column(name = "navigable", nullable = false)
    private Boolean navigable = true;

    /** 约束条件 */
    @Column(name = "constraints", length = 500)
    private String constraints;

    /** 关系基数 */
    @Column(name = "cardinality", length = 20)
    private String cardinality;

    /** 关系描述 */
    @Column(name = "description", length = 500)
    private String description;

    /** 扩展字段 */
    @Column(name = "extension_fields", columnDefinition = "jsonb")
    private Map<String, Object> extensionFields = new HashMap<>();

    /** 显示名称 */
    @Column(name = "display_name", length = 100)
    private String displayName;

    /** 创建时间 */
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    /** 更新时间 */
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    /** 源元数据对象 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_meta_object_id", nullable = false)
    private MetaObject sourceObject;

    /** 目标元数据对象 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_meta_object_id", nullable = false)
    private MetaObject targetObject;

    // Getter and Setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getNavigable() {
        return navigable;
    }

    public void setNavigable(Boolean navigable) {
        this.navigable = navigable;
    }

    public String getConstraints() {
        return constraints;
    }

    public void setConstraints(String constraints) {
        this.constraints = constraints;
    }

    public String getCardinality() {
        return cardinality;
    }

    public void setCardinality(String cardinality) {
        this.cardinality = cardinality;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Object> getExtensionFields() {
        return extensionFields;
    }

    public void setExtensionFields(Map<String, Object> extensionFields) {
        this.extensionFields = extensionFields;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public LocalDateTime getCreatedAt() {
        return createdTime;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdTime = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedTime;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedTime = updatedAt;
    }

    public MetaObject getSourceObject() {
        return sourceObject;
    }

    public void setSourceObject(MetaObject sourceObject) {
        this.sourceObject = sourceObject;
    }

    public MetaObject getTargetObject() {
        return targetObject;
    }

    public void setTargetObject(MetaObject targetObject) {
        this.targetObject = targetObject;
    }

    /**
     * 检查是否为双向关系
     * 
     * @return 是否为双向关系
     */
    public boolean isBidirectional() {
        return "bidirectional".equals(direction);
    }

    /**
     * 检查是否为单向关系
     * 
     * @return 是否为单向关系
     */
    public boolean isUnidirectional() {
        return "unidirectional".equals(direction);
    }

    /**
     * 检查是否为可导航关系
     * 
     * @return 是否为可导航关系
     */
    public boolean isNavigable() {
        return Boolean.TRUE.equals(navigable);
    }
} 
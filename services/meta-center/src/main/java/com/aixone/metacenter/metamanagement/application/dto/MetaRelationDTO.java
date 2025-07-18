package com.aixone.metacenter.metamanagement.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 元数据关系DTO
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
public class MetaRelationDTO {

    /** 唯一标识 */
    private Long id;

    /** 关系名称 */
    private String name;

    /** 关系类型 */
    private String relationType;

    /** 方向性 */
    private String direction;

    /** 路径性 */
    private String path;

    /** 是否可导航 */
    private Boolean navigable;

    /** 约束条件 */
    private String constraints;

    /** 扩展字段 */
    private Map<String, Object> extensionFields = new HashMap<>();

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /** 源元数据对象ID */
    private Long sourceMetaObjectId;

    /** 目标元数据对象ID */
    private Long targetMetaObjectId;

    /** 源元数据对象名称 */
    private String sourceObjectName;

    /** 目标元数据对象名称 */
    private String targetObjectName;

    /** 关系基数 */
    private String cardinality;

    /** 关系描述 */
    private String description;

    /** 源对象ID */
    private Long sourceObjectId;

    /** 目标对象ID */
    private Long targetObjectId;

    /** 显示名称 */
    private String displayName;

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

    public Map<String, Object> getExtensionFields() {
        return extensionFields;
    }

    public void setExtensionFields(Map<String, Object> extensionFields) {
        this.extensionFields = extensionFields;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getSourceMetaObjectId() {
        return sourceMetaObjectId;
    }

    public void setSourceMetaObjectId(Long sourceMetaObjectId) {
        this.sourceMetaObjectId = sourceMetaObjectId;
    }

    public Long getTargetMetaObjectId() {
        return targetMetaObjectId;
    }

    public void setTargetMetaObjectId(Long targetMetaObjectId) {
        this.targetMetaObjectId = targetMetaObjectId;
    }

    public String getSourceObjectName() {
        return sourceObjectName;
    }

    public void setSourceObjectName(String sourceObjectName) {
        this.sourceObjectName = sourceObjectName;
    }

    public String getTargetObjectName() {
        return targetObjectName;
    }

    public void setTargetObjectName(String targetObjectName) {
        this.targetObjectName = targetObjectName;
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

    public Long getSourceObjectId() {
        return sourceObjectId;
    }

    public void setSourceObjectId(Long sourceObjectId) {
        this.sourceObjectId = sourceObjectId;
    }

    public Long getTargetObjectId() {
        return targetObjectId;
    }

    public void setTargetObjectId(Long targetObjectId) {
        this.targetObjectId = targetObjectId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
} 
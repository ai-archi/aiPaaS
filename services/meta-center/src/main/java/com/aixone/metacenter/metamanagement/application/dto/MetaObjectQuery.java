package com.aixone.metacenter.metamanagement.application.dto;

import java.util.List;

/**
 * 元数据对象查询条件DTO
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
public class MetaObjectQuery {

    /** 租户ID */
    private String tenantId;

    /** 名称（模糊匹配） */
    private String name;

    /** 描述（模糊匹配） */
    private String description;

    /** 标签 */
    private String tags;

    /** 主类型列表 */
    private List<String> types;

    /** 对象类型列表 */
    private List<String> objectTypes;

    /** 生命周期状态列表 */
    private List<String> lifecycles;

    /** 运行状态列表 */
    private List<String> statuses;

    /** 责任人 */
    private String owner;

    /** 页码（从0开始） */
    private Integer page = 0;

    /** 每页大小 */
    private Integer size = 20;

    /** 排序字段 */
    private String sortBy = "createdAt";

    /** 排序方向 */
    private String sortDirection = "desc";

    /** 显示名称 */
    private String displayName;

    /** 关系类型 */
    private String relationType;

    /** 关系基数 */
    private String cardinality;

    /** 源对象ID */
    private Long sourceObjectId;

    /** 目标对象ID */
    private Long targetObjectId;

    /** 源对象名称 */
    private String sourceObjectName;

    /** 目标对象名称 */
    private String targetObjectName;

    /** 数据类型 */
    private String dataType;

    /** 是否必填 */
    private Boolean required;

    /** 元数据对象ID */
    private Long metaObjectId;

    /** 元数据对象名称 */
    private String metaObjectName;

    // Getter and Setter methods
    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public List<String> getObjectTypes() {
        return objectTypes;
    }

    public void setObjectTypes(List<String> objectTypes) {
        this.objectTypes = objectTypes;
    }

    public List<String> getLifecycles() {
        return lifecycles;
    }

    public void setLifecycles(List<String> lifecycles) {
        this.lifecycles = lifecycles;
    }

    public List<String> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<String> statuses) {
        this.statuses = statuses;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public String getCardinality() {
        return cardinality;
    }

    public void setCardinality(String cardinality) {
        this.cardinality = cardinality;
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

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Long getMetaObjectId() {
        return metaObjectId;
    }

    public void setMetaObjectId(Long metaObjectId) {
        this.metaObjectId = metaObjectId;
    }

    public String getMetaObjectName() {
        return metaObjectName;
    }

    public void setMetaObjectName(String metaObjectName) {
        this.metaObjectName = metaObjectName;
    }
} 
package com.aixone.metacenter.dataservice.application.dto;

import java.util.List;

/**
 * 数据实例查询条件DTO
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
public class DataInstanceQuery {

    /** 租户ID */
    private String tenantId;

    /** 实例名称（模糊匹配） */
    private String name;

    /** 显示名称（模糊匹配） */
    private String displayName;

    /** 描述（模糊匹配） */
    private String description;

    /** 元数据对象ID */
    private Long metaObjectId;

    /** 元数据对象名称（模糊匹配） */
    private String metaObjectName;

    /** 实例状态列表 */
    private List<String> statuses;

    /** 实例类型列表 */
    private List<String> instanceTypes;

    /** 数据源类型列表 */
    private List<String> dataSourceTypes;

    /** 页码（从0开始） */
    private Integer page = 0;

    /** 每页大小 */
    private Integer size = 20;

    /** 排序字段 */
    private String sortBy = "createdTime";

    /** 排序方向 */
    private String sortDirection = "desc";

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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public List<String> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<String> statuses) {
        this.statuses = statuses;
    }

    public List<String> getInstanceTypes() {
        return instanceTypes;
    }

    public void setInstanceTypes(List<String> instanceTypes) {
        this.instanceTypes = instanceTypes;
    }

    public List<String> getDataSourceTypes() {
        return dataSourceTypes;
    }

    public void setDataSourceTypes(List<String> dataSourceTypes) {
        this.dataSourceTypes = dataSourceTypes;
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
} 
package com.aixone.metacenter.dataservice.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据实例DTO
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
public class DataInstanceDTO {

    /** 唯一标识 */
    private Long id;

    /** 实例名称 */
    private String name;

    /** 显示名称 */
    private String displayName;

    /** 实例描述 */
    private String description;

    /** 元数据对象ID */
    private Long metaObjectId;

    /** 元数据对象名称 */
    private String metaObjectName;

    /** 租户ID */
    private String tenantId;

    /** 实例状态 */
    private String status;

    /** 实例类型 */
    private String instanceType;

    /** 数据源类型 */
    private String dataSourceType;

    /** 数据源配置 */
    private String dataSourceConfig;

    /** 数据内容 */
    private String dataContent;

    /** 扩展字段 */
    private Map<String, Object> extensionFields = new HashMap<>();

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

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

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public String getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(String dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public String getDataSourceConfig() {
        return dataSourceConfig;
    }

    public void setDataSourceConfig(String dataSourceConfig) {
        this.dataSourceConfig = dataSourceConfig;
    }

    public String getDataContent() {
        return dataContent;
    }

    public void setDataContent(String dataContent) {
        this.dataContent = dataContent;
    }

    public Map<String, Object> getExtensionFields() {
        return extensionFields;
    }

    public void setExtensionFields(Map<String, Object> extensionFields) {
        this.extensionFields = extensionFields;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }
} 
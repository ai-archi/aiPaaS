package com.aixone.metacenter.metamanagement.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 元数据属性DTO
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
public class MetaAttributeDTO {

    /** 唯一标识 */
    private Long id;

    /** 属性名 */
    private String name;

    /** 显示名称 */
    private String displayName;

    /** 显示标签 */
    private String label;

    /** 数据类型 */
    private String dataType;

    /** 字段长度 */
    private Integer length;

    /** 精度 */
    private Integer precision;

    /** 小数位数 */
    private Integer scale;

    /** 是否必填 */
    private Boolean required;

    /** 默认值 */
    private Object defaultValue;

    /** 约束条件 */
    private String constraints;

    /** 字典/枚举引用 */
    private String enumRef;

    /** 校验规则 */
    private Map<String, Object> validationRules = new HashMap<>();

    /** UI属性 */
    private Map<String, Object> ui = new HashMap<>();

    /** 安全属性 */
    private Map<String, Object> security = new HashMap<>();

    /** 质量属性 */
    private Map<String, Object> quality = new HashMap<>();

    /** 多语言支持 */
    private Map<String, String> i18n = new HashMap<>();

    /** 扩展字段 */
    private Map<String, Object> extensionFields = new HashMap<>();

    /** 排序 */
    private Integer sortOrder;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /** 关联的元数据对象ID */
    private Long metaObjectId;

    /** 描述 */
    private String description;

    /** 类型 */
    private String type;

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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getPrecision() {
        return precision;
    }

    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getConstraints() {
        return constraints;
    }

    public void setConstraints(String constraints) {
        this.constraints = constraints;
    }

    public String getEnumRef() {
        return enumRef;
    }

    public void setEnumRef(String enumRef) {
        this.enumRef = enumRef;
    }

    public Map<String, Object> getValidationRules() {
        return validationRules;
    }

    public void setValidationRules(Map<String, Object> validationRules) {
        this.validationRules = validationRules;
    }

    public Map<String, Object> getUi() {
        return ui;
    }

    public void setUi(Map<String, Object> ui) {
        this.ui = ui;
    }

    public Map<String, Object> getSecurity() {
        return security;
    }

    public void setSecurity(Map<String, Object> security) {
        this.security = security;
    }

    public Map<String, Object> getQuality() {
        return quality;
    }

    public void setQuality(Map<String, Object> quality) {
        this.quality = quality;
    }

    public Map<String, String> getI18n() {
        return i18n;
    }

    public void setI18n(Map<String, String> i18n) {
        this.i18n = i18n;
    }

    public Map<String, Object> getExtensionFields() {
        return extensionFields;
    }

    public void setExtensionFields(Map<String, Object> extensionFields) {
        this.extensionFields = extensionFields;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
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

    public Long getMetaObjectId() {
        return metaObjectId;
    }

    public void setMetaObjectId(Long metaObjectId) {
        this.metaObjectId = metaObjectId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
} 
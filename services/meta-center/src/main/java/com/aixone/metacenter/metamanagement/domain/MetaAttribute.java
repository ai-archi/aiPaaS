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
 * 元数据属性实体
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "meta_attributes", indexes = {
    @Index(name = "idx_meta_attributes_meta_object_id", columnList = "meta_object_id"),
    @Index(name = "idx_meta_attributes_name", columnList = "name")
})
@EntityListeners(AuditingEntityListener.class)
public class MetaAttribute {

    /** 唯一标识 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 属性名 */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /** 显示名称 */
    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    /** 显示标签 */
    @Column(name = "label", nullable = false, length = 100)
    private String label;

    /** 数据类型 */
    @Column(name = "data_type", nullable = false, length = 50)
    private String dataType;

    /** 字段长度 */
    @Column(name = "length")
    private Integer length;

    /** 精度 */
    @Column(name = "precision")
    private Integer precision;

    /** 小数位数 */
    @Column(name = "scale")
    private Integer scale;

    /** 是否必填 */
    @Column(name = "required", nullable = false)
    private Boolean required = false;

    /** 默认值 */
    @Column(name = "default_value", columnDefinition = "jsonb")
    private Object defaultValue;

    /** 约束条件 */
    @Column(name = "constraints", length = 500)
    private String constraints;

    /** 字典/枚举引用 */
    @Column(name = "enum_ref", length = 100)
    private String enumRef;

    /** 校验规则 */
    @Column(name = "validation_rules", columnDefinition = "jsonb")
    private Map<String, Object> validationRules = new HashMap<>();

    /** UI属性 */
    @Column(name = "ui", columnDefinition = "jsonb")
    private Map<String, Object> ui = new HashMap<>();

    /** 安全属性 */
    @Column(name = "security", columnDefinition = "jsonb")
    private Map<String, Object> security = new HashMap<>();

    /** 质量属性 */
    @Column(name = "quality", columnDefinition = "jsonb")
    private Map<String, Object> quality = new HashMap<>();

    /** 多语言支持 */
    @Column(name = "i18n", columnDefinition = "jsonb")
    private Map<String, String> i18n = new HashMap<>();

    /** 扩展字段 */
    @Column(name = "extension_fields", columnDefinition = "jsonb")
    private Map<String, Object> extensionFields = new HashMap<>();

    /** 描述 */
    @Column(name = "description", length = 500)
    private String description;

    /** 创建时间 */
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    /** 更新时间 */
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    /** 排序 */
    @Column(name = "sort_order")
    private Integer sortOrder = 0;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public MetaObject getMetaObject() {
        return metaObject;
    }

    public void setMetaObject(MetaObject metaObject) {
        this.metaObject = metaObject;
    }

    /**
     * 检查是否为必填字段
     * 
     * @return 是否为必填字段
     */
    public boolean isRequired() {
        return Boolean.TRUE.equals(required);
    }

    /**
     * 获取UI控件类型
     * 
     * @return UI控件类型
     */
    public String getUIControlType() {
        return (String) ui.get("controlType");
    }

    /**
     * 设置UI控件类型
     * 
     * @param controlType UI控件类型
     */
    public void setUIControlType(String controlType) {
        ui.put("controlType", controlType);
    }

    /**
     * 获取是否可见
     * 
     * @return 是否可见
     */
    public Boolean isVisible() {
        return (Boolean) ui.getOrDefault("visible", true);
    }

    /**
     * 设置是否可见
     * 
     * @param visible 是否可见
     */
    public void setVisible(Boolean visible) {
        ui.put("visible", visible);
    }

    /**
     * 获取是否脱敏
     * 
     * @return 是否脱敏
     */
    public Boolean isMasking() {
        return (Boolean) security.getOrDefault("masking", false);
    }

    /**
     * 设置是否脱敏
     * 
     * @param masking 是否脱敏
     */
    public void setMasking(Boolean masking) {
        security.put("masking", masking);
    }

    /**
     * 获取访问级别
     * 
     * @return 访问级别
     */
    public String getAccessLevel() {
        return (String) security.get("accessLevel");
    }

    /**
     * 设置访问级别
     * 
     * @param accessLevel 访问级别
     */
    public void setAccessLevel(String accessLevel) {
        security.put("accessLevel", accessLevel);
    }
} 
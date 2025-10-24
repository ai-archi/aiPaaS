package com.aixone.permission.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 资源模型
 * 代表被保护的业务对象，支持ABAC属性扩展
 * 
 * @author aixone
 */
@Data
@EqualsAndHashCode(of = "resourceId")
public class Resource {
    
    /**
     * 资源唯一标识
     */
    private String resourceId;
    
    /**
     * 租户ID
     */
    private String tenantId;
    
    /**
     * 资源类型（如user/order/product等）
     */
    private String type;
    
    /**
     * 资源名称
     */
    private String name;
    
    /**
     * 资源描述
     */
    private String description;
    
    /**
     * 资源属性（如部门、所有者等）
     */
    private Map<String, Object> attributes;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 构造函数
     */
    public Resource() {}
    
    /**
     * 构造函数
     */
    public Resource(String resourceId, String tenantId, String type, String name, Map<String, Object> attributes) {
        this.resourceId = resourceId;
        this.tenantId = tenantId;
        this.type = type;
        this.name = name;
        this.attributes = attributes;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 检查资源是否具有指定属性
     * 
     * @param key 属性键
     * @param value 属性值
     * @return 是否具有该属性
     */
    public boolean hasAttribute(String key, Object value) {
        if (attributes == null) {
            return false;
        }
        Object attrValue = attributes.get(key);
        return value != null && value.equals(attrValue);
    }
    
    /**
     * 获取资源属性值
     * 
     * @param key 属性键
     * @return 属性值
     */
    public Object getAttribute(String key) {
        return attributes != null ? attributes.get(key) : null;
    }
} 
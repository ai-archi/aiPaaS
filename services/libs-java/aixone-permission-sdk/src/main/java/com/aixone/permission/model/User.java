package com.aixone.permission.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户模型
 * 代表系统中的用户，支持ABAC属性扩展
 * 
 * @author aixone
 */
@Data
@EqualsAndHashCode(of = "userId")
public class User {
    
    /**
     * 用户唯一标识
     */
    private String userId;
    
    /**
     * 租户ID
     */
    private String tenantId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 用户属性（如部门、职位等）
     */
    private Map<String, Object> attributes;
    
    /**
     * 用户角色列表
     */
    private List<String> roleIds;
    
    /**
     * 用户权限列表
     */
    private List<Permission> permissions;
    
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
    public User() {}
    
    /**
     * 构造函数
     */
    public User(String userId, String tenantId, String username, Map<String, Object> attributes) {
        this.userId = userId;
        this.tenantId = tenantId;
        this.username = username;
        this.attributes = attributes;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 检查用户是否具有指定属性
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
     * 获取用户属性值
     * 
     * @param key 属性键
     * @return 属性值
     */
    public Object getAttribute(String key) {
        return attributes != null ? attributes.get(key) : null;
    }
} 
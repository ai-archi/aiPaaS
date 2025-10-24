package com.aixone.permission.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * ABAC策略模型
 * 与aixone-tech-auth保持一致
 * 
 * @author aixone
 */
@Data
@EqualsAndHashCode(of = "policyId")
public class Policy {
    
    /**
     * 策略ID
     */
    private String policyId;
    
    /**
     * 租户ID
     */
    private String tenantId;
    
    /**
     * 策略名称
     */
    private String name;
    
    /**
     * 策略描述
     */
    private String description;
    
    /**
     * 资源标识
     */
    private String resource;
    
    /**
     * 操作类型
     */
    private String action;
    
    /**
     * 策略条件表达式
     */
    private String condition;
    
    /**
     * 策略属性
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
    public Policy() {}
    
    /**
     * 构造函数
     */
    public Policy(String policyId, String tenantId, String name, String description, 
                  String resource, String action, String condition, Map<String, Object> attributes) {
        this.policyId = policyId;
        this.tenantId = tenantId;
        this.name = name;
        this.description = description;
        this.resource = resource;
        this.action = action;
        this.condition = condition;
        this.attributes = attributes;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 检查策略是否匹配指定的资源和操作
     * 
     * @param resource 资源标识
     * @param action 操作类型
     * @return 是否匹配
     */
    public boolean matches(String resource, String action) {
        return this.resource.equals(resource) && this.action.equals(action);
    }
} 
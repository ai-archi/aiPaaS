package com.aixone.permission.validator;

import com.aixone.permission.model.User;
import com.aixone.permission.model.Permission;
import com.aixone.permission.model.Resource;
import com.aixone.permission.model.Policy;
import com.aixone.permission.abac.AbacExpressionUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * ABAC权限校验器
 * 基于属性表达式校验权限
 * 
 * @author aixone
 */
@Slf4j
public class AbacValidator implements PermissionValidator {
    
    private final AbacExpressionUtil expressionUtil;
    
    public AbacValidator(AbacExpressionUtil expressionUtil) {
        this.expressionUtil = expressionUtil;
    }
    
    @Override
    public boolean hasPermission(User user, Permission permission, Resource resource) {
        if (user == null || permission == null) {
            log.warn("用户或权限为空，拒绝访问");
            return false;
        }
        
        try {
            // 1. 构建上下文
            Map<String, Object> context = buildContext(user, resource);
            
            // 2. 检查ABAC策略
            return checkAbacPolicies(user.getTenantId(), permission.getResource(), permission.getAction(), context);
            
        } catch (Exception e) {
            log.error("ABAC权限校验异常", e);
            return false;
        }
    }
    
    /**
     * 检查ABAC策略
     * 
     * @param tenantId 租户ID
     * @param resource 资源标识
     * @param action 操作类型
     * @param context 上下文
     * @return 是否通过策略检查
     */
    public boolean checkAbacPolicies(String tenantId, String resource, String action, Map<String, Object> context) {
        try {
            // 获取相关的ABAC策略
            List<Policy> policies = getAbacPolicies(tenantId, resource, action);
            
            if (policies == null || policies.isEmpty()) {
                log.debug("没有找到ABAC策略，默认允许访问");
                return true; // 没有ABAC策略，默认允许
            }
            
            // 检查所有策略，只要有一个通过就允许
            for (Policy policy : policies) {
                if (evaluatePolicy(policy, context)) {
                    log.debug("策略 {} 通过，允许访问", policy.getName());
                    return true;
                }
            }
            
            log.debug("所有ABAC策略都未通过，拒绝访问");
            return false;
            
        } catch (Exception e) {
            log.error("ABAC策略检查异常", e);
            return false;
        }
    }
    
    /**
     * 评估单个策略
     * 
     * @param policy 策略
     * @param context 上下文
     * @return 是否通过
     */
    private boolean evaluatePolicy(Policy policy, Map<String, Object> context) {
        if (policy == null || policy.getCondition() == null || policy.getCondition().trim().isEmpty()) {
            return true; // 没有条件，默认通过
        }
        
        try {
            // 使用表达式工具评估策略
            return expressionUtil.evaluate(policy.getCondition(), context);
            
        } catch (Exception e) {
            log.warn("策略 {} 评估异常: {}", policy.getName(), e.getMessage());
            return false;
        }
    }
    
    /**
     * 构建上下文
     * 
     * @param user 用户
     * @param resource 资源
     * @return 上下文
     */
    private Map<String, Object> buildContext(User user, Resource resource) {
        Map<String, Object> context = new java.util.HashMap<>();
        
        // 用户属性
        if (user.getAttributes() != null) {
            user.getAttributes().forEach((key, value) -> {
                context.put("user." + key, value);
            });
        }
        context.put("user.id", user.getUserId());
        context.put("user.tenantId", user.getTenantId());
        context.put("user.username", user.getUsername());
        
        // 资源属性
        if (resource != null) {
            if (resource.getAttributes() != null) {
                resource.getAttributes().forEach((key, value) -> {
                    context.put("resource." + key, value);
                });
            }
            context.put("resource.id", resource.getResourceId());
            context.put("resource.tenantId", resource.getTenantId());
            context.put("resource.type", resource.getType());
            context.put("resource.name", resource.getName());
        }
        
        // 环境属性
        context.put("time", java.time.LocalTime.now().toString());
        context.put("date", java.time.LocalDate.now().toString());
        context.put("timestamp", System.currentTimeMillis());
        
        return context;
    }
    
    /**
     * 获取ABAC策略
     * 子类需要实现此方法
     * 
     * @param tenantId 租户ID
     * @param resource 资源标识
     * @param action 操作类型
     * @return 策略列表
     */
    protected List<Policy> getAbacPolicies(String tenantId, String resource, String action) {
        // 默认实现，子类可以重写
        return List.of();
    }
} 
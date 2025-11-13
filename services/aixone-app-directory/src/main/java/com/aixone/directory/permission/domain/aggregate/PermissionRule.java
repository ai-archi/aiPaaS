package com.aixone.directory.permission.domain.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限规则聚合根
 * 权限规则用于管理接口的权限验证规则（路径-权限映射）
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionRule {
    
    private String id;
    private String tenantId;
    private String pattern;  // 路径模式，支持Ant路径匹配（**、*）
    private List<String> methods;  // HTTP方法数组（GET、POST、PUT、DELETE等）
    private String permission;  // 权限标识，格式：{resource}:{action} 或 admin:{resource}:{action}
    private String description;
    private Boolean enabled;
    private Integer priority;  // 优先级，数字越大优先级越高
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * 创建权限规则
     */
    public static PermissionRule create(String tenantId, String pattern, List<String> methods, String permission) {
        return PermissionRule.builder()
                .tenantId(tenantId)
                .pattern(pattern)
                .methods(methods)
                .permission(permission)
                .enabled(true)
                .priority(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 更新权限规则
     */
    public void update(String pattern, List<String> methods, String permission, String description, Boolean enabled, Integer priority) {
        this.pattern = pattern;
        this.methods = methods;
        this.permission = permission;
        this.description = description;
        this.enabled = enabled != null ? enabled : this.enabled;
        this.priority = priority != null ? priority : this.priority;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 启用权限规则
     */
    public void enable() {
        this.enabled = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 禁用权限规则
     */
    public void disable() {
        this.enabled = false;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 检查是否属于指定租户
     */
    public boolean belongsToTenant(String tenantId) {
        return this.tenantId != null && this.tenantId.equals(tenantId);
    }
    
    /**
     * 检查是否匹配指定的路径和方法
     */
    public boolean matches(String path, String method) {
        if (!enabled) {
            return false;
        }
        
        // 检查HTTP方法是否匹配
        if (methods != null && !methods.isEmpty() && !methods.contains(method)) {
            return false;
        }
        
        // 检查路径是否匹配（使用Ant路径匹配）
        return matchesPattern(path, pattern);
    }
    
    /**
     * Ant路径匹配
     * 支持 * 和 ** 通配符
     */
    private boolean matchesPattern(String path, String pattern) {
        if (path == null || pattern == null) {
            return false;
        }
        
        // 完全匹配
        if (path.equals(pattern)) {
            return true;
        }
        
        // 转换为正则表达式
        String regex = pattern
                .replace(".", "\\.")
                .replace("**", ".*")
                .replace("*", "[^/]*");
        
        return path.matches(regex);
    }
}


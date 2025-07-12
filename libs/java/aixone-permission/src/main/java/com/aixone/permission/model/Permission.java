package com.aixone.permission.model;

import lombok.Data;

/**
 * 权限实体
 * 
 * @author aixone
 */
@Data
public class Permission {
    
    /**
     * 权限ID
     */
    private String permissionId;
    
    /**
     * 资源标识
     */
    private String resource;
    
    /**
     * 操作类型
     */
    private String action;
    
    /**
     * 权限描述
     */
    private String description;
    
    /**
     * 权限级别
     */
    private PermissionLevel level;
    
    /**
     * 检查权限是否匹配指定的资源和操作
     * 
     * @param resource 资源标识
     * @param action 操作类型
     * @return 是否匹配
     */
    public boolean matches(String resource, String action) {
        return this.resource.equals(resource) && this.action.equals(action);
    }
    
    /**
     * 检查当前权限是否是另一个权限的子集
     * 
     * @param other 另一个权限
     * @return 是否是子集
     */
    public boolean isSubsetOf(Permission other) {
        return this.resource.equals(other.resource) && 
               this.action.equals(other.action) &&
               this.level.ordinal() <= other.level.ordinal();
    }
    
    /**
     * 权限级别枚举
     */
    public enum PermissionLevel {
        READ,   // 读取权限
        WRITE,  // 写入权限
        DELETE, // 删除权限
        ADMIN   // 管理权限
    }
} 
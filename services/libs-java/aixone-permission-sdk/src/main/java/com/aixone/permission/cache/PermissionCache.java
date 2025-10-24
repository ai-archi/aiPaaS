package com.aixone.permission.cache;

import com.aixone.permission.model.User;
import com.aixone.permission.model.Permission;
import com.aixone.permission.model.Role;
import com.aixone.permission.model.Policy;

import java.util.List;
import java.util.Map;

/**
 * 权限缓存接口
 * 
 * @author aixone
 */
public interface PermissionCache {
    
    // ==================== 用户权限缓存 ====================
    
    /**
     * 获取用户权限
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    List<Permission> getUserPermissions(String userId);
    
    /**
     * 缓存用户权限
     * 
     * @param userId 用户ID
     * @param permissions 权限列表
     */
    void putUserPermissions(String userId, List<Permission> permissions);
    
    // ==================== 用户角色缓存 ====================
    
    /**
     * 获取用户角色
     * 
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Role> getUserRoles(String userId);
    
    /**
     * 缓存用户角色
     * 
     * @param userId 用户ID
     * @param roles 角色列表
     */
    void putUserRoles(String userId, List<Role> roles);
    
    // ==================== 用户缓存 ====================
    
    /**
     * 获取用户信息
     * 
     * @param userId 用户ID
     * @return 用户信息
     */
    User getUser(String userId);
    
    /**
     * 缓存用户信息
     * 
     * @param userId 用户ID
     * @param user 用户信息
     */
    void putUser(String userId, User user);
    
    // ==================== 权限缓存 ====================
    
    /**
     * 获取权限信息
     * 
     * @param permissionId 权限ID
     * @return 权限信息
     */
    Permission getPermission(String permissionId);
    
    /**
     * 缓存权限信息
     * 
     * @param permissionId 权限ID
     * @param permission 权限信息
     */
    void putPermission(String permissionId, Permission permission);
    
    // ==================== 角色缓存 ====================
    
    /**
     * 获取角色信息
     * 
     * @param roleId 角色ID
     * @return 角色信息
     */
    Role getRole(String roleId);
    
    /**
     * 缓存角色信息
     * 
     * @param roleId 角色ID
     * @param role 角色信息
     */
    void putRole(String roleId, Role role);
    
    /**
     * 获取角色权限
     * 
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<Permission> getRolePermissions(String roleId);
    
    /**
     * 缓存角色权限
     * 
     * @param roleId 角色ID
     * @param permissions 权限列表
     */
    void putRolePermissions(String roleId, List<Permission> permissions);
    
    // ==================== ABAC策略缓存 ====================
    
    /**
     * 获取ABAC策略
     * 
     * @param policyId 策略ID
     * @return 策略信息
     */
    Policy getPolicy(String policyId);
    
    /**
     * 缓存ABAC策略
     * 
     * @param policyId 策略ID
     * @param policy 策略信息
     */
    void putPolicy(String policyId, Policy policy);
    
    /**
     * 获取资源相关的ABAC策略
     * 
     * @param key 缓存键（格式：tenantId:resource:action）
     * @return 策略列表
     */
    List<Policy> getAbacPolicies(String key);
    
    /**
     * 缓存资源相关的ABAC策略
     * 
     * @param key 缓存键（格式：tenantId:resource:action）
     * @param policies 策略列表
     */
    void putAbacPolicies(String key, List<Policy> policies);
    
    // ==================== 缓存管理 ====================
    
    /**
     * 清除用户相关缓存
     * 
     * @param userId 用户ID
     */
    void clearUserCache(String userId);
    
    /**
     * 清除所有缓存
     */
    void clear();
    
    /**
     * 检查缓存是否过期
     * 
     * @param key 缓存键
     * @return 是否过期
     */
    boolean isExpired(String key);
    
    /**
     * 清理过期缓存
     */
    void cleanExpiredCache();
    
    /**
     * 获取缓存统计信息
     * 
     * @return 缓存统计
     */
    Map<String, Object> getCacheStats();
} 
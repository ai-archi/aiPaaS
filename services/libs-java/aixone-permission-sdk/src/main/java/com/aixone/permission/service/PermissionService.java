package com.aixone.permission.service;

import java.util.List;

import com.aixone.permission.model.Permission;
import com.aixone.permission.model.Policy;
import com.aixone.permission.model.Resource;
import com.aixone.permission.model.Role;
import com.aixone.permission.model.User;

/**
 * 权限服务接口
 * 
 * @author aixone
 */
public interface PermissionService {
    
    // ==================== 权限检查 ====================
    
    /**
     * 检查用户是否有指定权限
     * 
     * @param user 用户
     * @param permission 权限
     * @param resource 资源
     * @return 是否有权限
     */
    boolean hasPermission(User user, Permission permission, Resource resource);
    
    /**
     * 检查用户是否有指定权限
     * 
     * @param userId 用户ID
     * @param resource 资源标识
     * @param action 操作类型
     * @return 是否有权限
     */
    boolean hasPermission(String userId, String resource, String action);
    
    /**
     * 检查用户是否有指定角色
     * 
     * @param userId 用户ID
     * @param roleName 角色名称
     * @return 是否有角色
     */
    boolean hasRole(String userId, String roleName);
    
    /**
     * 检查用户是否有指定角色
     * 
     * @param user 用户
     * @param roleName 角色名称
     * @return 是否有角色
     */
    boolean hasRole(User user, String roleName);
    
    // ==================== 用户权限管理 ====================
    
    /**
     * 获取用户的所有权限
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    List<Permission> getUserPermissions(String userId);
    
    /**
     * 获取用户的所有角色
     * 
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Role> getUserRoles(String userId);
    
    /**
     * 为用户分配角色
     * 
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 是否成功
     */
    boolean assignRole(String userId, String roleId);
    
    /**
     * 移除用户角色
     * 
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 是否成功
     */
    boolean removeRole(String userId, String roleId);
    
    // ==================== 角色权限管理 ====================
    
    /**
     * 为角色分配权限
     * 
     * @param roleId 角色ID
     * @param permissionId 权限ID
     * @return 是否成功
     */
    boolean assignPermission(String roleId, String permissionId);
    
    /**
     * 移除角色权限
     * 
     * @param roleId 角色ID
     * @param permissionId 权限ID
     * @return 是否成功
     */
    boolean removePermission(String roleId, String permissionId);
    
    /**
     * 获取角色的所有权限
     * 
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<Permission> getRolePermissions(String roleId);
    
    // ==================== 权限管理 ====================
    
    /**
     * 创建权限
     * 
     * @param permission 权限
     * @return 是否成功
     */
    boolean createPermission(Permission permission);
    
    /**
     * 更新权限
     * 
     * @param permission 权限
     * @return 是否成功
     */
    boolean updatePermission(Permission permission);
    
    /**
     * 删除权限
     * 
     * @param permissionId 权限ID
     * @return 是否成功
     */
    boolean deletePermission(String permissionId);
    
    /**
     * 获取权限
     * 
     * @param permissionId 权限ID
     * @return 权限
     */
    Permission getPermission(String permissionId);
    
    // ==================== 角色管理 ====================
    
    /**
     * 创建角色
     * 
     * @param role 角色
     * @return 是否成功
     */
    boolean createRole(Role role);
    
    /**
     * 更新角色
     * 
     * @param role 角色
     * @return 是否成功
     */
    boolean updateRole(Role role);
    
    /**
     * 删除角色
     * 
     * @param roleId 角色ID
     * @return 是否成功
     */
    boolean deleteRole(String roleId);
    
    /**
     * 获取角色
     * 
     * @param roleId 角色ID
     * @return 角色
     */
    Role getRole(String roleId);
    
    // ==================== ABAC策略管理 ====================
    
    /**
     * 创建ABAC策略
     * 
     * @param policy 策略
     * @return 是否成功
     */
    boolean createPolicy(Policy policy);
    
    /**
     * 更新ABAC策略
     * 
     * @param policy 策略
     * @return 是否成功
     */
    boolean updatePolicy(Policy policy);
    
    /**
     * 删除ABAC策略
     * 
     * @param policyId 策略ID
     * @return 是否成功
     */
    boolean deletePolicy(String policyId);
    
    /**
     * 获取ABAC策略
     * 
     * @param policyId 策略ID
     * @return 策略
     */
    Policy getPolicy(String policyId);
    
    /**
     * 获取资源相关的ABAC策略
     * 
     * @param tenantId 租户ID
     * @param resource 资源标识
     * @param action 操作类型
     * @return 策略列表
     */
    List<Policy> getAbacPolicies(String tenantId, String resource, String action);
}

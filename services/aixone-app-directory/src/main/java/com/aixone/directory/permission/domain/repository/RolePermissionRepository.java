package com.aixone.directory.permission.domain.repository;

import com.aixone.directory.permission.domain.aggregate.Permission;

import java.util.List;

/**
 * 角色权限关系仓储接口
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
public interface RolePermissionRepository {
    
    /**
     * 分配权限给角色
     */
    void assignPermission(String roleId, String permissionId, String tenantId);
    
    /**
     * 移除角色的权限
     */
    void removePermission(String roleId, String permissionId);
    
    /**
     * 获取角色的权限列表
     */
    List<Permission> findPermissionsByRoleId(String roleId, String tenantId);
    
    /**
     * 获取角色的权限ID列表
     */
    List<String> findPermissionIdsByRoleId(String roleId, String tenantId);
    
    /**
     * 批量分配权限给角色
     */
    void assignPermissions(String roleId, List<String> permissionIds, String tenantId);
    
    /**
     * 批量移除角色的权限
     */
    void removePermissions(String roleId, List<String> permissionIds);
    
    /**
     * 移除角色的所有权限
     */
    void removeAllPermissions(String roleId, String tenantId);
    
    /**
     * 检查角色是否拥有权限
     */
    boolean hasPermission(String roleId, String permissionId);
}


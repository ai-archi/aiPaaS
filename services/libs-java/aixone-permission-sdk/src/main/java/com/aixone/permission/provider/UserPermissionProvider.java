package com.aixone.permission.provider;

import com.aixone.permission.model.User;
import com.aixone.permission.model.Permission;
import com.aixone.permission.model.Role;
import java.util.List;

/**
 * 用户权限提供者扩展点
 * 由业务方实现，返回指定用户的权限列表
 */
public interface UserPermissionProvider {
    /**
     * 获取指定用户的权限列表
     * @param user 用户对象
     * @return 权限列表
     */
    List<Permission> getPermissions(User user);
    
    /**
     * 根据用户ID获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    User getUser(String userId);
    
    /**
     * 根据用户ID获取用户权限列表
     * @param userId 用户ID
     * @return 权限列表
     */
    List<Permission> getUserPermissions(String userId);
    
    /**
     * 根据用户ID和租户ID获取用户权限列表
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @return 权限列表
     */
    List<Permission> getUserPermissions(String userId, String tenantId);
    
    /**
     * 根据用户ID获取用户角色列表
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Role> getUserRoles(String userId);
    
    /**
     * 根据用户ID和租户ID获取用户角色列表
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @return 角色列表
     */
    List<Role> getUserRoles(String userId, String tenantId);
} 
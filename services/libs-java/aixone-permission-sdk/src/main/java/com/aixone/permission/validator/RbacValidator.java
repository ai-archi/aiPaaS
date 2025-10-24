package com.aixone.permission.validator;

import com.aixone.permission.model.User;
import com.aixone.permission.model.Permission;
import com.aixone.permission.model.Resource;
import com.aixone.permission.model.Role;
import com.aixone.permission.provider.UserPermissionProvider;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * RBAC权限校验器
 * 基于角色权限校验
 * 
 * @author aixone
 */
@Slf4j
public class RbacValidator implements PermissionValidator {
    
    private final UserPermissionProvider userPermissionProvider;
    
    public RbacValidator(UserPermissionProvider userPermissionProvider) {
        this.userPermissionProvider = userPermissionProvider;
    }
    
    @Override
    public boolean hasPermission(User user, Permission permission, Resource resource) {
        if (user == null || permission == null) {
            log.warn("用户或权限为空，拒绝访问");
            return false;
        }
        
        try {
            // 1. 获取用户权限列表
            List<Permission> userPermissions = userPermissionProvider.getUserPermissions(
                user.getTenantId(), user.getUserId());
            
            if (userPermissions == null || userPermissions.isEmpty()) {
                log.debug("用户 {} 没有权限", user.getUserId());
                return false;
            }
            
            // 2. 检查是否有匹配的权限
            boolean hasPermission = userPermissions.stream()
                .anyMatch(userPerm -> userPerm.matches(permission.getResource(), permission.getAction()));
            
            if (hasPermission) {
                log.debug("用户 {} 具有权限 {}.{}", user.getUserId(), permission.getResource(), permission.getAction());
            } else {
                log.debug("用户 {} 没有权限 {}.{}", user.getUserId(), permission.getResource(), permission.getAction());
            }
            
            return hasPermission;
            
        } catch (Exception e) {
            log.error("RBAC权限校验异常", e);
            return false;
        }
    }
    
    /**
     * 检查用户是否具有指定资源和操作的权限
     * 
     * @param user 用户
     * @param resource 资源标识
     * @param action 操作类型
     * @return 是否有权限
     */
    public boolean hasPermission(User user, String resource, String action) {
        if (user == null || resource == null || action == null) {
            return false;
        }
        
        try {
            List<Permission> userPermissions = userPermissionProvider.getUserPermissions(
                user.getTenantId(), user.getUserId());
            
            if (userPermissions == null || userPermissions.isEmpty()) {
                return false;
            }
            
            return userPermissions.stream()
                .anyMatch(permission -> permission.matches(resource, action));
                
        } catch (Exception e) {
            log.error("RBAC权限校验异常", e);
            return false;
        }
    }
    
    /**
     * 检查用户是否具有指定角色的权限
     * 
     * @param user 用户
     * @param roleName 角色名称
     * @return 是否有该角色
     */
    public boolean hasRole(User user, String roleName) {
        if (user == null || roleName == null) {
            return false;
        }
        
        try {
            List<Role> userRoles = userPermissionProvider.getUserRoles(
                user.getTenantId(), user.getUserId());
            
            if (userRoles == null || userRoles.isEmpty()) {
                return false;
            }
            
            return userRoles.stream()
                .anyMatch(role -> roleName.equals(role.getName()));
                
        } catch (Exception e) {
            log.error("角色校验异常", e);
            return false;
        }
    }
} 
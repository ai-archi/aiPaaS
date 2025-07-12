package com.aixone.auth.service;

import com.aixone.auth.permission.model.Permission;
import com.aixone.auth.role.model.Role;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class PermissionServiceImpl implements PermissionService {
    // 本地缓存：用户ID->角色列表
    private final Map<String, List<Role>> userRoleCache = new HashMap<>();
    // 本地缓存：角色ID->权限列表
    private final Map<String, List<Permission>> rolePermissionCache = new HashMap<>();
    // 本地缓存：用户属性、资源属性等（ABAC）
    private final Map<String, Map<String, Object>> userAttributes = new HashMap<>();
    private final Map<String, Map<String, Object>> resourceAttributes = new HashMap<>();
    // 可注入的分布式缓存实现
    public com.aixone.permission.cache.PermissionCache permissionCache = null;

    @Override
    public boolean hasRolePermission(String userId, String resource, String action) {
        if (permissionCache != null) {
            java.util.List<com.aixone.permission.model.Role> roles = permissionCache.getUserRoles(userId);
            for (com.aixone.permission.model.Role role : roles) {
                java.util.List<com.aixone.permission.model.Permission> permissions = permissionCache.getRolePermissions(role.getRoleId());
                for (com.aixone.permission.model.Permission perm : permissions) {
                    if (resource.equals(perm.getResource()) && action.equals(perm.getAction())) {
                        return true;
                    }
                }
            }
            return false;
        }
        List<Role> roles = userRoleCache.getOrDefault(userId, Collections.emptyList());
        for (Role role : roles) {
            List<Permission> permissions = rolePermissionCache.getOrDefault(role.getRoleId(), Collections.emptyList());
            for (Permission perm : permissions) {
                if (resource.equals(perm.getResource()) && action.equals(perm.getAction())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean evaluateAbac(String userId, String resource, String action, Map<String, Object> context) {
        // 简化：假设策略为"用户部门=资源部门"
        Map<String, Object> userAttr = userAttributes.getOrDefault(userId, Collections.emptyMap());
        Map<String, Object> resAttr = resourceAttributes.getOrDefault(resource, Collections.emptyMap());
        String userDept = (String) userAttr.get("department");
        String resDept = (String) resAttr.get("department");
        if (userDept != null && userDept.equals(resDept)) {
            return true;
        }
        // 可扩展：解析context和更复杂的表达式
        return false;
    }

    @Override
    public boolean checkAccess(String userId, String resource, String action, Map<String, Object> context) {
        if (!hasRolePermission(userId, resource, action)) {
            return false;
        }
        if (!evaluateAbac(userId, resource, action, context)) {
            return false;
        }
        return true;
    }

    // TODO: 定时/事件驱动同步主数据，刷新缓存
} 
package com.aixone.permission.cache;

import com.aixone.permission.model.Role;
import com.aixone.permission.model.Permission;
import java.util.*;

public class InMemoryPermissionCache implements PermissionCache {
    private final Map<String, List<Role>> userRoleCache = new HashMap<>();
    private final Map<String, List<Permission>> rolePermissionCache = new HashMap<>();

    @Override
    public List<Role> getUserRoles(String userId) {
        return userRoleCache.getOrDefault(userId, Collections.emptyList());
    }
    @Override
    public List<Permission> getRolePermissions(String roleId) {
        return rolePermissionCache.getOrDefault(roleId, Collections.emptyList());
    }
    @Override
    public void putUserRoles(String userId, List<Role> roles) {
        userRoleCache.put(userId, roles);
    }
    @Override
    public void putRolePermissions(String roleId, List<Permission> permissions) {
        rolePermissionCache.put(roleId, permissions);
    }
    @Override
    public void clear() {
        userRoleCache.clear();
        rolePermissionCache.clear();
    }
} 
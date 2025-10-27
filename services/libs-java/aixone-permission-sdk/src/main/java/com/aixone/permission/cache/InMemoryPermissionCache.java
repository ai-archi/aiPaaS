package com.aixone.permission.cache;

import com.aixone.permission.model.Role;
import com.aixone.permission.model.Permission;
import com.aixone.permission.model.User;
import com.aixone.permission.model.Policy;
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
        userRoleCache.put(userId, roles != null ? roles : Collections.emptyList());
    }
    @Override
    public void putRolePermissions(String roleId, List<Permission> permissions) {
        rolePermissionCache.put(roleId, permissions != null ? permissions : Collections.emptyList());
    }
    @Override
    public void clear() {
        userRoleCache.clear();
        rolePermissionCache.clear();
    }
    
    // 实现其他必需的方法
    @Override
    public List<Permission> getUserPermissions(String userId) {
        return Collections.emptyList();
    }
    
    @Override
    public void putUserPermissions(String userId, List<Permission> permissions) {
        // 简单实现
    }
    
    @Override
    public User getUser(String userId) {
        return null;
    }
    
    @Override
    public void putUser(String userId, User user) {
        // 简单实现
    }
    
    @Override
    public Permission getPermission(String permissionId) {
        return null;
    }
    
    @Override
    public void putPermission(String permissionId, Permission permission) {
        // 简单实现
    }
    
    @Override
    public Role getRole(String roleId) {
        return null;
    }
    
    @Override
    public void putRole(String roleId, Role role) {
        // 简单实现
    }
    
    @Override
    public Policy getPolicy(String policyId) {
        return null;
    }
    
    @Override
    public void putPolicy(String policyId, Policy policy) {
        // 简单实现
    }
    
    @Override
    public List<Policy> getAbacPolicies(String key) {
        return Collections.emptyList();
    }
    
    @Override
    public void putAbacPolicies(String key, List<Policy> policies) {
        // 简单实现
    }
    
    @Override
    public void clearUserCache(String userId) {
        userRoleCache.remove(userId);
    }
    
    @Override
    public boolean isExpired(String key) {
        return false;
    }
    
    @Override
    public void cleanExpiredCache() {
        // 简单实现
    }
    
    @Override
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("userRoleCacheSize", userRoleCache.size());
        stats.put("rolePermissionCacheSize", rolePermissionCache.size());
        return stats;
    }
} 
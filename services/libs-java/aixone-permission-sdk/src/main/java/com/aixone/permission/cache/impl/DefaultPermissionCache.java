package com.aixone.permission.cache.impl;

import com.aixone.permission.model.User;
import com.aixone.permission.model.Permission;
import com.aixone.permission.model.Role;
import com.aixone.permission.model.Policy;
import com.aixone.permission.cache.PermissionCache;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 默认权限缓存实现
 * 
 * @author aixone
 */
@Slf4j
public class DefaultPermissionCache implements PermissionCache {
    
    // 缓存存储
    private final ConcurrentHashMap<String, List<Permission>> userPermissionCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<Role>> userRoleCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, User> userCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Permission> permissionCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Role> roleCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Policy> policyCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<Policy>> abacPolicyCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<Permission>> rolePermissionCache = new ConcurrentHashMap<>();
    
    // 缓存时间戳
    private final ConcurrentHashMap<String, Long> cacheTimestamps = new ConcurrentHashMap<>();
    
    // 读写锁
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    // 缓存配置
    private final long cacheExpireTime = TimeUnit.MINUTES.toMillis(30); // 30分钟过期
    private final long maxCacheSize = 10000; // 最大缓存条目数
    
    // ==================== 用户权限缓存 ====================
    
    @Override
    public List<Permission> getUserPermissions(String userId) {
        lock.readLock().lock();
        try {
            if (isExpired(userId)) {
                return null;
            }
            return userPermissionCache.get(userId);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public void putUserPermissions(String userId, List<Permission> permissions) {
        lock.writeLock().lock();
        try {
            userPermissionCache.put(userId, permissions);
            cacheTimestamps.put(userId, System.currentTimeMillis());
            log.debug("缓存用户权限: userId={}, size={}", userId, permissions != null ? permissions.size() : 0);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    // ==================== 用户角色缓存 ====================
    
    @Override
    public List<Role> getUserRoles(String userId) {
        lock.readLock().lock();
        try {
            if (isExpired(userId)) {
                return null;
            }
            return userRoleCache.get(userId);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public void putUserRoles(String userId, List<Role> roles) {
        lock.writeLock().lock();
        try {
            userRoleCache.put(userId, roles);
            cacheTimestamps.put(userId, System.currentTimeMillis());
            log.debug("缓存用户角色: userId={}, size={}", userId, roles != null ? roles.size() : 0);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    // ==================== 用户缓存 ====================
    
    @Override
    public User getUser(String userId) {
        lock.readLock().lock();
        try {
            if (isExpired(userId)) {
                return null;
            }
            return userCache.get(userId);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public void putUser(String userId, User user) {
        lock.writeLock().lock();
        try {
            userCache.put(userId, user);
            cacheTimestamps.put(userId, System.currentTimeMillis());
            log.debug("缓存用户信息: userId={}", userId);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    // ==================== 权限缓存 ====================
    
    @Override
    public Permission getPermission(String permissionId) {
        lock.readLock().lock();
        try {
            if (isExpired(permissionId)) {
                return null;
            }
            return permissionCache.get(permissionId);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public void putPermission(String permissionId, Permission permission) {
        lock.writeLock().lock();
        try {
            permissionCache.put(permissionId, permission);
            cacheTimestamps.put(permissionId, System.currentTimeMillis());
            log.debug("缓存权限信息: permissionId={}", permissionId);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    // ==================== 角色缓存 ====================
    
    @Override
    public Role getRole(String roleId) {
        lock.readLock().lock();
        try {
            if (isExpired(roleId)) {
                return null;
            }
            return roleCache.get(roleId);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public void putRole(String roleId, Role role) {
        lock.writeLock().lock();
        try {
            roleCache.put(roleId, role);
            cacheTimestamps.put(roleId, System.currentTimeMillis());
            log.debug("缓存角色信息: roleId={}", roleId);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public List<Permission> getRolePermissions(String roleId) {
        lock.readLock().lock();
        try {
            if (isExpired(roleId)) {
                return null;
            }
            return rolePermissionCache.get(roleId);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public void putRolePermissions(String roleId, List<Permission> permissions) {
        lock.writeLock().lock();
        try {
            rolePermissionCache.put(roleId, permissions);
            cacheTimestamps.put(roleId, System.currentTimeMillis());
            log.debug("缓存角色权限: roleId={}, size={}", roleId, permissions != null ? permissions.size() : 0);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    // ==================== ABAC策略缓存 ====================
    
    @Override
    public Policy getPolicy(String policyId) {
        lock.readLock().lock();
        try {
            if (isExpired(policyId)) {
                return null;
            }
            return policyCache.get(policyId);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public void putPolicy(String policyId, Policy policy) {
        lock.writeLock().lock();
        try {
            policyCache.put(policyId, policy);
            cacheTimestamps.put(policyId, System.currentTimeMillis());
            log.debug("缓存ABAC策略: policyId={}", policyId);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public List<Policy> getAbacPolicies(String key) {
        lock.readLock().lock();
        try {
            if (isExpired(key)) {
                return null;
            }
            return abacPolicyCache.get(key);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public void putAbacPolicies(String key, List<Policy> policies) {
        lock.writeLock().lock();
        try {
            abacPolicyCache.put(key, policies);
            cacheTimestamps.put(key, System.currentTimeMillis());
            log.debug("缓存ABAC策略列表: key={}, size={}", key, policies != null ? policies.size() : 0);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    // ==================== 缓存管理 ====================
    
    @Override
    public void clearUserCache(String userId) {
        lock.writeLock().lock();
        try {
            userPermissionCache.remove(userId);
            userRoleCache.remove(userId);
            userCache.remove(userId);
            cacheTimestamps.remove(userId);
            log.debug("清除用户缓存: userId={}", userId);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public void clear() {
        lock.writeLock().lock();
        try {
            userPermissionCache.clear();
            userRoleCache.clear();
            userCache.clear();
            permissionCache.clear();
            roleCache.clear();
            policyCache.clear();
            abacPolicyCache.clear();
            rolePermissionCache.clear();
            cacheTimestamps.clear();
            log.debug("清除所有缓存");
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public boolean isExpired(String key) {
        Long timestamp = cacheTimestamps.get(key);
        if (timestamp == null) {
            return true;
        }
        return System.currentTimeMillis() - timestamp > cacheExpireTime;
    }
    
    @Override
    public void cleanExpiredCache() {
        lock.writeLock().lock();
        try {
            long currentTime = System.currentTimeMillis();
            cacheTimestamps.entrySet().removeIf(entry -> {
                boolean expired = currentTime - entry.getValue() > cacheExpireTime;
                if (expired) {
                    String key = entry.getKey();
                    userPermissionCache.remove(key);
                    userRoleCache.remove(key);
                    userCache.remove(key);
                    permissionCache.remove(key);
                    roleCache.remove(key);
                    policyCache.remove(key);
                    abacPolicyCache.remove(key);
                    rolePermissionCache.remove(key);
                }
                return expired;
            });
            log.debug("清理过期缓存完成");
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public Map<String, Object> getCacheStats() {
        lock.readLock().lock();
        try {
            Map<String, Object> stats = new ConcurrentHashMap<>();
            stats.put("userPermissionCacheSize", userPermissionCache.size());
            stats.put("userRoleCacheSize", userRoleCache.size());
            stats.put("userCacheSize", userCache.size());
            stats.put("permissionCacheSize", permissionCache.size());
            stats.put("roleCacheSize", roleCache.size());
            stats.put("policyCacheSize", policyCache.size());
            stats.put("abacPolicyCacheSize", abacPolicyCache.size());
            stats.put("rolePermissionCacheSize", rolePermissionCache.size());
            stats.put("totalCacheSize", cacheTimestamps.size());
            return stats;
        } finally {
            lock.readLock().unlock();
        }
    }
}

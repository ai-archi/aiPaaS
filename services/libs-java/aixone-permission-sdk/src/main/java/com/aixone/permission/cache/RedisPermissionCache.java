package com.aixone.permission.cache;

import com.aixone.permission.model.Role;
import com.aixone.permission.model.Permission;
import com.aixone.permission.model.User;
import com.aixone.permission.model.Policy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import java.util.*;

/**
 * 基于Redis的分布式权限缓存实现
 */
public class RedisPermissionCache implements PermissionCache {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ValueOperations<String, Object> ops;
    private static final String USER_ROLE_KEY_PREFIX = "perm:user_roles:";
    private static final String ROLE_PERM_KEY_PREFIX = "perm:role_perms:";
    private static final String USER_PERM_KEY_PREFIX = "perm:user_perms:";
    private static final String USER_KEY_PREFIX = "perm:user:";
    private static final String PERM_KEY_PREFIX = "perm:permission:";
    private static final String ROLE_KEY_PREFIX = "perm:role:";
    private static final String POLICY_KEY_PREFIX = "perm:policy:";
    private static final String ABAC_POLICIES_KEY_PREFIX = "perm:abac_policies:";

    public RedisPermissionCache(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.ops = redisTemplate.opsForValue();
    }

    @Override
    public List<Role> getUserRoles(String userId) {
        Object val = ops.get(USER_ROLE_KEY_PREFIX + userId);
        if (val instanceof List) {
            return (List<Role>) val;
        }
        return Collections.emptyList();
    }

    @Override
    public List<Permission> getRolePermissions(String roleId) {
        Object val = ops.get(ROLE_PERM_KEY_PREFIX + roleId);
        if (val instanceof List) {
            return (List<Permission>) val;
        }
        return Collections.emptyList();
    }

    @Override
    public void putUserRoles(String userId, List<Role> roles) {
        ops.set(USER_ROLE_KEY_PREFIX + userId, roles);
    }

    @Override
    public void putRolePermissions(String roleId, List<Permission> permissions) {
        ops.set(ROLE_PERM_KEY_PREFIX + roleId, permissions);
    }

    @Override
    public void clear() {
        // 实际生产环境建议用scan+del批量清理
        // 这里只做占位
    }
    
    // 实现其他必需的方法
    @Override
    public List<Permission> getUserPermissions(String userId) {
        Object val = ops.get(USER_PERM_KEY_PREFIX + userId);
        if (val instanceof List) {
            return (List<Permission>) val;
        }
        return Collections.emptyList();
    }
    
    @Override
    public void putUserPermissions(String userId, List<Permission> permissions) {
        ops.set(USER_PERM_KEY_PREFIX + userId, permissions);
    }
    
    @Override
    public User getUser(String userId) {
        Object val = ops.get(USER_KEY_PREFIX + userId);
        if (val instanceof User) {
            return (User) val;
        }
        return null;
    }
    
    @Override
    public void putUser(String userId, User user) {
        ops.set(USER_KEY_PREFIX + userId, user);
    }
    
    @Override
    public Permission getPermission(String permissionId) {
        Object val = ops.get(PERM_KEY_PREFIX + permissionId);
        if (val instanceof Permission) {
            return (Permission) val;
        }
        return null;
    }
    
    @Override
    public void putPermission(String permissionId, Permission permission) {
        ops.set(PERM_KEY_PREFIX + permissionId, permission);
    }
    
    @Override
    public Role getRole(String roleId) {
        Object val = ops.get(ROLE_KEY_PREFIX + roleId);
        if (val instanceof Role) {
            return (Role) val;
        }
        return null;
    }
    
    @Override
    public void putRole(String roleId, Role role) {
        ops.set(ROLE_KEY_PREFIX + roleId, role);
    }
    
    @Override
    public Policy getPolicy(String policyId) {
        Object val = ops.get(POLICY_KEY_PREFIX + policyId);
        if (val instanceof Policy) {
            return (Policy) val;
        }
        return null;
    }
    
    @Override
    public void putPolicy(String policyId, Policy policy) {
        ops.set(POLICY_KEY_PREFIX + policyId, policy);
    }
    
    @Override
    public List<Policy> getAbacPolicies(String key) {
        Object val = ops.get(ABAC_POLICIES_KEY_PREFIX + key);
        if (val instanceof List) {
            return (List<Policy>) val;
        }
        return Collections.emptyList();
    }
    
    @Override
    public void putAbacPolicies(String key, List<Policy> policies) {
        ops.set(ABAC_POLICIES_KEY_PREFIX + key, policies);
    }
    
    @Override
    public void clearUserCache(String userId) {
        redisTemplate.delete(USER_ROLE_KEY_PREFIX + userId);
        redisTemplate.delete(USER_PERM_KEY_PREFIX + userId);
        redisTemplate.delete(USER_KEY_PREFIX + userId);
    }
    
    @Override
    public boolean isExpired(String key) {
        return !redisTemplate.hasKey(key);
    }
    
    @Override
    public void cleanExpiredCache() {
        // Redis会自动处理过期键，这里不需要手动清理
    }
    
    @Override
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        // 这里可以添加Redis特定的统计信息
        stats.put("redisTemplate", redisTemplate.getClass().getSimpleName());
        return stats;
    }
} 
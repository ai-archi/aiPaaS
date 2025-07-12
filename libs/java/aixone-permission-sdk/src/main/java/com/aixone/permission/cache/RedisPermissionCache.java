package com.aixone.permission.cache;

import com.aixone.permission.model.Role;
import com.aixone.permission.model.Permission;
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
} 
package com.aixone.auth.service;

import com.aixone.auth.service.PermissionService;
import com.aixone.auth.service.PermissionServiceImpl;
import com.aixone.permission.cache.DistributedPermissionCache;
import com.aixone.permission.cache.PermissionCache;
import com.aixone.permission.model.Permission;
import com.aixone.permission.model.Role;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.util.*;

/**
 * 权限服务适配器，负责主数据同步、权限获取、校验等
 */
@Service
public class PermissionServiceAdapter {
    private final PermissionCache permissionCache = new DistributedPermissionCache();
    private final PermissionService permissionService;

    public PermissionServiceAdapter() {
        this.permissionService = new PermissionServiceImpl();
        // 手动注入缓存实现
        ((PermissionServiceImpl)permissionService).permissionCache = this.permissionCache;
    }

    // 模拟主数据同步（实际应从directory-serve拉取并定时/事件驱动刷新）
    @PostConstruct
    public void init() {
        // 示例：初始化用户-角色-权限关系
        Permission p1 = new Permission();
        p1.setPermissionId("perm-doc-read");
        p1.setResource("document");
        p1.setAction("read");
        Permission p2 = new Permission();
        p2.setPermissionId("perm-doc-write");
        p2.setResource("document");
        p2.setAction("write");
        Role r1 = new Role();
        r1.setRoleId("role-admin");
        r1.setName("管理员");
        r1.setPermissions(Arrays.asList(p1, p2));
        Role r2 = new Role();
        r2.setRoleId("role-user");
        r2.setName("普通用户");
        r2.setPermissions(Collections.singletonList(p1));
        // 假设user1是管理员，user2是普通用户
        permissionCache.putUserRoles("user1", Arrays.asList(r1));
        permissionCache.putUserRoles("user2", Arrays.asList(r2));
        permissionCache.putRolePermissions("role-admin", r1.getPermissions());
        permissionCache.putRolePermissions("role-user", r2.getPermissions());
    }

    /**
     * 权限校验（RBAC+ABAC）
     */
    public boolean checkAccess(String userId, String resource, String action, Map<String, Object> context) {
        return permissionService.checkAccess(userId, resource, action, context);
    }

    /**
     * 获取用户所有角色
     */
    public List<Role> getUserRoles(String userId) {
        return permissionCache.getUserRoles(userId);
    }

    /**
     * 获取角色所有权限
     */
    public List<Permission> getRolePermissions(String roleId) {
        return permissionCache.getRolePermissions(roleId);
    }
} 
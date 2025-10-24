package com.aixone.permission.provider;

import com.aixone.permission.model.User;
import com.aixone.permission.model.Permission;
import com.aixone.permission.model.Role;
import java.util.Collections;
import java.util.List;

/**
 * 默认用户权限提供者实现
 * 返回空权限列表，业务方可自定义实现
 */
public class DefaultUserPermissionProvider implements UserPermissionProvider {
    @Override
    public List<Permission> getPermissions(User user) {
        return Collections.emptyList();
    }

    @Override
    public User getUser(String userId) {
        return null;
    }

    @Override
    public List<Permission> getUserPermissions(String userId) {
        return Collections.emptyList();
    }

    @Override
    public List<Permission> getUserPermissions(String userId, String tenantId) {
        return Collections.emptyList();
    }

    @Override
    public List<Role> getUserRoles(String userId) {
        return Collections.emptyList();
    }

    @Override
    public List<Role> getUserRoles(String userId, String tenantId) {
        return Collections.emptyList();
    }
} 
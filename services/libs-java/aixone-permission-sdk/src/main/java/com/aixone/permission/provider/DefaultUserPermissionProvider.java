package com.aixone.permission.provider;

import com.aixone.permission.model.User;
import com.aixone.permission.model.Permission;
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
} 
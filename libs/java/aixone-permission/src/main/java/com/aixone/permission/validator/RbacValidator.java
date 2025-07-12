package com.aixone.permission.validator;

import com.aixone.permission.model.User;
import com.aixone.permission.model.Permission;
import com.aixone.permission.model.Resource;

/**
 * RBAC权限校验器
 * 基于角色权限校验
 */
public class RbacValidator implements PermissionValidator {
    @Override
    public boolean hasPermission(User user, Permission permission, Resource resource) {
        throw new UnsupportedOperationException("RBAC校验未实现，请自定义实现RbacValidator");
    }
} 
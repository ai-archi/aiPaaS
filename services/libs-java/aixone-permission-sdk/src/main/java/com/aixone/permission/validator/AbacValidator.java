package com.aixone.permission.validator;

import com.aixone.permission.model.User;
import com.aixone.permission.model.Permission;
import com.aixone.permission.model.Resource;

/**
 * ABAC权限校验器
 * 基于属性表达式校验权限
 */
public class AbacValidator implements PermissionValidator {
    @Override
    public boolean hasPermission(User user, Permission permission, Resource resource) {
        throw new UnsupportedOperationException("ABAC表达式解析未实现，请自定义实现AbacValidator");
    }
} 
package com.aixone.permission.validator;

import com.aixone.permission.model.User;
import com.aixone.permission.model.Permission;
import com.aixone.permission.model.Resource;

/**
 * 权限校验器扩展点
 * 负责校验用户是否拥有某权限
 */
public interface PermissionValidator {
    /**
     * 校验用户是否拥有指定权限
     * @param user 用户对象
     * @param permission 权限对象
     * @param resource 资源对象
     * @return 是否有权限
     */
    boolean hasPermission(User user, Permission permission, Resource resource);
} 
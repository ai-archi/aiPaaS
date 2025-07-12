package com.aixone.permission.provider;

import com.aixone.permission.model.User;
import com.aixone.permission.model.Permission;
import java.util.List;

/**
 * 用户权限提供者扩展点
 * 由业务方实现，返回指定用户的权限列表
 */
public interface UserPermissionProvider {
    /**
     * 获取指定用户的权限列表
     * @param user 用户对象
     * @return 权限列表
     */
    List<Permission> getPermissions(User user);
} 
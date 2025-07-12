package com.aixone.permission.cache;

import com.aixone.permission.model.Role;
import com.aixone.permission.model.Permission;
import java.util.List;

public interface PermissionCache {
    List<Role> getUserRoles(String userId);
    List<Permission> getRolePermissions(String roleId);
    void putUserRoles(String userId, List<Role> roles);
    void putRolePermissions(String roleId, List<Permission> permissions);
    void clear();
} 
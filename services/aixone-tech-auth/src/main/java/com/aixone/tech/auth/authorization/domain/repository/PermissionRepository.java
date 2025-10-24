package com.aixone.tech.auth.authorization.domain.repository;

import com.aixone.tech.auth.authorization.domain.model.Permission;
import java.util.List;

/**
 * 权限仓储接口
 */
public interface PermissionRepository {
    Permission save(Permission permission);
    Permission findByTenantIdAndPermissionId(String tenantId, String permissionId);
    List<Permission> findByTenantIdAndPermissionIdIn(String tenantId, List<String> permissionIds);
    List<Permission> findByTenantIdAndResourceAndAction(String tenantId, String resource, String action);
    List<Permission> findByTenantId(String tenantId);
    void deleteByTenantIdAndPermissionId(String tenantId, String permissionId);
    boolean existsByTenantIdAndName(String tenantId, String name);
}

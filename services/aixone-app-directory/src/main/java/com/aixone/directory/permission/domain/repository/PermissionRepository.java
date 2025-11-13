package com.aixone.directory.permission.domain.repository;

import com.aixone.directory.permission.domain.aggregate.Permission;

import java.util.List;
import java.util.Optional;

/**
 * 权限仓储接口
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
public interface PermissionRepository {
    
    /**
     * 保存权限
     */
    Permission save(Permission permission);
    
    /**
     * 根据ID查找权限
     */
    Optional<Permission> findById(String permissionId);
    
    /**
     * 根据租户ID查找所有权限
     */
    List<Permission> findByTenantId(String tenantId);
    
    /**
     * 根据租户ID和编码查找权限
     */
    Optional<Permission> findByTenantIdAndCode(String tenantId, String code);
    
    /**
     * 根据租户ID、资源标识和操作标识查找权限
     */
    Optional<Permission> findByTenantIdAndResourceAndAction(String tenantId, String resource, String action);
    
    /**
     * 删除权限
     */
    void delete(String permissionId);
    
    /**
     * 检查是否存在
     */
    boolean existsById(String permissionId);
    
    /**
     * 检查编码是否存在
     */
    boolean existsByTenantIdAndCode(String tenantId, String code);
}


package com.aixone.metacenter.permissionservice.infrastructure;

import com.aixone.metacenter.permissionservice.domain.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 权限JPA仓储接口
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Repository
public interface PermissionJpaRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {

    /**
     * 根据租户ID查询权限
     * 
     * @param tenantId 租户ID
     * @return 权限列表
     */
    List<Permission> findByTenantId(String tenantId);

    /**
     * 根据状态查询权限
     * 
     * @param status 状态
     * @return 权限列表
     */
    List<Permission> findByStatus(String status);

    /**
     * 根据角色ID查询权限
     * 
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<Permission> findByRoleId(Long roleId);

    /**
     * 根据用户ID查询权限
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    List<Permission> findByUserId(Long userId);

    /**
     * 根据权限名称查询权限
     * 
     * @param name 权限名称
     * @return 权限
     */
    Optional<Permission> findByName(String name);

    /**
     * 根据租户ID和权限名称查询权限
     * 
     * @param tenantId 租户ID
     * @param name 权限名称
     * @return 权限
     */
    Optional<Permission> findByTenantIdAndName(String tenantId, String name);

    /**
     * 根据权限类型查询权限
     * 
     * @param permissionType 权限类型
     * @return 权限列表
     */
    List<Permission> findByPermissionType(String permissionType);

    /**
     * 根据资源查询权限
     * 
     * @param resource 资源
     * @return 权限列表
     */
    List<Permission> findByResource(String resource);

    /**
     * 根据操作查询权限
     * 
     * @param action 操作
     * @return 权限列表
     */
    List<Permission> findByAction(String action);

    /**
     * 根据租户ID和状态查询权限
     * 
     * @param tenantId 租户ID
     * @param status 状态
     * @return 权限列表
     */
    List<Permission> findByTenantIdAndStatus(String tenantId, String status);
} 
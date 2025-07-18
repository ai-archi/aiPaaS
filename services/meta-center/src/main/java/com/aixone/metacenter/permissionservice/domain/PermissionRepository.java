package com.aixone.metacenter.permissionservice.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 权限仓储接口
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    /**
     * 根据租户ID、主体、对象、操作查找权限
     * 
     * @param tenantId 租户ID
     * @param subject 主体
     * @param object 对象
     * @param action 操作
     * @return 权限
     */
    Optional<Permission> findByTenantIdAndSubjectAndObjectAndAction(String tenantId, String subject, String object, String action);

    /**
     * 根据租户ID和主体查找权限列表
     * 
     * @param tenantId 租户ID
     * @param subject 主体
     * @return 权限列表
     */
    List<Permission> findByTenantIdAndSubject(String tenantId, String subject);

    /**
     * 根据租户ID和对象查找权限列表
     * 
     * @param tenantId 租户ID
     * @param object 对象
     * @return 权限列表
     */
    List<Permission> findByTenantIdAndObject(String tenantId, String object);

    /**
     * 根据租户ID查找启用的权限列表
     * 
     * @param tenantId 租户ID
     * @return 启用的权限列表
     */
    List<Permission> findByTenantIdAndEnabledTrue(String tenantId);

    /**
     * 根据租户ID分页查询权限
     * 
     * @param tenantId 租户ID
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<Permission> findByTenantId(String tenantId, Pageable pageable);

    /**
     * 根据租户ID和主体分页查询权限
     * 
     * @param tenantId 租户ID
     * @param subject 主体
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<Permission> findByTenantIdAndSubject(String tenantId, String subject, Pageable pageable);

    /**
     * 根据租户ID和对象分页查询权限
     * 
     * @param tenantId 租户ID
     * @param object 对象
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<Permission> findByTenantIdAndObject(String tenantId, String object, Pageable pageable);

    /**
     * 检查权限是否存在
     * 
     * @param tenantId 租户ID
     * @param subject 主体
     * @param object 对象
     * @param action 操作
     * @return 是否存在
     */
    boolean existsByTenantIdAndSubjectAndObjectAndAction(String tenantId, String subject, String object, String action);

    /**
     * 根据租户ID统计权限数量
     * 
     * @param tenantId 租户ID
     * @return 数量
     */
    long countByTenantId(String tenantId);

    /**
     * 根据租户ID和主体统计权限数量
     * 
     * @param tenantId 租户ID
     * @param subject 主体
     * @return 数量
     */
    long countByTenantIdAndSubject(String tenantId, String subject);
} 
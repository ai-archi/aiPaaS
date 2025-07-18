package com.aixone.metacenter.integrationorchestration.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 集成编排仓储接口
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Repository
public interface IntegrationRepository extends JpaRepository<Integration, Long> {

    /**
     * 根据租户ID和名称查找集成
     * 
     * @param tenantId 租户ID
     * @param name 名称
     * @return 集成
     */
    Optional<Integration> findByTenantIdAndName(String tenantId, String name);

    /**
     * 根据租户ID和类型查找集成列表
     * 
     * @param tenantId 租户ID
     * @param type 类型
     * @return 集成列表
     */
    List<Integration> findByTenantIdAndType(String tenantId, String type);

    /**
     * 根据租户ID和状态查找集成列表
     * 
     * @param tenantId 租户ID
     * @param status 状态
     * @return 集成列表
     */
    List<Integration> findByTenantIdAndStatus(String tenantId, String status);

    /**
     * 根据租户ID分页查询集成
     * 
     * @param tenantId 租户ID
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<Integration> findByTenantId(String tenantId, Pageable pageable);

    /**
     * 根据租户ID和名称模糊查询集成
     * 
     * @param tenantId 租户ID
     * @param name 名称（模糊匹配）
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<Integration> findByTenantIdAndNameContainingIgnoreCase(String tenantId, String name, Pageable pageable);

    /**
     * 检查租户ID和名称是否存在
     * 
     * @param tenantId 租户ID
     * @param name 名称
     * @return 是否存在
     */
    boolean existsByTenantIdAndName(String tenantId, String name);

    /**
     * 根据租户ID统计集成数量
     * 
     * @param tenantId 租户ID
     * @return 数量
     */
    long countByTenantId(String tenantId);

    /**
     * 根据租户ID和类型统计集成数量
     * 
     * @param tenantId 租户ID
     * @param type 类型
     * @return 数量
     */
    long countByTenantIdAndType(String tenantId, String type);
} 
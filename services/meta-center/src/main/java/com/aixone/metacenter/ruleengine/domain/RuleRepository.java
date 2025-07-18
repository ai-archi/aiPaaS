package com.aixone.metacenter.ruleengine.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 规则仓储接口
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Repository
public interface RuleRepository extends JpaRepository<Rule, Long> {

    /**
     * 根据租户ID和名称查找规则
     * 
     * @param tenantId 租户ID
     * @param name 名称
     * @return 规则
     */
    Optional<Rule> findByTenantIdAndName(String tenantId, String name);

    /**
     * 根据租户ID和类型查找规则列表
     * 
     * @param tenantId 租户ID
     * @param type 类型
     * @return 规则列表
     */
    List<Rule> findByTenantIdAndType(String tenantId, String type);

    /**
     * 根据租户ID和状态查找规则列表
     * 
     * @param tenantId 租户ID
     * @param status 状态
     * @return 规则列表
     */
    List<Rule> findByTenantIdAndStatus(String tenantId, String status);

    /**
     * 根据租户ID查找启用的规则列表
     * 
     * @param tenantId 租户ID
     * @return 启用的规则列表
     */
    List<Rule> findByTenantIdAndEnabledTrue(String tenantId);

    /**
     * 根据租户ID查找规则列表
     * 
     * @param tenantId 租户ID
     * @return 规则列表
     */
    List<Rule> findByTenantId(String tenantId);

    /**
     * 根据名称查找规则
     * 
     * @param name 规则名称
     * @return 规则
     */
    Optional<Rule> findByName(String name);

    /**
     * 查找启用的规则列表
     * 
     * @return 启用的规则列表
     */
    List<Rule> findByEnabledTrue();

    /**
     * 根据优先级查找规则列表
     * 
     * @param priority 优先级
     * @return 规则列表
     */
    List<Rule> findByPriority(Integer priority);

    /**
     * 检查规则名称是否存在
     * 
     * @param name 规则名称
     * @return 是否存在
     */
    boolean existsByName(String name);

    /**
     * 根据租户ID分页查询规则
     * 
     * @param tenantId 租户ID
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<Rule> findByTenantId(String tenantId, Pageable pageable);

    /**
     * 根据租户ID和名称模糊查询规则
     * 
     * @param tenantId 租户ID
     * @param name 名称（模糊匹配）
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<Rule> findByTenantIdAndNameContainingIgnoreCase(String tenantId, String name, Pageable pageable);

    /**
     * 检查租户ID和名称是否存在
     * 
     * @param tenantId 租户ID
     * @param name 名称
     * @return 是否存在
     */
    boolean existsByTenantIdAndName(String tenantId, String name);

    /**
     * 根据租户ID统计规则数量
     * 
     * @param tenantId 租户ID
     * @return 数量
     */
    long countByTenantId(String tenantId);
} 
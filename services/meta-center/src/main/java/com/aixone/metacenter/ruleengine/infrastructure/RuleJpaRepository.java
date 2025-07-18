package com.aixone.metacenter.ruleengine.infrastructure;

import com.aixone.metacenter.ruleengine.domain.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 规则JPA仓储接口
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Repository
public interface RuleJpaRepository extends JpaRepository<Rule, Long>, JpaSpecificationExecutor<Rule> {

    /**
     * 根据元数据对象ID查询规则列表
     * 
     * @param metaObjectId 元数据对象ID
     * @return 规则列表
     */
    List<Rule> findByMetaObjectId(Long metaObjectId);

    /**
     * 根据元数据对象ID和规则名称查询规则
     * 
     * @param metaObjectId 元数据对象ID
     * @param name 规则名称
     * @return 规则
     */
    Optional<Rule> findByMetaObjectIdAndName(Long metaObjectId, String name);

    /**
     * 根据租户ID查询规则列表
     * 
     * @param tenantId 租户ID
     * @return 规则列表
     */
    List<Rule> findByTenantId(String tenantId);

    /**
     * 根据租户ID和规则类型查询规则列表
     * 
     * @param tenantId 租户ID
     * @param ruleType 规则类型
     * @return 规则列表
     */
    List<Rule> findByTenantIdAndRuleType(String tenantId, String ruleType);

    /**
     * 根据租户ID和规则名称查询规则列表
     * 
     * @param tenantId 租户ID
     * @param name 规则名称
     * @return 规则列表
     */
    List<Rule> findByTenantIdAndName(String tenantId, String name);

    /**
     * 根据租户ID和启用状态查询规则列表
     * 
     * @param tenantId 租户ID
     * @param enabled 启用状态
     * @return 规则列表
     */
    List<Rule> findByTenantIdAndEnabled(String tenantId, Boolean enabled);

    /**
     * 根据租户ID和优先级查询规则列表
     * 
     * @param tenantId 租户ID
     * @return 规则列表（按优先级排序）
     */
    List<Rule> findByTenantIdOrderByPriority(String tenantId);

    /**
     * 根据元数据对象ID和启用状态查询规则列表
     * 
     * @param metaObjectId 元数据对象ID
     * @param enabled 启用状态
     * @return 规则列表
     */
    List<Rule> findByMetaObjectIdAndEnabled(Long metaObjectId, Boolean enabled);

    /**
     * 根据元数据对象ID和优先级查询规则列表
     * 
     * @param metaObjectId 元数据对象ID
     * @return 规则列表（按优先级排序）
     */
    List<Rule> findByMetaObjectIdOrderByPriority(Long metaObjectId);

    /**
     * 根据租户ID和触发条件查询规则列表
     * 
     * @param tenantId 租户ID
     * @param trigger 触发条件
     * @return 规则列表
     */
    List<Rule> findByTenantIdAndTrigger(String tenantId, String trigger);

    /**
     * 根据元数据对象ID和触发条件查询规则列表
     * 
     * @param metaObjectId 元数据对象ID
     * @param trigger 触发条件
     * @return 规则列表
     */
    List<Rule> findByMetaObjectIdAndTrigger(Long metaObjectId, String trigger);

    /**
     * 根据租户ID和标签查询规则列表
     * 
     * @param tenantId 租户ID
     * @param tags 标签
     * @return 规则列表
     */
    @Query("SELECT r FROM Rule r WHERE r.tenantId = :tenantId AND r.tags LIKE %:tags%")
    List<Rule> findByTenantIdAndTagsContaining(@Param("tenantId") String tenantId, @Param("tags") String tags);

    /**
     * 根据元数据对象ID和标签查询规则列表
     * 
     * @param metaObjectId 元数据对象ID
     * @param tags 标签
     * @return 规则列表
     */
    @Query("SELECT r FROM Rule r WHERE r.metaObject.id = :metaObjectId AND r.tags LIKE %:tags%")
    List<Rule> findByMetaObjectIdAndTagsContaining(@Param("metaObjectId") Long metaObjectId, @Param("tags") String tags);

    /**
     * 检查元数据对象ID和规则名称是否存在
     * 
     * @param metaObjectId 元数据对象ID
     * @param name 规则名称
     * @return 是否存在
     */
    boolean existsByMetaObjectIdAndName(Long metaObjectId, String name);

    /**
     * 根据元数据对象ID统计规则数量
     * 
     * @param metaObjectId 元数据对象ID
     * @return 数量
     */
    long countByMetaObjectId(Long metaObjectId);

    /**
     * 根据租户ID统计规则数量
     * 
     * @param tenantId 租户ID
     * @return 数量
     */
    long countByTenantId(String tenantId);

    /**
     * 根据租户ID和规则类型统计规则数量
     * 
     * @param tenantId 租户ID
     * @param ruleType 规则类型
     * @return 数量
     */
    long countByTenantIdAndRuleType(String tenantId, String ruleType);

    /**
     * 根据租户ID和启用状态统计规则数量
     * 
     * @param tenantId 租户ID
     * @param enabled 启用状态
     * @return 数量
     */
    long countByTenantIdAndEnabled(String tenantId, Boolean enabled);

    /**
     * 根据元数据对象ID删除规则
     * 
     * @param metaObjectId 元数据对象ID
     */
    void deleteByMetaObjectId(Long metaObjectId);

    /**
     * 根据租户ID删除规则
     * 
     * @param tenantId 租户ID
     */
    void deleteByTenantId(String tenantId);
} 
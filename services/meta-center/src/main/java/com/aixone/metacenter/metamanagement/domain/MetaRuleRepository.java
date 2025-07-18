package com.aixone.metacenter.metamanagement.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 元数据规则仓储接口
 * 负责元数据规则的数据访问
 */
@Repository
public interface MetaRuleRepository extends JpaRepository<MetaRule, Long> {

    /**
     * 根据名称查询元数据规则
     *
     * @param name 规则名称
     * @return 元数据规则
     */
    Optional<MetaRule> findByName(String name);

    /**
     * 根据元数据对象ID查询规则列表
     *
     * @param metaObjectId 元数据对象ID
     * @return 规则列表
     */
    List<MetaRule> findByMetaObjectId(Long metaObjectId);

    /**
     * 根据规则类型查询规则列表
     *
     * @param ruleType 规则类型
     * @return 规则列表
     */
    List<MetaRule> findByRuleType(String ruleType);

    /**
     * 根据是否启用查询规则列表
     *
     * @param enabled 是否启用
     * @return 规则列表
     */
    List<MetaRule> findByEnabled(Boolean enabled);

    /**
     * 根据优先级查询规则列表
     *
     * @param priority 优先级
     * @return 规则列表
     */
    List<MetaRule> findByPriority(Integer priority);

    /**
     * 根据元数据对象ID和规则类型查询规则
     *
     * @param metaObjectId 元数据对象ID
     * @param ruleType 规则类型
     * @return 规则列表
     */
    List<MetaRule> findByMetaObjectIdAndRuleType(Long metaObjectId, String ruleType);

    /**
     * 根据元数据对象ID和是否启用查询规则
     *
     * @param metaObjectId 元数据对象ID
     * @param enabled 是否启用
     * @return 规则列表
     */
    List<MetaRule> findByMetaObjectIdAndEnabled(Long metaObjectId, Boolean enabled);

    /**
     * 根据条件分页查询规则
     *
     * @param name 规则名称
     * @param displayName 显示名称
     * @param ruleType 规则类型
     * @param enabled 是否启用
     * @param priority 优先级
     * @param metaObjectId 元数据对象ID
     * @param pageable 分页参数
     * @return 分页结果
     */
    @Query("SELECT mr FROM MetaRule mr " +
           "LEFT JOIN mr.metaObject mo " +
           "WHERE (:name IS NULL OR mr.name LIKE %:name%) " +
           "AND (:displayName IS NULL OR mr.displayName LIKE %:displayName%) " +
           "AND (:ruleType IS NULL OR mr.ruleType = :ruleType) " +
           "AND (:enabled IS NULL OR mr.enabled = :enabled) " +
           "AND (:priority IS NULL OR mr.priority = :priority) " +
           "AND (:metaObjectId IS NULL OR mo.id = :metaObjectId)")
    Page<MetaRule> findByConditions(@Param("name") String name,
                                   @Param("displayName") String displayName,
                                   @Param("ruleType") String ruleType,
                                   @Param("enabled") Boolean enabled,
                                   @Param("priority") Integer priority,
                                   @Param("metaObjectId") Long metaObjectId,
                                   Pageable pageable);

    /**
     * 统计元数据对象的规则数量
     *
     * @param metaObjectId 元数据对象ID
     * @return 规则数量
     */
    long countByMetaObjectId(Long metaObjectId);

    /**
     * 根据元数据对象ID删除规则
     *
     * @param metaObjectId 元数据对象ID
     */
    void deleteByMetaObjectId(Long metaObjectId);

    /**
     * 根据规则类型删除规则
     *
     * @param ruleType 规则类型
     */
    void deleteByRuleType(String ruleType);

    /**
     * 查找启用的规则并按优先级排序
     *
     * @return 启用的规则列表
     */
    List<MetaRule> findByEnabledTrueOrderByPriorityAsc();

    /**
     * 根据元数据对象ID查找启用的规则并按优先级排序
     *
     * @param metaObjectId 元数据对象ID
     * @return 启用的规则列表
     */
    List<MetaRule> findByMetaObjectIdAndEnabledTrueOrderByPriorityAsc(Long metaObjectId);
} 
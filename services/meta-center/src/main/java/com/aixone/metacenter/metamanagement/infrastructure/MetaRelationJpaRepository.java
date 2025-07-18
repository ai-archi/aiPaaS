package com.aixone.metacenter.metamanagement.infrastructure;

import com.aixone.metacenter.metamanagement.domain.MetaRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 元数据关系JPA仓储接口
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Repository
public interface MetaRelationJpaRepository extends JpaRepository<MetaRelation, Long>, JpaSpecificationExecutor<MetaRelation> {

    /**
     * 根据源对象ID查询关系列表
     * 
     * @param sourceObjectId 源对象ID
     * @return 关系列表
     */
    List<MetaRelation> findBySourceObjectId(Long sourceObjectId);

    /**
     * 根据目标对象ID查询关系列表
     * 
     * @param targetObjectId 目标对象ID
     * @return 关系列表
     */
    List<MetaRelation> findByTargetObjectId(Long targetObjectId);

    /**
     * 根据源对象ID和目标对象ID查询关系
     * 
     * @param sourceObjectId 源对象ID
     * @param targetObjectId 目标对象ID
     * @return 关系
     */
    Optional<MetaRelation> findBySourceObjectIdAndTargetObjectId(Long sourceObjectId, Long targetObjectId);

    /**
     * 根据源对象ID和关系名称查询关系
     * 
     * @param sourceObjectId 源对象ID
     * @param name 关系名称
     * @return 关系
     */
    Optional<MetaRelation> findBySourceObjectIdAndName(Long sourceObjectId, String name);

    /**
     * 根据租户ID查询关系列表
     * 
     * @param tenantId 租户ID
     * @return 关系列表
     */
    List<MetaRelation> findByTenantId(String tenantId);

    /**
     * 根据租户ID和关系类型查询关系列表
     * 
     * @param tenantId 租户ID
     * @param relationType 关系类型
     * @return 关系列表
     */
    List<MetaRelation> findByTenantIdAndRelationType(String tenantId, String relationType);

    /**
     * 根据租户ID和关系名称查询关系列表
     * 
     * @param tenantId 租户ID
     * @param name 关系名称
     * @return 关系列表
     */
    List<MetaRelation> findByTenantIdAndName(String tenantId, String name);

    /**
     * 根据租户ID和方向性查询关系列表
     * 
     * @param tenantId 租户ID
     * @param direction 方向性
     * @return 关系列表
     */
    List<MetaRelation> findByTenantIdAndDirection(String tenantId, String direction);

    /**
     * 根据租户ID和可导航性查询关系列表
     * 
     * @param tenantId 租户ID
     * @param navigable 可导航性
     * @return 关系列表
     */
    List<MetaRelation> findByTenantIdAndNavigable(String tenantId, Boolean navigable);

    /**
     * 根据源对象ID和关系类型查询关系列表
     * 
     * @param sourceObjectId 源对象ID
     * @param relationType 关系类型
     * @return 关系列表
     */
    List<MetaRelation> findBySourceObjectIdAndRelationType(Long sourceObjectId, String relationType);

    /**
     * 根据目标对象ID和关系类型查询关系列表
     * 
     * @param targetObjectId 目标对象ID
     * @param relationType 关系类型
     * @return 关系列表
     */
    List<MetaRelation> findByTargetObjectIdAndRelationType(Long targetObjectId, String relationType);

    /**
     * 根据源对象ID和可导航性查询关系列表
     * 
     * @param sourceObjectId 源对象ID
     * @param navigable 可导航性
     * @return 关系列表
     */
    List<MetaRelation> findBySourceObjectIdAndNavigable(Long sourceObjectId, Boolean navigable);

    /**
     * 根据目标对象ID和可导航性查询关系列表
     * 
     * @param targetObjectId 目标对象ID
     * @param navigable 可导航性
     * @return 关系列表
     */
    List<MetaRelation> findByTargetObjectIdAndNavigable(Long targetObjectId, Boolean navigable);

    /**
     * 根据租户ID和标签查询关系列表
     * 
     * @param tenantId 租户ID
     * @param tags 标签
     * @return 关系列表
     */
    @Query("SELECT mr FROM MetaRelation mr WHERE mr.tenantId = :tenantId AND mr.tags LIKE %:tags%")
    List<MetaRelation> findByTenantIdAndTagsContaining(@Param("tenantId") String tenantId, @Param("tags") String tags);

    /**
     * 根据源对象ID和标签查询关系列表
     * 
     * @param sourceObjectId 源对象ID
     * @param tags 标签
     * @return 关系列表
     */
    @Query("SELECT mr FROM MetaRelation mr WHERE mr.sourceObject.id = :sourceObjectId AND mr.tags LIKE %:tags%")
    List<MetaRelation> findBySourceObjectIdAndTagsContaining(@Param("sourceObjectId") Long sourceObjectId, @Param("tags") String tags);

    /**
     * 根据目标对象ID和标签查询关系列表
     * 
     * @param targetObjectId 目标对象ID
     * @param tags 标签
     * @return 关系列表
     */
    @Query("SELECT mr FROM MetaRelation mr WHERE mr.targetObject.id = :targetObjectId AND mr.tags LIKE %:tags%")
    List<MetaRelation> findByTargetObjectIdAndTagsContaining(@Param("targetObjectId") Long targetObjectId, @Param("tags") String tags);

    /**
     * 检查源对象ID和目标对象ID是否存在关系
     * 
     * @param sourceObjectId 源对象ID
     * @param targetObjectId 目标对象ID
     * @return 是否存在
     */
    boolean existsBySourceObjectIdAndTargetObjectId(Long sourceObjectId, Long targetObjectId);

    /**
     * 检查源对象ID和关系名称是否存在
     * 
     * @param sourceObjectId 源对象ID
     * @param name 关系名称
     * @return 是否存在
     */
    boolean existsBySourceObjectIdAndName(Long sourceObjectId, String name);

    /**
     * 根据源对象ID统计关系数量
     * 
     * @param sourceObjectId 源对象ID
     * @return 数量
     */
    long countBySourceObjectId(Long sourceObjectId);

    /**
     * 根据目标对象ID统计关系数量
     * 
     * @param targetObjectId 目标对象ID
     * @return 数量
     */
    long countByTargetObjectId(Long targetObjectId);

    /**
     * 根据租户ID统计关系数量
     * 
     * @param tenantId 租户ID
     * @return 数量
     */
    long countByTenantId(String tenantId);

    /**
     * 根据租户ID和关系类型统计关系数量
     * 
     * @param tenantId 租户ID
     * @param relationType 关系类型
     * @return 数量
     */
    long countByTenantIdAndRelationType(String tenantId, String relationType);

    /**
     * 根据源对象ID删除关系
     * 
     * @param sourceObjectId 源对象ID
     */
    void deleteBySourceObjectId(Long sourceObjectId);

    /**
     * 根据目标对象ID删除关系
     * 
     * @param targetObjectId 目标对象ID
     */
    void deleteByTargetObjectId(Long targetObjectId);

    /**
     * 根据租户ID删除关系
     * 
     * @param tenantId 租户ID
     */
    void deleteByTenantId(String tenantId);
} 
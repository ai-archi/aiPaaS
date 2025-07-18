package com.aixone.metacenter.metamanagement.domain;

import com.aixone.metacenter.metamanagement.application.dto.MetaObjectQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 元数据关系仓储接口
 * 负责元数据关系的数据访问
 */
@Repository
public interface MetaRelationRepository extends JpaRepository<MetaRelation, Long> {

    /**
     * 根据名称查询元数据关系
     *
     * @param name 关系名称
     * @return 元数据关系
     */
    Optional<MetaRelation> findByName(String name);

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
     * 根据名称和源对象ID查询关系
     *
     * @param name 关系名称
     * @param sourceObjectId 源对象ID
     * @return 关系
     */
    Optional<MetaRelation> findByNameAndSourceObjectId(String name, Long sourceObjectId);

    /**
     * 检查关系名称是否存在
     *
     * @param name 关系名称
     * @param sourceObjectId 源对象ID
     * @return 是否存在
     */
    boolean existsByNameAndSourceObjectId(String name, Long sourceObjectId);

    /**
     * 根据关系类型查询关系列表
     *
     * @param relationType 关系类型
     * @return 关系列表
     */
    List<MetaRelation> findByRelationType(String relationType);

    /**
     * 根据源对象ID和目标对象ID查询关系
     *
     * @param sourceObjectId 源对象ID
     * @param targetObjectId 目标对象ID
     * @return 关系列表
     */
    List<MetaRelation> findBySourceObjectIdAndTargetObjectId(Long sourceObjectId, Long targetObjectId);

    /**
     * 根据条件分页查询关系
     *
     * @param query 查询条件
     * @param pageable 分页参数
     * @return 分页结果
     */
    @Query("SELECT mr FROM MetaRelation mr " +
           "LEFT JOIN mr.sourceObject so " +
           "LEFT JOIN mr.targetObject to " +
           "WHERE (:name IS NULL OR mr.name LIKE %:name%) " +
           "AND (:displayName IS NULL OR mr.displayName LIKE %:displayName%) " +
           "AND (:relationType IS NULL OR mr.relationType = :relationType) " +
           "AND (:cardinality IS NULL OR mr.cardinality = :cardinality) " +
           "AND (:sourceObjectId IS NULL OR so.id = :sourceObjectId) " +
           "AND (:targetObjectId IS NULL OR to.id = :targetObjectId) " +
           "AND (:sourceObjectName IS NULL OR so.name LIKE %:sourceObjectName%) " +
           "AND (:targetObjectName IS NULL OR to.name LIKE %:targetObjectName%)")
    Page<MetaRelation> findByConditions(@Param("name") String name,
                                       @Param("displayName") String displayName,
                                       @Param("relationType") String relationType,
                                       @Param("cardinality") String cardinality,
                                       @Param("sourceObjectId") Long sourceObjectId,
                                       @Param("targetObjectId") Long targetObjectId,
                                       @Param("sourceObjectName") String sourceObjectName,
                                       @Param("targetObjectName") String targetObjectName,
                                       Pageable pageable);

    /**
     * 根据条件分页查询关系（使用MetaObjectQuery）
     *
     * @param query 查询条件
     * @param pageable 分页参数
     * @return 分页结果
     */
    default Page<MetaRelation> findByConditions(MetaObjectQuery query, Pageable pageable) {
        if (query == null) {
            return findAll(pageable);
        }
        return findByConditions(
            query.getName(),
            query.getDisplayName(),
            query.getRelationType(),
            query.getCardinality(),
            query.getSourceObjectId(),
            query.getTargetObjectId(),
            query.getSourceObjectName(),
            query.getTargetObjectName(),
            pageable
        );
    }

    /**
     * 根据源对象ID和关系类型查询关系
     *
     * @param sourceObjectId 源对象ID
     * @param relationType 关系类型
     * @return 关系列表
     */
    List<MetaRelation> findBySourceObjectIdAndRelationType(Long sourceObjectId, String relationType);

    /**
     * 根据目标对象ID和关系类型查询关系
     *
     * @param targetObjectId 目标对象ID
     * @param relationType 关系类型
     * @return 关系列表
     */
    List<MetaRelation> findByTargetObjectIdAndRelationType(Long targetObjectId, String relationType);

    /**
     * 统计源对象的关系数量
     *
     * @param sourceObjectId 源对象ID
     * @return 关系数量
     */
    long countBySourceObjectId(Long sourceObjectId);

    /**
     * 统计目标对象的关系数量
     *
     * @param targetObjectId 目标对象ID
     * @return 关系数量
     */
    long countByTargetObjectId(Long targetObjectId);

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
     * 根据源对象ID和目标对象ID删除关系
     *
     * @param sourceObjectId 源对象ID
     * @param targetObjectId 目标对象ID
     */
    void deleteBySourceObjectIdAndTargetObjectId(Long sourceObjectId, Long targetObjectId);

    /**
     * 根据源对象ID或目标对象ID查询关系
     * 
     * @param sourceMetaObjectId 源对象ID
     * @param targetMetaObjectId 目标对象ID
     * @return 关系列表
     */
    List<MetaRelation> findBySourceMetaObjectIdOrTargetMetaObjectId(Long sourceMetaObjectId, Long targetMetaObjectId);

    /**
     * 根据查询条件分页查询元数据关系
     * 
     * @param query 查询条件
     * @param pageable 分页参数
     * @return 分页结果
     */
} 
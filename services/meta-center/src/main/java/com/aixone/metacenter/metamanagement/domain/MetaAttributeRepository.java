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
 * 元数据属性仓储接口
 * 负责元数据属性的数据访问
 */
@Repository
public interface MetaAttributeRepository extends JpaRepository<MetaAttribute, Long> {

    /**
     * 根据名称查询元数据属性
     *
     * @param name 属性名称
     * @return 元数据属性
     */
    Optional<MetaAttribute> findByName(String name);

    /**
     * 根据元数据对象ID查询属性列表
     *
     * @param metaObjectId 元数据对象ID
     * @return 属性列表
     */
    List<MetaAttribute> findByMetaObjectId(Long metaObjectId);

    /**
     * 根据名称和元数据对象ID查询属性
     *
     * @param name 属性名称
     * @param metaObjectId 元数据对象ID
     * @return 属性
     */
    Optional<MetaAttribute> findByNameAndMetaObjectId(String name, Long metaObjectId);

    /**
     * 检查属性名称是否存在
     *
     * @param name 属性名称
     * @param metaObjectId 元数据对象ID
     * @return 是否存在
     */
    boolean existsByNameAndMetaObjectId(String name, Long metaObjectId);

    /**
     * 根据数据类型查询属性列表
     *
     * @param dataType 数据类型
     * @return 属性列表
     */
    List<MetaAttribute> findByDataType(String dataType);

    /**
     * 根据是否必填查询属性列表
     *
     * @param required 是否必填
     * @return 属性列表
     */
    List<MetaAttribute> findByRequired(Boolean required);

    /**
     * 根据条件分页查询属性
     *
     * @param query 查询条件
     * @param pageable 分页参数
     * @return 分页结果
     */
    @Query("SELECT ma FROM MetaAttribute ma " +
           "LEFT JOIN ma.metaObject mo " +
           "WHERE (:name IS NULL OR ma.name LIKE %:name%) " +
           "AND (:displayName IS NULL OR ma.displayName LIKE %:displayName%) " +
           "AND (:dataType IS NULL OR ma.dataType = :dataType) " +
           "AND (:required IS NULL OR ma.required = :required) " +
           "AND (:metaObjectId IS NULL OR mo.id = :metaObjectId) " +
           "AND (:metaObjectName IS NULL OR mo.name LIKE %:metaObjectName%)")
    Page<MetaAttribute> findByConditions(@Param("name") String name,
                                        @Param("displayName") String displayName,
                                        @Param("dataType") String dataType,
                                        @Param("required") Boolean required,
                                        @Param("metaObjectId") Long metaObjectId,
                                        @Param("metaObjectName") String metaObjectName,
                                        Pageable pageable);

    /**
     * 根据条件分页查询属性（使用MetaObjectQuery）
     *
     * @param query 查询条件
     * @param pageable 分页参数
     * @return 分页结果
     */
    default Page<MetaAttribute> findByConditions(MetaObjectQuery query, Pageable pageable) {
        if (query == null) {
            return findAll(pageable);
        }
        return findByConditions(
            query.getName(),
            query.getDisplayName(),
            query.getDataType(),
            query.getRequired(),
            query.getMetaObjectId(),
            query.getMetaObjectName(),
            pageable
        );
    }

    /**
     * 根据元数据对象ID和数据类型查询属性
     *
     * @param metaObjectId 元数据对象ID
     * @param dataType 数据类型
     * @return 属性列表
     */
    List<MetaAttribute> findByMetaObjectIdAndDataType(Long metaObjectId, String dataType);

    /**
     * 根据元数据对象ID和是否必填查询属性
     *
     * @param metaObjectId 元数据对象ID
     * @param required 是否必填
     * @return 属性列表
     */
    List<MetaAttribute> findByMetaObjectIdAndRequired(Long metaObjectId, Boolean required);

    /**
     * 统计元数据对象的属性数量
     *
     * @param metaObjectId 元数据对象ID
     * @return 属性数量
     */
    long countByMetaObjectId(Long metaObjectId);

    /**
     * 根据元数据对象ID删除属性
     *
     * @param metaObjectId 元数据对象ID
     */
    void deleteByMetaObjectId(Long metaObjectId);
} 
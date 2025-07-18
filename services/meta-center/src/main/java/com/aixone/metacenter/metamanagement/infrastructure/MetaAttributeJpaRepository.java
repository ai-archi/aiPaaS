package com.aixone.metacenter.metamanagement.infrastructure;

import com.aixone.metacenter.metamanagement.domain.MetaAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 元数据属性JPA仓储接口
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Repository
public interface MetaAttributeJpaRepository extends JpaRepository<MetaAttribute, Long>, JpaSpecificationExecutor<MetaAttribute> {

    /**
     * 根据元数据对象ID查询属性列表
     * 
     * @param metaObjectId 元数据对象ID
     * @return 属性列表
     */
    List<MetaAttribute> findByMetaObjectId(Long metaObjectId);

    /**
     * 根据元数据对象ID和属性名称查询属性
     * 
     * @param metaObjectId 元数据对象ID
     * @param name 属性名称
     * @return 属性
     */
    Optional<MetaAttribute> findByMetaObjectIdAndName(Long metaObjectId, String name);

    /**
     * 根据租户ID查询属性列表
     * 
     * @param tenantId 租户ID
     * @return 属性列表
     */
    List<MetaAttribute> findByTenantId(String tenantId);

    /**
     * 根据租户ID和属性名称查询属性列表
     * 
     * @param tenantId 租户ID
     * @param name 属性名称
     * @return 属性列表
     */
    List<MetaAttribute> findByTenantIdAndName(String tenantId, String name);

    /**
     * 根据租户ID和数据类型查询属性列表
     * 
     * @param tenantId 租户ID
     * @param dataType 数据类型
     * @return 属性列表
     */
    List<MetaAttribute> findByTenantIdAndDataType(String tenantId, String dataType);

    /**
     * 根据租户ID和是否必填查询属性列表
     * 
     * @param tenantId 租户ID
     * @param required 是否必填
     * @return 属性列表
     */
    List<MetaAttribute> findByTenantIdAndRequired(String tenantId, Boolean required);

    /**
     * 根据租户ID和显示顺序查询属性列表
     * 
     * @param tenantId 租户ID
     * @return 属性列表（按显示顺序排序）
     */
    List<MetaAttribute> findByTenantIdOrderByDisplayOrder(String tenantId);

    /**
     * 根据元数据对象ID和显示顺序查询属性列表
     * 
     * @param metaObjectId 元数据对象ID
     * @return 属性列表（按显示顺序排序）
     */
    List<MetaAttribute> findByMetaObjectIdOrderByDisplayOrder(Long metaObjectId);

    /**
     * 根据租户ID和标签查询属性列表
     * 
     * @param tenantId 租户ID
     * @param tags 标签
     * @return 属性列表
     */
    @Query("SELECT ma FROM MetaAttribute ma WHERE ma.tenantId = :tenantId AND ma.tags LIKE %:tags%")
    List<MetaAttribute> findByTenantIdAndTagsContaining(@Param("tenantId") String tenantId, @Param("tags") String tags);

    /**
     * 根据元数据对象ID和标签查询属性列表
     * 
     * @param metaObjectId 元数据对象ID
     * @param tags 标签
     * @return 属性列表
     */
    @Query("SELECT ma FROM MetaAttribute ma WHERE ma.metaObject.id = :metaObjectId AND ma.tags LIKE %:tags%")
    List<MetaAttribute> findByMetaObjectIdAndTagsContaining(@Param("metaObjectId") Long metaObjectId, @Param("tags") String tags);

    /**
     * 检查元数据对象ID和属性名称是否存在
     * 
     * @param metaObjectId 元数据对象ID
     * @param name 属性名称
     * @return 是否存在
     */
    boolean existsByMetaObjectIdAndName(Long metaObjectId, String name);

    /**
     * 根据元数据对象ID统计属性数量
     * 
     * @param metaObjectId 元数据对象ID
     * @return 数量
     */
    long countByMetaObjectId(Long metaObjectId);

    /**
     * 根据租户ID统计属性数量
     * 
     * @param tenantId 租户ID
     * @return 数量
     */
    long countByTenantId(String tenantId);

    /**
     * 根据租户ID和数据类型统计属性数量
     * 
     * @param tenantId 租户ID
     * @param dataType 数据类型
     * @return 数量
     */
    long countByTenantIdAndDataType(String tenantId, String dataType);

    /**
     * 根据元数据对象ID删除属性
     * 
     * @param metaObjectId 元数据对象ID
     */
    void deleteByMetaObjectId(Long metaObjectId);

    /**
     * 根据租户ID删除属性
     * 
     * @param tenantId 租户ID
     */
    void deleteByTenantId(String tenantId);
} 
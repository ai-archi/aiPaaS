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
 * 元数据扩展仓储接口
 * 负责元数据扩展的数据访问
 */
@Repository
public interface MetaExtensionRepository extends JpaRepository<MetaExtension, Long> {

    /**
     * 根据名称查询元数据扩展
     *
     * @param name 扩展名称
     * @return 元数据扩展
     */
    Optional<MetaExtension> findByName(String name);

    /**
     * 根据元数据对象ID查询扩展列表
     *
     * @param metaObjectId 元数据对象ID
     * @return 扩展列表
     */
    List<MetaExtension> findByMetaObjectId(Long metaObjectId);

    /**
     * 根据扩展类型查询扩展列表
     *
     * @param extensionType 扩展类型
     * @return 扩展列表
     */
    List<MetaExtension> findByExtensionType(String extensionType);

    /**
     * 根据是否启用查询扩展列表
     *
     * @param enabled 是否启用
     * @return 扩展列表
     */
    List<MetaExtension> findByEnabled(Boolean enabled);

    /**
     * 根据名称和元数据对象ID查询扩展
     *
     * @param name 扩展名称
     * @param metaObjectId 元数据对象ID
     * @return 扩展
     */
    Optional<MetaExtension> findByNameAndMetaObjectId(String name, Long metaObjectId);

    /**
     * 检查扩展名称是否存在
     *
     * @param name 扩展名称
     * @param metaObjectId 元数据对象ID
     * @return 是否存在
     */
    boolean existsByNameAndMetaObjectId(String name, Long metaObjectId);

    /**
     * 根据元数据对象ID和扩展类型查询扩展
     *
     * @param metaObjectId 元数据对象ID
     * @param extensionType 扩展类型
     * @return 扩展列表
     */
    List<MetaExtension> findByMetaObjectIdAndExtensionType(Long metaObjectId, String extensionType);

    /**
     * 根据元数据对象ID和是否启用查询扩展
     *
     * @param metaObjectId 元数据对象ID
     * @param enabled 是否启用
     * @return 扩展列表
     */
    List<MetaExtension> findByMetaObjectIdAndEnabled(Long metaObjectId, Boolean enabled);

    /**
     * 根据条件分页查询扩展
     *
     * @param name 扩展名称
     * @param displayName 显示名称
     * @param extensionType 扩展类型
     * @param enabled 是否启用
     * @param metaObjectId 元数据对象ID
     * @param pageable 分页参数
     * @return 分页结果
     */
    @Query("SELECT me FROM MetaExtension me " +
           "LEFT JOIN me.metaObject mo " +
           "WHERE (:name IS NULL OR me.name LIKE %:name%) " +
           "AND (:displayName IS NULL OR me.displayName LIKE %:displayName%) " +
           "AND (:extensionType IS NULL OR me.extensionType = :extensionType) " +
           "AND (:enabled IS NULL OR me.enabled = :enabled) " +
           "AND (:metaObjectId IS NULL OR mo.id = :metaObjectId)")
    Page<MetaExtension> findByConditions(@Param("name") String name,
                                        @Param("displayName") String displayName,
                                        @Param("extensionType") String extensionType,
                                        @Param("enabled") Boolean enabled,
                                        @Param("metaObjectId") Long metaObjectId,
                                        Pageable pageable);

    /**
     * 统计元数据对象的扩展数量
     *
     * @param metaObjectId 元数据对象ID
     * @return 扩展数量
     */
    long countByMetaObjectId(Long metaObjectId);

    /**
     * 根据元数据对象ID删除扩展
     *
     * @param metaObjectId 元数据对象ID
     */
    void deleteByMetaObjectId(Long metaObjectId);

    /**
     * 根据扩展类型删除扩展
     *
     * @param extensionType 扩展类型
     */
    void deleteByExtensionType(String extensionType);

    /**
     * 查找启用的扩展
     *
     * @return 启用的扩展列表
     */
    List<MetaExtension> findByEnabledTrue();

    /**
     * 根据元数据对象ID查找启用的扩展
     *
     * @param metaObjectId 元数据对象ID
     * @return 启用的扩展列表
     */
    List<MetaExtension> findByMetaObjectIdAndEnabledTrue(Long metaObjectId);

    /**
     * 根据扩展类型查找启用的扩展
     *
     * @param extensionType 扩展类型
     * @return 启用的扩展列表
     */
    List<MetaExtension> findByExtensionTypeAndEnabledTrue(String extensionType);
} 
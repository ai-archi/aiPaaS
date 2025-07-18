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
 * 元数据对象仓储接口
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Repository
public interface MetaObjectRepository extends JpaRepository<MetaObject, Long> {

    /**
     * 根据租户ID和名称查找元数据对象
     * 
     * @param tenantId 租户ID
     * @param name 名称
     * @return 元数据对象
     */
    Optional<MetaObject> findByTenantIdAndName(String tenantId, String name);

    /**
     * 根据租户ID和类型查找元数据对象列表
     * 
     * @param tenantId 租户ID
     * @param type 类型
     * @return 元数据对象列表
     */
    List<MetaObject> findByTenantIdAndType(String tenantId, String type);

    /**
     * 根据租户ID和对象类型查找元数据对象列表
     * 
     * @param tenantId 租户ID
     * @param objectType 对象类型
     * @return 元数据对象列表
     */
    List<MetaObject> findByTenantIdAndObjectType(String tenantId, String objectType);

    /**
     * 根据租户ID和生命周期状态查找元数据对象列表
     * 
     * @param tenantId 租户ID
     * @param lifecycle 生命周期状态
     * @return 元数据对象列表
     */
    List<MetaObject> findByTenantIdAndLifecycle(String tenantId, String lifecycle);

    /**
     * 根据租户ID和运行状态查找元数据对象列表
     * 
     * @param tenantId 租户ID
     * @param status 运行状态
     * @return 元数据对象列表
     */
    List<MetaObject> findByTenantIdAndStatus(String tenantId, String status);

    /**
     * 根据租户ID分页查询元数据对象
     * 
     * @param tenantId 租户ID
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<MetaObject> findByTenantId(String tenantId, Pageable pageable);

    /**
     * 根据租户ID和名称模糊查询元数据对象
     * 
     * @param tenantId 租户ID
     * @param name 名称（模糊匹配）
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<MetaObject> findByTenantIdAndNameContainingIgnoreCase(String tenantId, String name, Pageable pageable);

    /**
     * 根据租户ID和描述模糊查询元数据对象
     * 
     * @param tenantId 租户ID
     * @param description 描述（模糊匹配）
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<MetaObject> findByTenantIdAndDescriptionContainingIgnoreCase(String tenantId, String description, Pageable pageable);

    /**
     * 根据租户ID和标签查询元数据对象
     * 
     * @param tenantId 租户ID
     * @param tag 标签
     * @param pageable 分页参数
     * @return 分页结果
     */
    @Query("SELECT m FROM MetaObject m WHERE m.tenantId = :tenantId AND m.tags LIKE %:tag%")
    Page<MetaObject> findByTenantIdAndTagsContaining(@Param("tenantId") String tenantId, @Param("tag") String tag, Pageable pageable);

    /**
     * 根据租户ID和多个类型查询元数据对象
     * 
     * @param tenantId 租户ID
     * @param types 类型列表
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<MetaObject> findByTenantIdAndTypeIn(String tenantId, List<String> types, Pageable pageable);

    /**
     * 根据租户ID和多个生命周期状态查询元数据对象
     * 
     * @param tenantId 租户ID
     * @param lifecycles 生命周期状态列表
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<MetaObject> findByTenantIdAndLifecycleIn(String tenantId, List<String> lifecycles, Pageable pageable);

    /**
     * 检查租户ID和名称是否存在
     * 
     * @param tenantId 租户ID
     * @param name 名称
     * @return 是否存在
     */
    boolean existsByTenantIdAndName(String tenantId, String name);

    /**
     * 根据租户ID统计元数据对象数量
     * 
     * @param tenantId 租户ID
     * @return 数量
     */
    long countByTenantId(String tenantId);

    /**
     * 根据租户ID和类型统计元数据对象数量
     * 
     * @param tenantId 租户ID
     * @param type 类型
     * @return 数量
     */
    long countByTenantIdAndType(String tenantId, String type);
} 
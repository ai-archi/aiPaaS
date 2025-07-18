package com.aixone.metacenter.metamanagement.infrastructure;

import com.aixone.metacenter.metamanagement.domain.MetaObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 元数据对象JPA仓储接口
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Repository
public interface MetaObjectJpaRepository extends JpaRepository<MetaObject, Long>, JpaSpecificationExecutor<MetaObject> {

    /**
     * 根据租户ID查询元数据对象
     * 
     * @param tenantId 租户ID
     * @return 元数据对象列表
     */
    List<MetaObject> findByTenantId(String tenantId);

    /**
     * 根据租户ID和对象类型查询元数据对象
     * 
     * @param tenantId 租户ID
     * @param objectType 对象类型
     * @return 元数据对象列表
     */
    List<MetaObject> findByTenantIdAndObjectType(String tenantId, String objectType);

    /**
     * 根据租户ID和主类型查询元数据对象
     * 
     * @param tenantId 租户ID
     * @param type 主类型
     * @return 元数据对象列表
     */
    List<MetaObject> findByTenantIdAndType(String tenantId, String type);

    /**
     * 根据租户ID和生命周期状态查询元数据对象
     * 
     * @param tenantId 租户ID
     * @param lifecycle 生命周期状态
     * @return 元数据对象列表
     */
    List<MetaObject> findByTenantIdAndLifecycle(String tenantId, String lifecycle);

    /**
     * 根据租户ID和运行状态查询元数据对象
     * 
     * @param tenantId 租户ID
     * @param status 运行状态
     * @return 元数据对象列表
     */
    List<MetaObject> findByTenantIdAndStatus(String tenantId, String status);

    /**
     * 根据租户ID和名称查询元数据对象
     * 
     * @param tenantId 租户ID
     * @param name 名称
     * @return 元数据对象
     */
    Optional<MetaObject> findByTenantIdAndName(String tenantId, String name);

    /**
     * 根据租户ID和标签查询元数据对象
     * 
     * @param tenantId 租户ID
     * @param tags 标签
     * @return 元数据对象列表
     */
    @Query("SELECT mo FROM MetaObject mo WHERE mo.tenantId = :tenantId AND mo.tags LIKE %:tags%")
    List<MetaObject> findByTenantIdAndTagsContaining(@Param("tenantId") String tenantId, @Param("tags") String tags);

    /**
     * 根据租户ID和名称模糊查询分页
     * 
     * @param tenantId 租户ID
     * @param name 名称
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<MetaObject> findByTenantIdAndNameContainingIgnoreCase(String tenantId, String name, Pageable pageable);

    /**
     * 根据租户ID和描述模糊查询分页
     * 
     * @param tenantId 租户ID
     * @param description 描述
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<MetaObject> findByTenantIdAndDescriptionContainingIgnoreCase(String tenantId, String description, Pageable pageable);

    /**
     * 根据租户ID和标签模糊查询分页
     * 
     * @param tenantId 租户ID
     * @param tags 标签
     * @param pageable 分页参数
     * @return 分页结果
     */
    @Query("SELECT mo FROM MetaObject mo WHERE mo.tenantId = :tenantId AND mo.tags LIKE %:tags%")
    Page<MetaObject> findByTenantIdAndTagsContaining(@Param("tenantId") String tenantId, @Param("tags") String tags, Pageable pageable);

    /**
     * 根据租户ID和主类型列表查询分页
     * 
     * @param tenantId 租户ID
     * @param types 主类型列表
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<MetaObject> findByTenantIdAndTypeIn(String tenantId, List<String> types, Pageable pageable);

    /**
     * 根据租户ID和生命周期状态列表查询分页
     * 
     * @param tenantId 租户ID
     * @param lifecycles 生命周期状态列表
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<MetaObject> findByTenantIdAndLifecycleIn(String tenantId, List<String> lifecycles, Pageable pageable);

    /**
     * 根据租户ID和责任人查询元数据对象
     * 
     * @param tenantId 租户ID
     * @param owner 责任人
     * @return 元数据对象列表
     */
    List<MetaObject> findByTenantIdAndOwner(String tenantId, String owner);

    /**
     * 根据租户ID分页查询元数据对象
     * 
     * @param tenantId 租户ID
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<MetaObject> findByTenantId(String tenantId, Pageable pageable);

    /**
     * 根据租户ID和对象类型分页查询元数据对象
     * 
     * @param tenantId 租户ID
     * @param objectType 对象类型
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<MetaObject> findByTenantIdAndObjectType(String tenantId, String objectType, Pageable pageable);

    /**
     * 根据租户ID和主类型分页查询元数据对象
     * 
     * @param tenantId 租户ID
     * @param type 主类型
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<MetaObject> findByTenantIdAndType(String tenantId, String type, Pageable pageable);

    /**
     * 根据租户ID和生命周期状态分页查询元数据对象
     * 
     * @param tenantId 租户ID
     * @param lifecycle 生命周期状态
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<MetaObject> findByTenantIdAndLifecycle(String tenantId, String lifecycle, Pageable pageable);

    /**
     * 根据租户ID和运行状态分页查询元数据对象
     * 
     * @param tenantId 租户ID
     * @param status 运行状态
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<MetaObject> findByTenantIdAndStatus(String tenantId, String status, Pageable pageable);

    /**
     * 根据租户ID和责任人分页查询元数据对象
     * 
     * @param tenantId 租户ID
     * @param owner 责任人
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<MetaObject> findByTenantIdAndOwner(String tenantId, String owner, Pageable pageable);

    /**
     * 根据租户ID统计元数据对象数量
     * 
     * @param tenantId 租户ID
     * @return 数量
     */
    long countByTenantId(String tenantId);

    /**
     * 根据租户ID和对象类型统计元数据对象数量
     * 
     * @param tenantId 租户ID
     * @param objectType 对象类型
     * @return 数量
     */
    long countByTenantIdAndObjectType(String tenantId, String objectType);

    /**
     * 根据租户ID和主类型统计元数据对象数量
     * 
     * @param tenantId 租户ID
     * @param type 主类型
     * @return 数量
     */
    long countByTenantIdAndType(String tenantId, String type);

    /**
     * 根据租户ID和生命周期状态统计元数据对象数量
     * 
     * @param tenantId 租户ID
     * @param lifecycle 生命周期状态
     * @return 数量
     */
    long countByTenantIdAndLifecycle(String tenantId, String lifecycle);

    /**
     * 根据租户ID和运行状态统计元数据对象数量
     * 
     * @param tenantId 租户ID
     * @param status 运行状态
     * @return 数量
     */
    long countByTenantIdAndStatus(String tenantId, String status);

    /**
     * 根据租户ID和责任人统计元数据对象数量
     * 
     * @param tenantId 租户ID
     * @param owner 责任人
     * @return 数量
     */
    long countByTenantIdAndOwner(String tenantId, String owner);

    /**
     * 检查租户ID和名称是否存在
     * 
     * @param tenantId 租户ID
     * @param name 名称
     * @return 是否存在
     */
    boolean existsByTenantIdAndName(String tenantId, String name);

    /**
     * 根据租户ID删除元数据对象
     * 
     * @param tenantId 租户ID
     */
    void deleteByTenantId(String tenantId);
} 
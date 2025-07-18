package com.aixone.metacenter.dataservice.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 数据实例仓储接口
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Repository
public interface DataInstanceRepository extends JpaRepository<DataInstance, Long> {

    /**
     * 根据租户ID和元数据对象ID查找数据实例列表
     * 
     * @param tenantId 租户ID
     * @param metaObjectId 元数据对象ID
     * @return 数据实例列表
     */
    List<DataInstance> findByTenantIdAndMetaObjectId(String tenantId, Long metaObjectId);

    /**
     * 根据租户ID、元数据对象ID和状态查找数据实例列表
     * 
     * @param tenantId 租户ID
     * @param metaObjectId 元数据对象ID
     * @param status 状态
     * @return 数据实例列表
     */
    List<DataInstance> findByTenantIdAndMetaObjectIdAndStatus(String tenantId, Long metaObjectId, String status);

    /**
     * 根据租户ID和元数据对象ID分页查询数据实例
     * 
     * @param tenantId 租户ID
     * @param metaObjectId 元数据对象ID
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<DataInstance> findByTenantIdAndMetaObjectId(String tenantId, Long metaObjectId, Pageable pageable);

    /**
     * 根据租户ID、元数据对象ID和状态分页查询数据实例
     * 
     * @param tenantId 租户ID
     * @param metaObjectId 元数据对象ID
     * @param status 状态
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<DataInstance> findByTenantIdAndMetaObjectIdAndStatus(String tenantId, Long metaObjectId, String status, Pageable pageable);

    /**
     * 根据租户ID统计数据实例数量
     * 
     * @param tenantId 租户ID
     * @return 数量
     */
    long countByTenantId(String tenantId);

    /**
     * 根据租户ID和元数据对象ID统计数据实例数量
     * 
     * @param tenantId 租户ID
     * @param metaObjectId 元数据对象ID
     * @return 数量
     */
    long countByTenantIdAndMetaObjectId(String tenantId, Long metaObjectId);

    /**
     * 根据租户ID、元数据对象ID和状态统计数据实例数量
     * 
     * @param tenantId 租户ID
     * @param metaObjectId 元数据对象ID
     * @param status 状态
     * @return 数量
     */
    long countByTenantIdAndMetaObjectIdAndStatus(String tenantId, Long metaObjectId, String status);
} 
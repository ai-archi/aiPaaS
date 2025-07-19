package com.aixone.metacenter.dataservice.infrastructure;

import com.aixone.metacenter.dataservice.domain.DataInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 数据实例JPA仓储接口
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Repository
public interface DataInstanceJpaRepository extends JpaRepository<DataInstance, Long>, JpaSpecificationExecutor<DataInstance> {

    /**
     * 根据元数据对象ID查询数据实例
     * 
     * @param metaObjectId 元数据对象ID
     * @return 数据实例列表
     */
    List<DataInstance> findByMetaObjectId(Long metaObjectId);

    /**
     * 根据租户ID查询数据实例
     * 
     * @param tenantId 租户ID
     * @return 数据实例列表
     */
    List<DataInstance> findByTenantId(String tenantId);

    /**
     * 根据状态查询数据实例
     * 
     * @param status 状态
     * @return 数据实例列表
     */
    List<DataInstance> findByStatus(String status);

    /**
     * 根据实例名称查询数据实例
     * 
     * @param name 实例名称
     * @return 数据实例
     */
    Optional<DataInstance> findByName(String name);

    /**
     * 根据租户ID和实例名称查询数据实例
     * 
     * @param tenantId 租户ID
     * @param name 实例名称
     * @return 数据实例
     */
    Optional<DataInstance> findByTenantIdAndName(String tenantId, String name);

    /**
     * 根据实例类型查询数据实例
     * 
     * @param instanceType 实例类型
     * @return 数据实例列表
     */
    List<DataInstance> findByInstanceType(String instanceType);

    /**
     * 根据数据源类型查询数据实例
     * 
     * @param dataSourceType 数据源类型
     * @return 数据实例列表
     */
    List<DataInstance> findByDataSourceType(String dataSourceType);

    /**
     * 根据租户ID和状态查询数据实例
     * 
     * @param tenantId 租户ID
     * @param status 状态
     * @return 数据实例列表
     */
    List<DataInstance> findByTenantIdAndStatus(String tenantId, String status);

    /**
     * 根据元数据对象ID和状态查询数据实例
     * 
     * @param metaObjectId 元数据对象ID
     * @param status 状态
     * @return 数据实例列表
     */
    List<DataInstance> findByMetaObjectIdAndStatus(Long metaObjectId, String status);

    /**
     * 根据租户ID和元数据对象ID查询数据实例
     * 
     * @param tenantId 租户ID
     * @param metaObjectId 元数据对象ID
     * @return 数据实例列表
     */
    List<DataInstance> findByTenantIdAndMetaObjectId(String tenantId, Long metaObjectId);

    /**
     * 根据租户ID、元数据对象ID和状态查询数据实例
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
    org.springframework.data.domain.Page<DataInstance> findByTenantIdAndMetaObjectId(String tenantId, Long metaObjectId, org.springframework.data.domain.Pageable pageable);

    /**
     * 根据租户ID、元数据对象ID和状态分页查询数据实例
     * 
     * @param tenantId 租户ID
     * @param metaObjectId 元数据对象ID
     * @param status 状态
     * @param pageable 分页参数
     * @return 分页结果
     */
    org.springframework.data.domain.Page<DataInstance> findByTenantIdAndMetaObjectIdAndStatus(String tenantId, Long metaObjectId, String status, org.springframework.data.domain.Pageable pageable);

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
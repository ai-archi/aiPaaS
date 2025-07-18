package com.aixone.metacenter.processengine.infrastructure;

import com.aixone.metacenter.processengine.domain.Process;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 流程JPA仓储接口
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Repository
public interface ProcessJpaRepository extends JpaRepository<Process, Long>, JpaSpecificationExecutor<Process> {

    /**
     * 根据元数据对象ID查询流程列表
     * 
     * @param metaObjectId 元数据对象ID
     * @return 流程列表
     */
    List<Process> findByMetaObjectId(Long metaObjectId);

    /**
     * 根据元数据对象ID和流程名称查询流程
     * 
     * @param metaObjectId 元数据对象ID
     * @param name 流程名称
     * @return 流程
     */
    Optional<Process> findByMetaObjectIdAndName(Long metaObjectId, String name);

    /**
     * 根据租户ID查询流程列表
     * 
     * @param tenantId 租户ID
     * @return 流程列表
     */
    List<Process> findByTenantId(String tenantId);

    /**
     * 根据租户ID和流程类型查询流程列表
     * 
     * @param tenantId 租户ID
     * @param processType 流程类型
     * @return 流程列表
     */
    List<Process> findByTenantIdAndProcessType(String tenantId, String processType);

    /**
     * 根据租户ID和流程名称查询流程列表
     * 
     * @param tenantId 租户ID
     * @param name 流程名称
     * @return 流程列表
     */
    List<Process> findByTenantIdAndName(String tenantId, String name);

    /**
     * 根据租户ID和启用状态查询流程列表
     * 
     * @param tenantId 租户ID
     * @param enabled 启用状态
     * @return 流程列表
     */
    List<Process> findByTenantIdAndEnabled(String tenantId, Boolean enabled);

    /**
     * 根据租户ID和版本查询流程列表
     * 
     * @param tenantId 租户ID
     * @param version 版本
     * @return 流程列表
     */
    List<Process> findByTenantIdAndVersion(String tenantId, String version);

    /**
     * 根据元数据对象ID和启用状态查询流程列表
     * 
     * @param metaObjectId 元数据对象ID
     * @param enabled 启用状态
     * @return 流程列表
     */
    List<Process> findByMetaObjectIdAndEnabled(Long metaObjectId, Boolean enabled);

    /**
     * 根据元数据对象ID和版本查询流程列表
     * 
     * @param metaObjectId 元数据对象ID
     * @param version 版本
     * @return 流程列表
     */
    List<Process> findByMetaObjectIdAndVersion(Long metaObjectId, String version);

    /**
     * 根据租户ID和标签查询流程列表
     * 
     * @param tenantId 租户ID
     * @param tags 标签
     * @return 流程列表
     */
    @Query("SELECT p FROM Process p WHERE p.tenantId = :tenantId AND p.tags LIKE %:tags%")
    List<Process> findByTenantIdAndTagsContaining(@Param("tenantId") String tenantId, @Param("tags") String tags);

    /**
     * 根据元数据对象ID和标签查询流程列表
     * 
     * @param metaObjectId 元数据对象ID
     * @param tags 标签
     * @return 流程列表
     */
    @Query("SELECT p FROM Process p WHERE p.metaObject.id = :metaObjectId AND p.tags LIKE %:tags%")
    List<Process> findByMetaObjectIdAndTagsContaining(@Param("metaObjectId") Long metaObjectId, @Param("tags") String tags);

    /**
     * 检查元数据对象ID和流程名称是否存在
     * 
     * @param metaObjectId 元数据对象ID
     * @param name 流程名称
     * @return 是否存在
     */
    boolean existsByMetaObjectIdAndName(Long metaObjectId, String name);

    /**
     * 根据元数据对象ID统计流程数量
     * 
     * @param metaObjectId 元数据对象ID
     * @return 数量
     */
    long countByMetaObjectId(Long metaObjectId);

    /**
     * 根据租户ID统计流程数量
     * 
     * @param tenantId 租户ID
     * @return 数量
     */
    long countByTenantId(String tenantId);

    /**
     * 根据租户ID和流程类型统计流程数量
     * 
     * @param tenantId 租户ID
     * @param processType 流程类型
     * @return 数量
     */
    long countByTenantIdAndProcessType(String tenantId, String processType);

    /**
     * 根据租户ID和启用状态统计流程数量
     * 
     * @param tenantId 租户ID
     * @param enabled 启用状态
     * @return 数量
     */
    long countByTenantIdAndEnabled(String tenantId, Boolean enabled);

    /**
     * 根据元数据对象ID删除流程
     * 
     * @param metaObjectId 元数据对象ID
     */
    void deleteByMetaObjectId(Long metaObjectId);

    /**
     * 根据租户ID删除流程
     * 
     * @param tenantId 租户ID
     */
    void deleteByTenantId(String tenantId);
} 
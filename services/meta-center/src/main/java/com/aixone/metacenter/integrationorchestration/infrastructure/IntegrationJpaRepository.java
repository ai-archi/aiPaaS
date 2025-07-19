package com.aixone.metacenter.integrationorchestration.infrastructure;

import com.aixone.metacenter.integrationorchestration.domain.Integration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 集成编排JPA仓储接口
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Repository
public interface IntegrationJpaRepository extends JpaRepository<Integration, Long>, JpaSpecificationExecutor<Integration> {

    /**
     * 根据租户ID查询集成列表
     * 
     * @param tenantId 租户ID
     * @return 集成列表
     */
    List<Integration> findByTenantId(String tenantId);

    /**
     * 根据租户ID和集成名称查询集成
     * 
     * @param tenantId 租户ID
     * @param name 集成名称
     * @return 集成
     */
    Optional<Integration> findByTenantIdAndName(String tenantId, String name);

    /**
     * 根据租户ID和集成类型查询集成列表
     * 
     * @param tenantId 租户ID
     * @param integrationType 集成类型
     * @return 集成列表
     */
    List<Integration> findByTenantIdAndIntegrationType(String tenantId, String integrationType);

    /**
     * 根据租户ID和协议类型查询集成列表
     * 
     * @param tenantId 租户ID
     * @param protocolType 协议类型
     * @return 集成列表
     */
    List<Integration> findByTenantIdAndProtocolType(String tenantId, String protocolType);

    /**
     * 根据租户ID和启用状态查询集成列表
     * 
     * @param tenantId 租户ID
     * @param enabled 启用状态
     * @return 集成列表
     */
    List<Integration> findByTenantIdAndEnabled(String tenantId, Boolean enabled);

    /**
     * 根据租户ID和版本查询集成列表
     * 
     * @param tenantId 租户ID
     * @param version 版本
     * @return 集成列表
     */
    List<Integration> findByTenantIdAndVersion(String tenantId, String version);

    /**
     * 根据租户ID和标签查询集成列表
     * 
     * @param tenantId 租户ID
     * @param tags 标签
     * @return 集成列表
     */
    @Query("SELECT i FROM Integration i WHERE i.tenantId = :tenantId AND i.tags LIKE %:tags%")
    List<Integration> findByTenantIdAndTagsContaining(@Param("tenantId") String tenantId, @Param("tags") String tags);

    /**
     * 根据租户ID和外部系统ID查询集成列表
     * 
     * @param tenantId 租户ID
     * @param externalSystemId 外部系统ID
     * @return 集成列表
     */
    List<Integration> findByTenantIdAndExternalSystemId(String tenantId, String externalSystemId);

    /**
     * 根据租户ID和API端点查询集成列表
     * 
     * @param tenantId 租户ID
     * @param apiEndpoint API端点
     * @return 集成列表
     */
    List<Integration> findByTenantIdAndApiEndpoint(String tenantId, String apiEndpoint);

    /**
     * 检查租户ID和集成名称是否存在
     * 
     * @param tenantId 租户ID
     * @param name 集成名称
     * @return 是否存在
     */
    boolean existsByTenantIdAndName(String tenantId, String name);

    /**
     * 根据租户ID统计集成数量
     * 
     * @param tenantId 租户ID
     * @return 数量
     */
    long countByTenantId(String tenantId);

    /**
     * 根据租户ID和集成类型统计集成数量
     * 
     * @param tenantId 租户ID
     * @param integrationType 集成类型
     * @return 数量
     */
    long countByTenantIdAndIntegrationType(String tenantId, String integrationType);

    /**
     * 根据租户ID和协议类型统计集成数量
     * 
     * @param tenantId 租户ID
     * @param protocolType 协议类型
     * @return 数量
     */
    long countByTenantIdAndProtocolType(String tenantId, String protocolType);

    /**
     * 根据租户ID和启用状态统计集成数量
     * 
     * @param tenantId 租户ID
     * @param enabled 启用状态
     * @return 数量
     */
    long countByTenantIdAndEnabled(String tenantId, Boolean enabled);

    /**
     * 根据租户ID删除集成
     * 
     * @param tenantId 租户ID
     */
    void deleteByTenantId(String tenantId);

    /**
     * 根据租户ID和类型查询集成列表
     * 
     * @param tenantId 租户ID
     * @param type 类型
     * @return 集成列表
     */
    List<Integration> findByTenantIdAndType(String tenantId, String type);

    /**
     * 根据租户ID和状态查询集成列表
     * 
     * @param tenantId 租户ID
     * @param status 状态
     * @return 集成列表
     */
    List<Integration> findByTenantIdAndStatus(String tenantId, String status);

    /**
     * 根据租户ID分页查询集成
     * 
     * @param tenantId 租户ID
     * @param pageable 分页参数
     * @return 分页结果
     */
    org.springframework.data.domain.Page<Integration> findByTenantId(String tenantId, org.springframework.data.domain.Pageable pageable);

    /**
     * 根据租户ID和名称模糊查询集成
     * 
     * @param tenantId 租户ID
     * @param name 名称（模糊匹配）
     * @param pageable 分页参数
     * @return 分页结果
     */
    org.springframework.data.domain.Page<Integration> findByTenantIdAndNameContainingIgnoreCase(String tenantId, String name, org.springframework.data.domain.Pageable pageable);

    /**
     * 根据租户ID和类型统计集成数量
     * 
     * @param tenantId 租户ID
     * @param type 类型
     * @return 数量
     */
    long countByTenantIdAndType(String tenantId, String type);
}

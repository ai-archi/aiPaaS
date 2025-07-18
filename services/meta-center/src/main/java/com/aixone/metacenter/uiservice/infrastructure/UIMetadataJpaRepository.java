package com.aixone.metacenter.uiservice.infrastructure;

import com.aixone.metacenter.uiservice.domain.UIMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * UI元数据JPA仓储接口
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Repository
public interface UIMetadataJpaRepository extends JpaRepository<UIMetadata, Long>, JpaSpecificationExecutor<UIMetadata> {

    /**
     * 根据租户ID查询UI元数据
     * 
     * @param tenantId 租户ID
     * @return UI元数据列表
     */
    List<UIMetadata> findByTenantId(String tenantId);

    /**
     * 根据状态查询UI元数据
     * 
     * @param status 状态
     * @return UI元数据列表
     */
    List<UIMetadata> findByStatus(String status);

    /**
     * 根据页面类型查询UI元数据
     * 
     * @param pageType 页面类型
     * @return UI元数据列表
     */
    List<UIMetadata> findByPageType(String pageType);

    /**
     * 根据组件类型查询UI元数据
     * 
     * @param componentType 组件类型
     * @return UI元数据列表
     */
    List<UIMetadata> findByComponentType(String componentType);

    /**
     * 根据名称查询UI元数据
     * 
     * @param name 名称
     * @return UI元数据
     */
    Optional<UIMetadata> findByName(String name);

    /**
     * 根据租户ID和名称查询UI元数据
     * 
     * @param tenantId 租户ID
     * @param name 名称
     * @return UI元数据
     */
    Optional<UIMetadata> findByTenantIdAndName(String tenantId, String name);

    /**
     * 根据租户ID和状态查询UI元数据
     * 
     * @param tenantId 租户ID
     * @param status 状态
     * @return UI元数据列表
     */
    List<UIMetadata> findByTenantIdAndStatus(String tenantId, String status);

    /**
     * 根据租户ID和页面类型查询UI元数据
     * 
     * @param tenantId 租户ID
     * @param pageType 页面类型
     * @return UI元数据列表
     */
    List<UIMetadata> findByTenantIdAndPageType(String tenantId, String pageType);
} 
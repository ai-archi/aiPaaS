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

    /**
     * 根据租户ID和页面ID查询UI元数据
     * 
     * @param tenantId 租户ID
     * @param pageId 页面ID
     * @return UI元数据
     */
    Optional<UIMetadata> findByTenantIdAndPageId(String tenantId, String pageId);

    /**
     * 根据租户ID和类型查询UI元数据
     * 
     * @param tenantId 租户ID
     * @param type 类型
     * @return UI元数据列表
     */
    List<UIMetadata> findByTenantIdAndType(String tenantId, String type);

    /**
     * 根据租户ID分页查询UI元数据
     * 
     * @param tenantId 租户ID
     * @param pageable 分页参数
     * @return 分页结果
     */
    org.springframework.data.domain.Page<UIMetadata> findByTenantId(String tenantId, org.springframework.data.domain.Pageable pageable);

    /**
     * 根据租户ID和页面ID模糊查询UI元数据
     * 
     * @param tenantId 租户ID
     * @param pageId 页面ID（模糊匹配）
     * @param pageable 分页参数
     * @return 分页结果
     */
    org.springframework.data.domain.Page<UIMetadata> findByTenantIdAndPageIdContainingIgnoreCase(String tenantId, String pageId, org.springframework.data.domain.Pageable pageable);

    /**
     * 根据租户ID和标题模糊查询UI元数据
     * 
     * @param tenantId 租户ID
     * @param title 标题（模糊匹配）
     * @param pageable 分页参数
     * @return 分页结果
     */
    org.springframework.data.domain.Page<UIMetadata> findByTenantIdAndTitleContainingIgnoreCase(String tenantId, String title, org.springframework.data.domain.Pageable pageable);

    /**
     * 检查租户ID和页面ID是否存在
     * 
     * @param tenantId 租户ID
     * @param pageId 页面ID
     * @return 是否存在
     */
    boolean existsByTenantIdAndPageId(String tenantId, String pageId);

    /**
     * 根据租户ID统计UI元数据数量
     * 
     * @param tenantId 租户ID
     * @return 数量
     */
    long countByTenantId(String tenantId);

    /**
     * 根据租户ID和类型统计UI元数据数量
     * 
     * @param tenantId 租户ID
     * @param type 类型
     * @return 数量
     */
    long countByTenantIdAndType(String tenantId, String type);
} 
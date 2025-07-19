package com.aixone.metacenter.uiservice.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * UI元数据仓储接口
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Repository
public interface UIMetadataRepository {

    /**
     * 保存UI元数据
     * 
     * @param uiMetadata UI元数据
     * @return 保存后的UI元数据
     */
    UIMetadata save(UIMetadata uiMetadata);

    /**
     * 根据ID查找UI元数据
     * 
     * @param id UI元数据ID
     * @return UI元数据
     */
    Optional<UIMetadata> findById(Long id);

    /**
     * 检查ID是否存在
     * 
     * @param id UI元数据ID
     * @return 是否存在
     */
    boolean existsById(Long id);

    /**
     * 根据ID删除UI元数据
     * 
     * @param id UI元数据ID
     */
    void deleteById(Long id);

    /**
     * 根据租户ID和页面ID查找UI元数据
     * 
     * @param tenantId 租户ID
     * @param pageId 页面ID
     * @return UI元数据
     */
    Optional<UIMetadata> findByTenantIdAndPageId(String tenantId, String pageId);

    /**
     * 根据租户ID和类型查找UI元数据列表
     * 
     * @param tenantId 租户ID
     * @param type 类型
     * @return UI元数据列表
     */
    List<UIMetadata> findByTenantIdAndType(String tenantId, String type);

    /**
     * 根据租户ID和状态查找UI元数据列表
     * 
     * @param tenantId 租户ID
     * @param status 状态
     * @return UI元数据列表
     */
    List<UIMetadata> findByTenantIdAndStatus(String tenantId, String status);

    /**
     * 根据租户ID分页查询UI元数据
     * 
     * @param tenantId 租户ID
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<UIMetadata> findByTenantId(String tenantId, Pageable pageable);

    /**
     * 根据租户ID和页面ID模糊查询UI元数据
     * 
     * @param tenantId 租户ID
     * @param pageId 页面ID（模糊匹配）
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<UIMetadata> findByTenantIdAndPageIdContainingIgnoreCase(String tenantId, String pageId, Pageable pageable);

    /**
     * 根据租户ID和标题模糊查询UI元数据
     * 
     * @param tenantId 租户ID
     * @param title 标题（模糊匹配）
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<UIMetadata> findByTenantIdAndTitleContainingIgnoreCase(String tenantId, String title, Pageable pageable);

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
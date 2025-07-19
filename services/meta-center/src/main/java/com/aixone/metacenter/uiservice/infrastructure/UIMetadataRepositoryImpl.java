package com.aixone.metacenter.uiservice.infrastructure;

import com.aixone.metacenter.uiservice.application.dto.UIMetadataQuery;
import com.aixone.metacenter.uiservice.domain.UIMetadata;
import com.aixone.metacenter.uiservice.domain.UIMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * UI元数据仓储实现
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Repository
public class UIMetadataRepositoryImpl implements UIMetadataRepository {

    @Autowired
    private UIMetadataJpaRepository uiMetadataJpaRepository;

    @Override
    public UIMetadata save(UIMetadata uiMetadata) {
        return uiMetadataJpaRepository.save(uiMetadata);
    }

    // 接口中定义的自定义方法实现
    @Override
    public Optional<UIMetadata> findByTenantIdAndPageId(String tenantId, String pageId) {
        return uiMetadataJpaRepository.findByTenantIdAndPageId(tenantId, pageId);
    }

    @Override
    public List<UIMetadata> findByTenantIdAndType(String tenantId, String type) {
        return uiMetadataJpaRepository.findByTenantIdAndType(tenantId, type);
    }

    @Override
    public List<UIMetadata> findByTenantIdAndStatus(String tenantId, String status) {
        return uiMetadataJpaRepository.findByTenantIdAndStatus(tenantId, status);
    }

    @Override
    public Page<UIMetadata> findByTenantId(String tenantId, Pageable pageable) {
        return uiMetadataJpaRepository.findByTenantId(tenantId, pageable);
    }

    @Override
    public Page<UIMetadata> findByTenantIdAndPageIdContainingIgnoreCase(String tenantId, String pageId, Pageable pageable) {
        return uiMetadataJpaRepository.findByTenantIdAndPageIdContainingIgnoreCase(tenantId, pageId, pageable);
    }

    @Override
    public Page<UIMetadata> findByTenantIdAndTitleContainingIgnoreCase(String tenantId, String title, Pageable pageable) {
        return uiMetadataJpaRepository.findByTenantIdAndTitleContainingIgnoreCase(tenantId, title, pageable);
    }

    @Override
    public boolean existsByTenantIdAndPageId(String tenantId, String pageId) {
        return uiMetadataJpaRepository.existsByTenantIdAndPageId(tenantId, pageId);
    }

    @Override
    public long countByTenantId(String tenantId) {
        return uiMetadataJpaRepository.countByTenantId(tenantId);
    }

    @Override
    public long countByTenantIdAndType(String tenantId, String type) {
        return uiMetadataJpaRepository.countByTenantIdAndType(tenantId, type);
    }

    @Override
    public void deleteById(Long id) {
        uiMetadataJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return uiMetadataJpaRepository.existsById(id);
    }

    @Override
    public Optional<UIMetadata> findById(Long id) {
        return uiMetadataJpaRepository.findById(id);
    }
} 
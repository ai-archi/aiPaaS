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

    @Override
    public List<UIMetadata> saveAll(List<UIMetadata> uiMetadataList) {
        return uiMetadataJpaRepository.saveAll(uiMetadataList);
    }

    @Override
    public List<UIMetadata> findAll() {
        return uiMetadataJpaRepository.findAll();
    }

    @Override
    public List<UIMetadata> findByIds(List<Long> ids) {
        return uiMetadataJpaRepository.findAllById(ids);
    }

    @Override
    public List<UIMetadata> findByTenantId(String tenantId) {
        return uiMetadataJpaRepository.findByTenantId(tenantId);
    }

    @Override
    public List<UIMetadata> findByStatus(String status) {
        return uiMetadataJpaRepository.findByStatus(status);
    }

    @Override
    public List<UIMetadata> findByPageType(String pageType) {
        return uiMetadataJpaRepository.findByPageType(pageType);
    }

    @Override
    public List<UIMetadata> findByComponentType(String componentType) {
        return uiMetadataJpaRepository.findByComponentType(componentType);
    }

    @Override
    public Page<UIMetadata> findByQuery(UIMetadataQuery query, Pageable pageable) {
        Specification<UIMetadata> spec = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (query.getTenantId() != null && !query.getTenantId().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("tenantId"), query.getTenantId()));
            }

            if (query.getName() != null && !query.getName().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + query.getName() + "%"));
            }

            if (query.getDisplayName() != null && !query.getDisplayName().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("displayName"), "%" + query.getDisplayName() + "%"));
            }

            if (query.getDescription() != null && !query.getDescription().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("description"), "%" + query.getDescription() + "%"));
            }

            if (query.getPageType() != null && !query.getPageType().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("pageType"), query.getPageType()));
            }

            if (query.getComponentType() != null && !query.getComponentType().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("componentType"), query.getComponentType()));
            }

            if (query.getStatuses() != null && !query.getStatuses().isEmpty()) {
                predicates.add(root.get("status").in(query.getStatuses()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return uiMetadataJpaRepository.findAll(spec, pageable);
    }

    @Override
    public void deleteById(Long id) {
        uiMetadataJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAll(List<UIMetadata> uiMetadataList) {
        uiMetadataJpaRepository.deleteAll(uiMetadataList);
    }

    @Override
    public boolean existsById(Long id) {
        return uiMetadataJpaRepository.existsById(id);
    }

    @Override
    public long count() {
        return uiMetadataJpaRepository.count();
    }
} 
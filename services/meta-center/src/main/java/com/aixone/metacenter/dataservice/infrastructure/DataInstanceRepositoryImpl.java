package com.aixone.metacenter.dataservice.infrastructure;

import com.aixone.metacenter.dataservice.application.dto.DataInstanceQuery;
import com.aixone.metacenter.dataservice.domain.DataInstance;
import com.aixone.metacenter.dataservice.domain.DataInstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据实例仓储实现
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Repository
public class DataInstanceRepositoryImpl implements DataInstanceRepository {

    @Autowired
    private DataInstanceJpaRepository dataInstanceJpaRepository;

    @Override
    public DataInstance save(DataInstance dataInstance) {
        return dataInstanceJpaRepository.save(dataInstance);
    }

    @Override
    public List<DataInstance> saveAll(List<DataInstance> dataInstances) {
        return dataInstanceJpaRepository.saveAll(dataInstances);
    }

    @Override
    public List<DataInstance> findAll() {
        return dataInstanceJpaRepository.findAll();
    }

    @Override
    public List<DataInstance> findByIds(List<Long> ids) {
        return dataInstanceJpaRepository.findAllById(ids);
    }

    @Override
    public List<DataInstance> findByMetaObjectId(Long metaObjectId) {
        return dataInstanceJpaRepository.findByMetaObjectId(metaObjectId);
    }

    @Override
    public List<DataInstance> findByTenantId(String tenantId) {
        return dataInstanceJpaRepository.findByTenantId(tenantId);
    }

    @Override
    public List<DataInstance> findByStatus(String status) {
        return dataInstanceJpaRepository.findByStatus(status);
    }

    @Override
    public Page<DataInstance> findByQuery(DataInstanceQuery query, Pageable pageable) {
        Specification<DataInstance> spec = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 租户ID
            if (query.getTenantId() != null && !query.getTenantId().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("tenantId"), query.getTenantId()));
            }

            // 实例名称（模糊匹配）
            if (query.getName() != null && !query.getName().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + query.getName() + "%"));
            }

            // 显示名称（模糊匹配）
            if (query.getDisplayName() != null && !query.getDisplayName().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("displayName"), "%" + query.getDisplayName() + "%"));
            }

            // 描述（模糊匹配）
            if (query.getDescription() != null && !query.getDescription().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("description"), "%" + query.getDescription() + "%"));
            }

            // 元数据对象ID
            if (query.getMetaObjectId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("metaObjectId"), query.getMetaObjectId()));
            }

            // 元数据对象名称（模糊匹配）
            if (query.getMetaObjectName() != null && !query.getMetaObjectName().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("metaObjectName"), "%" + query.getMetaObjectName() + "%"));
            }

            // 实例状态列表
            if (query.getStatuses() != null && !query.getStatuses().isEmpty()) {
                predicates.add(root.get("status").in(query.getStatuses()));
            }

            // 实例类型列表
            if (query.getInstanceTypes() != null && !query.getInstanceTypes().isEmpty()) {
                predicates.add(root.get("instanceType").in(query.getInstanceTypes()));
            }

            // 数据源类型列表
            if (query.getDataSourceTypes() != null && !query.getDataSourceTypes().isEmpty()) {
                predicates.add(root.get("dataSourceType").in(query.getDataSourceTypes()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return dataInstanceJpaRepository.findAll(spec, pageable);
    }

    @Override
    public void deleteById(Long id) {
        dataInstanceJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAll(List<DataInstance> dataInstances) {
        dataInstanceJpaRepository.deleteAll(dataInstances);
    }

    @Override
    public boolean existsById(Long id) {
        return dataInstanceJpaRepository.existsById(id);
    }

    @Override
    public long count() {
        return dataInstanceJpaRepository.count();
    }
} 
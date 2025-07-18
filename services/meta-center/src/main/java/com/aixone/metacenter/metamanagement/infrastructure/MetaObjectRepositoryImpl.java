package com.aixone.metacenter.metamanagement.infrastructure;

import com.aixone.metacenter.metamanagement.domain.MetaObject;
import com.aixone.metacenter.metamanagement.domain.MetaObjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 元数据对象仓储实现类
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MetaObjectRepositoryImpl implements MetaObjectRepository {

    private final MetaObjectJpaRepository metaObjectJpaRepository;

    @Override
    public MetaObject save(MetaObject metaObject) {
        log.debug("保存元数据对象: {}", metaObject.getName());
        return metaObjectJpaRepository.save(metaObject);
    }

    @Override
    public List<MetaObject> saveAll(List<MetaObject> metaObjects) {
        log.debug("批量保存元数据对象: {}", metaObjects.size());
        return metaObjectJpaRepository.saveAll(metaObjects);
    }

    @Override
    public Optional<MetaObject> findById(Long id) {
        log.debug("根据ID查询元数据对象: {}", id);
        return metaObjectJpaRepository.findById(id);
    }

    @Override
    public Optional<MetaObject> findByTenantIdAndName(String tenantId, String name) {
        log.debug("根据租户ID和名称查询元数据对象: tenantId={}, name={}", tenantId, name);
        return metaObjectJpaRepository.findByTenantIdAndName(tenantId, name);
    }

    @Override
    public List<MetaObject> findByTenantId(String tenantId) {
        log.debug("根据租户ID查询元数据对象: {}", tenantId);
        return metaObjectJpaRepository.findByTenantId(tenantId);
    }

    @Override
    public List<MetaObject> findByTenantIdAndObjectType(String tenantId, String objectType) {
        log.debug("根据租户ID和对象类型查询元数据对象: tenantId={}, objectType={}", tenantId, objectType);
        return metaObjectJpaRepository.findByTenantIdAndObjectType(tenantId, objectType);
    }

    @Override
    public List<MetaObject> findByTenantIdAndType(String tenantId, String type) {
        log.debug("根据租户ID和主类型查询元数据对象: tenantId={}, type={}", tenantId, type);
        return metaObjectJpaRepository.findByTenantIdAndType(tenantId, type);
    }

    @Override
    public List<MetaObject> findByTenantIdAndLifecycle(String tenantId, String lifecycle) {
        log.debug("根据租户ID和生命周期状态查询元数据对象: tenantId={}, lifecycle={}", tenantId, lifecycle);
        return metaObjectJpaRepository.findByTenantIdAndLifecycle(tenantId, lifecycle);
    }

    @Override
    public List<MetaObject> findByTenantIdAndStatus(String tenantId, String status) {
        log.debug("根据租户ID和运行状态查询元数据对象: tenantId={}, status={}", tenantId, status);
        return metaObjectJpaRepository.findByTenantIdAndStatus(tenantId, status);
    }

    @Override
    public List<MetaObject> findByTenantIdAndTagsContaining(String tenantId, String tags) {
        log.debug("根据租户ID和标签查询元数据对象: tenantId={}, tags={}", tenantId, tags);
        return metaObjectJpaRepository.findByTenantIdAndTagsContaining(tenantId, tags);
    }

    @Override
    public Page<MetaObject> findByTenantIdAndNameContainingIgnoreCase(String tenantId, String name, Pageable pageable) {
        log.debug("根据租户ID和名称模糊查询分页: tenantId={}, name={}, pageable={}", tenantId, name, pageable);
        return metaObjectJpaRepository.findByTenantIdAndNameContainingIgnoreCase(tenantId, name, pageable);
    }

    @Override
    public Page<MetaObject> findByTenantIdAndDescriptionContainingIgnoreCase(String tenantId, String description, Pageable pageable) {
        log.debug("根据租户ID和描述模糊查询分页: tenantId={}, description={}, pageable={}", tenantId, description, pageable);
        return metaObjectJpaRepository.findByTenantIdAndDescriptionContainingIgnoreCase(tenantId, description, pageable);
    }

    @Override
    public Page<MetaObject> findByTenantIdAndTagsContaining(String tenantId, String tags, Pageable pageable) {
        log.debug("根据租户ID和标签模糊查询分页: tenantId={}, tags={}, pageable={}", tenantId, tags, pageable);
        return metaObjectJpaRepository.findByTenantIdAndTagsContaining(tenantId, tags, pageable);
    }

    @Override
    public Page<MetaObject> findByTenantIdAndTypeIn(String tenantId, List<String> types, Pageable pageable) {
        log.debug("根据租户ID和主类型列表查询分页: tenantId={}, types={}, pageable={}", tenantId, types, pageable);
        return metaObjectJpaRepository.findByTenantIdAndTypeIn(tenantId, types, pageable);
    }

    @Override
    public Page<MetaObject> findByTenantIdAndLifecycleIn(String tenantId, List<String> lifecycles, Pageable pageable) {
        log.debug("根据租户ID和生命周期状态列表查询分页: tenantId={}, lifecycles={}, pageable={}", tenantId, lifecycles, pageable);
        return metaObjectJpaRepository.findByTenantIdAndLifecycleIn(tenantId, lifecycles, pageable);
    }

    @Override
    public List<MetaObject> findByTenantIdAndOwner(String tenantId, String owner) {
        log.debug("根据租户ID和责任人查询元数据对象: tenantId={}, owner={}", tenantId, owner);
        return metaObjectJpaRepository.findByTenantIdAndOwner(tenantId, owner);
    }

    @Override
    public Page<MetaObject> findByTenantId(String tenantId, Pageable pageable) {
        log.debug("根据租户ID分页查询元数据对象: tenantId={}, pageable={}", tenantId, pageable);
        return metaObjectJpaRepository.findByTenantId(tenantId, pageable);
    }

    @Override
    public Page<MetaObject> findByTenantIdAndObjectType(String tenantId, String objectType, Pageable pageable) {
        log.debug("根据租户ID和对象类型分页查询元数据对象: tenantId={}, objectType={}, pageable={}", tenantId, objectType, pageable);
        return metaObjectJpaRepository.findByTenantIdAndObjectType(tenantId, objectType, pageable);
    }

    @Override
    public Page<MetaObject> findByTenantIdAndType(String tenantId, String type, Pageable pageable) {
        log.debug("根据租户ID和主类型分页查询元数据对象: tenantId={}, type={}, pageable={}", tenantId, type, pageable);
        return metaObjectJpaRepository.findByTenantIdAndType(tenantId, type, pageable);
    }

    @Override
    public Page<MetaObject> findByTenantIdAndLifecycle(String tenantId, String lifecycle, Pageable pageable) {
        log.debug("根据租户ID和生命周期状态分页查询元数据对象: tenantId={}, lifecycle={}, pageable={}", tenantId, lifecycle, pageable);
        return metaObjectJpaRepository.findByTenantIdAndLifecycle(tenantId, lifecycle, pageable);
    }

    @Override
    public Page<MetaObject> findByTenantIdAndStatus(String tenantId, String status, Pageable pageable) {
        log.debug("根据租户ID和运行状态分页查询元数据对象: tenantId={}, status={}, pageable={}", tenantId, status, pageable);
        return metaObjectJpaRepository.findByTenantIdAndStatus(tenantId, status, pageable);
    }

    @Override
    public Page<MetaObject> findByTenantIdAndOwner(String tenantId, String owner, Pageable pageable) {
        log.debug("根据租户ID和责任人分页查询元数据对象: tenantId={}, owner={}, pageable={}", tenantId, owner, pageable);
        return metaObjectJpaRepository.findByTenantIdAndOwner(tenantId, owner, pageable);
    }

    @Override
    public Page<MetaObject> findAll(Specification<MetaObject> spec, Pageable pageable) {
        log.debug("根据条件分页查询元数据对象: spec={}, pageable={}", spec, pageable);
        return metaObjectJpaRepository.findAll(spec, pageable);
    }

    @Override
    public List<MetaObject> findAll(Specification<MetaObject> spec) {
        log.debug("根据条件查询元数据对象: spec={}", spec);
        return metaObjectJpaRepository.findAll(spec);
    }

    @Override
    public long countByTenantId(String tenantId) {
        log.debug("根据租户ID统计元数据对象数量: {}", tenantId);
        return metaObjectJpaRepository.countByTenantId(tenantId);
    }

    @Override
    public long countByTenantIdAndObjectType(String tenantId, String objectType) {
        log.debug("根据租户ID和对象类型统计元数据对象数量: tenantId={}, objectType={}", tenantId, objectType);
        return metaObjectJpaRepository.countByTenantIdAndObjectType(tenantId, objectType);
    }

    @Override
    public long countByTenantIdAndType(String tenantId, String type) {
        log.debug("根据租户ID和主类型统计元数据对象数量: tenantId={}, type={}", tenantId, type);
        return metaObjectJpaRepository.countByTenantIdAndType(tenantId, type);
    }

    @Override
    public long countByTenantIdAndLifecycle(String tenantId, String lifecycle) {
        log.debug("根据租户ID和生命周期状态统计元数据对象数量: tenantId={}, lifecycle={}", tenantId, lifecycle);
        return metaObjectJpaRepository.countByTenantIdAndLifecycle(tenantId, lifecycle);
    }

    @Override
    public long countByTenantIdAndStatus(String tenantId, String status) {
        log.debug("根据租户ID和运行状态统计元数据对象数量: tenantId={}, status={}", tenantId, status);
        return metaObjectJpaRepository.countByTenantIdAndStatus(tenantId, status);
    }

    @Override
    public long countByTenantIdAndOwner(String tenantId, String owner) {
        log.debug("根据租户ID和责任人统计元数据对象数量: tenantId={}, owner={}", tenantId, owner);
        return metaObjectJpaRepository.countByTenantIdAndOwner(tenantId, owner);
    }

    @Override
    public boolean existsByTenantIdAndName(String tenantId, String name) {
        log.debug("检查租户ID和名称是否存在: tenantId={}, name={}", tenantId, name);
        return metaObjectJpaRepository.existsByTenantIdAndName(tenantId, name);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("根据ID删除元数据对象: {}", id);
        metaObjectJpaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByTenantId(String tenantId) {
        log.debug("根据租户ID删除元数据对象: {}", tenantId);
        metaObjectJpaRepository.deleteByTenantId(tenantId);
    }

    @Override
    @Transactional
    public void delete(MetaObject metaObject) {
        log.debug("删除元数据对象: {}", metaObject.getName());
        metaObjectJpaRepository.delete(metaObject);
    }
} 
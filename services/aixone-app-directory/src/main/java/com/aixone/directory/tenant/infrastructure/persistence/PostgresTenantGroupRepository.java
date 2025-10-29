package com.aixone.directory.tenant.infrastructure.persistence;

import com.aixone.directory.tenant.domain.aggregate.TenantGroup;
import com.aixone.directory.tenant.domain.repository.TenantGroupRepository;
import com.aixone.directory.tenant.infrastructure.persistence.dbo.TenantGroupEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 租户组仓储实现
 */
@Repository
@RequiredArgsConstructor
public class PostgresTenantGroupRepository implements TenantGroupRepository {

    private final TenantGroupJpaRepository jpaRepository;
    private final TenantGroupMapper mapper;

    @Override
    public TenantGroup save(TenantGroup tenantGroup) {
        TenantGroupEntity entity = mapper.toEntity(tenantGroup);
        TenantGroupEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<TenantGroup> findById(String id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<TenantGroup> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<TenantGroup> findByParentId(String parentId) {
        return jpaRepository.findByParentId(parentId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<TenantGroup> findRootGroups() {
        return jpaRepository.findRootGroups().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }

    @Override
    public void deleteById(String id) {
        jpaRepository.deleteById(id);
    }
}


package com.aixone.tech.auth.authorization.infrastructure.persistence.repository;

import com.aixone.tech.auth.authorization.domain.model.Permission;
import com.aixone.tech.auth.authorization.domain.repository.PermissionRepository;
import com.aixone.tech.auth.authorization.infrastructure.persistence.entity.PermissionEntity;
import com.aixone.tech.auth.authorization.infrastructure.persistence.mapper.PermissionMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Permission JPA 仓储实现
 */
@Repository
public class JpaPermissionRepository implements PermissionRepository {

    private final PermissionJpaRepository jpaRepository;
    private final PermissionMapper mapper;

    public JpaPermissionRepository(PermissionJpaRepository jpaRepository, PermissionMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Permission> findByTenantIdAndPermissionIdIn(String tenantId, List<String> permissionIds) {
        return jpaRepository.findByTenantIdAndPermissionIdIn(tenantId, permissionIds)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Permission> findByTenantIdAndResourceAndAction(String tenantId, String resource, String action) {
        return jpaRepository.findByTenantIdAndResourceAndAction(tenantId, resource, action)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Permission save(Permission permission) {
        PermissionEntity entity = mapper.toEntity(permission);
        PermissionEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Permission findByTenantIdAndPermissionId(String tenantId, String permissionId) {
        return jpaRepository.findByTenantIdAndPermissionId(tenantId, permissionId)
                .map(mapper::toDomain)
                .orElse(null);
    }

    @Override
    public void deleteByTenantIdAndPermissionId(String tenantId, String permissionId) {
        jpaRepository.deleteByTenantIdAndPermissionId(tenantId, permissionId);
    }

    @Override
    public List<Permission> findByTenantId(String tenantId) {
        return jpaRepository.findByTenantId(tenantId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByTenantIdAndName(String tenantId, String name) {
        return jpaRepository.existsByTenantIdAndName(tenantId, name);
    }
}

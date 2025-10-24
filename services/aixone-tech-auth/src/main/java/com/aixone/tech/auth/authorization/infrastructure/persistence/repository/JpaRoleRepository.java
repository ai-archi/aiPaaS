package com.aixone.tech.auth.authorization.infrastructure.persistence.repository;

import com.aixone.tech.auth.authorization.domain.model.Role;
import com.aixone.tech.auth.authorization.domain.repository.RoleRepository;
import com.aixone.tech.auth.authorization.infrastructure.persistence.entity.RoleEntity;
import com.aixone.tech.auth.authorization.infrastructure.persistence.mapper.RoleMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Role JPA 仓储实现
 */
@Repository
public class JpaRoleRepository implements RoleRepository {

    private final RoleJpaRepository jpaRepository;
    private final RoleMapper mapper;

    public JpaRoleRepository(RoleJpaRepository jpaRepository, RoleMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Role> findByTenantIdAndRoleIdIn(String tenantId, List<String> roleIds) {
        return jpaRepository.findByTenantIdAndRoleIdIn(tenantId, roleIds)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Role save(Role role) {
        RoleEntity entity = mapper.toEntity(role);
        RoleEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Role findByTenantIdAndRoleId(String tenantId, String roleId) {
        return jpaRepository.findByTenantIdAndRoleId(tenantId, roleId)
                .map(mapper::toDomain)
                .orElse(null);
    }

    @Override
    public List<Role> findByTenantId(String tenantId) {
        return jpaRepository.findByTenantId(tenantId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteByTenantIdAndRoleId(String tenantId, String roleId) {
        jpaRepository.deleteByTenantIdAndRoleId(tenantId, roleId);
    }

    @Override
    public boolean existsByTenantIdAndName(String tenantId, String name) {
        return jpaRepository.existsByTenantIdAndName(tenantId, name);
    }
}

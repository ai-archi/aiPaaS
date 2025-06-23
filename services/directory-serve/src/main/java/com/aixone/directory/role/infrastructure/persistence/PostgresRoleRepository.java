package com.aixone.directory.role.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.aixone.directory.role.domain.aggregate.Role;
import com.aixone.directory.role.domain.repository.RoleRepository;
import com.aixone.directory.role.infrastructure.persistence.dbo.RoleDbo;

@Repository("rolePostgresRepository") // Named to avoid conflict
public class PostgresRoleRepository implements RoleRepository {

    private final RoleJpaRepository jpaRepository;
    private final RoleMapper mapper;

    public PostgresRoleRepository(RoleJpaRepository jpaRepository, RoleMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public void save(Role role) {
        RoleDbo dbo = mapper.toDbo(role);
        jpaRepository.save(dbo);
    }

    @Override
    public Optional<Role> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Role> findByTenantId(UUID tenantId) {
        return jpaRepository.findByTenantId(tenantId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Role> findByTenantIdAndName(UUID tenantId, String name) {
        return jpaRepository.findByTenantIdAndName(tenantId, name).map(mapper::toDomain);
    }
} 
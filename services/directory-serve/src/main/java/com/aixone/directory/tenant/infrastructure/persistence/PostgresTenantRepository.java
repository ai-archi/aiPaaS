package com.aixone.directory.tenant.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.aixone.directory.tenant.domain.aggregate.Tenant;
import com.aixone.directory.tenant.domain.repository.TenantRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PostgresTenantRepository implements TenantRepository {

    private final TenantJpaRepository jpaRepository;
    private final TenantMapper tenantMapper;

    @Override
    public void save(Tenant tenant) {
        jpaRepository.save(tenantMapper.toDbo(tenant));
    }

    @Override
    public Optional<Tenant> findById(String id) {
        return jpaRepository.findById(id).map(tenantMapper::toDomain);
    }
} 
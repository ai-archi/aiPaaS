package com.aixone.directory.tenant.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.aixone.directory.tenant.domain.aggregate.Tenant;

public interface TenantRepository {

    void save(Tenant tenant);

    Optional<Tenant> findById(UUID id);
} 
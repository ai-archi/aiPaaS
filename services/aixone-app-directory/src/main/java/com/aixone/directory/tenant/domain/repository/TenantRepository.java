package com.aixone.directory.tenant.domain.repository;

import java.util.Optional;

import com.aixone.directory.tenant.domain.aggregate.Tenant;

public interface TenantRepository {

    void save(Tenant tenant);

    Optional<Tenant> findById(String id);

    void delete(String tenantId);
} 
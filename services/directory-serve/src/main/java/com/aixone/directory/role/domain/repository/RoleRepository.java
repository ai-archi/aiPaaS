package com.aixone.directory.role.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.aixone.directory.role.domain.aggregate.Role;

public interface RoleRepository {
    void save(Role role);
    Optional<Role> findById(UUID id);
    List<Role> findByTenantId(UUID tenantId);
    Optional<Role> findByTenantIdAndName(UUID tenantId, String name);
} 
package com.aixone.directory.role.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aixone.directory.role.infrastructure.persistence.dbo.RoleDbo;

@Repository
public interface RoleJpaRepository extends JpaRepository<RoleDbo, String> {
    List<RoleDbo> findByTenantId(String tenantId);
    Optional<RoleDbo> findByTenantIdAndName(String tenantId, String name);
} 
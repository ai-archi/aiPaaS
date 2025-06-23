package com.aixone.directory.organization.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aixone.directory.organization.infrastructure.persistence.dbo.OrganizationDbo;

@Repository
public interface OrganizationJpaRepository extends JpaRepository<OrganizationDbo, UUID> {

    Optional<OrganizationDbo> findByTenantIdAndName(UUID tenantId, String name);
} 
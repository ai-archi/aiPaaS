package com.aixone.directory.organization.infrastructure.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.aixone.directory.organization.infrastructure.persistence.dbo.OrganizationDbo;

@Repository
public interface OrganizationJpaRepository extends JpaRepository<OrganizationDbo, String>, JpaSpecificationExecutor<OrganizationDbo> {

    Optional<OrganizationDbo> findByTenantIdAndName(String tenantId, String name);
    
    java.util.List<OrganizationDbo> findByTenantId(String tenantId);
} 
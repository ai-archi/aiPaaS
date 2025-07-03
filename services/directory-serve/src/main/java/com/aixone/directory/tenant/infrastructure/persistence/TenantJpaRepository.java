package com.aixone.directory.tenant.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aixone.directory.tenant.infrastructure.persistence.dbo.TenantDbo;

@Repository
public interface TenantJpaRepository extends JpaRepository<TenantDbo, String> {
} 
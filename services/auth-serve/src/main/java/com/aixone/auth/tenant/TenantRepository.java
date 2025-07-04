package com.aixone.auth.tenant;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 租户Repository
 * 提供Tenant实体的基本CRUD操作
 */
public interface TenantRepository extends JpaRepository<Tenant, String> {
} 
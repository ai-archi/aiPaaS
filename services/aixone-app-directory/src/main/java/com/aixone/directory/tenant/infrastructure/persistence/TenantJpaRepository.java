package com.aixone.directory.tenant.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.aixone.directory.tenant.infrastructure.persistence.dbo.TenantDbo;

/**
 * 租户 JPA 仓储接口
 * 提供租户数据的基本 CRUD 操作
 */
@Repository
public interface TenantJpaRepository extends JpaRepository<TenantDbo, String>, JpaSpecificationExecutor<TenantDbo> {
    
    /**
     * 检查租户名称是否存在
     * 
     * @param name 租户名称
     * @return 是否存在
     */
    boolean existsByName(String name);
} 
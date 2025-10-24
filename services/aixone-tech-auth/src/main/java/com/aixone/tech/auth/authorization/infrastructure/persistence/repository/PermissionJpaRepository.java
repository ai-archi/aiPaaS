package com.aixone.tech.auth.authorization.infrastructure.persistence.repository;

import com.aixone.tech.auth.authorization.infrastructure.persistence.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Permission JPA Repository
 */
@Repository
public interface PermissionJpaRepository extends JpaRepository<PermissionEntity, String> {
    
    List<PermissionEntity> findByTenantIdAndPermissionIdIn(String tenantId, List<String> permissionIds);
    
    List<PermissionEntity> findByTenantIdAndResourceAndAction(String tenantId, String resource, String action);
    
    Optional<PermissionEntity> findByTenantIdAndPermissionId(String tenantId, String permissionId);
    
    List<PermissionEntity> findByTenantId(String tenantId);
    
    void deleteByTenantIdAndPermissionId(String tenantId, String permissionId);
    
    boolean existsByTenantIdAndName(String tenantId, String name);
}

package com.aixone.tech.auth.authorization.infrastructure.persistence.repository;

import com.aixone.tech.auth.authorization.infrastructure.persistence.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Role JPA Repository
 */
@Repository
public interface RoleJpaRepository extends JpaRepository<RoleEntity, String> {
    
    List<RoleEntity> findByTenantIdAndRoleIdIn(String tenantId, List<String> roleIds);
    
    Optional<RoleEntity> findByTenantIdAndRoleId(String tenantId, String roleId);
    
    List<RoleEntity> findByTenantId(String tenantId);
    
    void deleteByTenantIdAndRoleId(String tenantId, String roleId);
    
    boolean existsByTenantIdAndName(String tenantId, String name);
}

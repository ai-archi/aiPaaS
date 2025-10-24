package com.aixone.tech.auth.authorization.infrastructure.persistence.repository;

import com.aixone.tech.auth.authorization.infrastructure.persistence.entity.AbacPolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * AbacPolicy JPA Repository
 */
@Repository
public interface AbacPolicyJpaRepository extends JpaRepository<AbacPolicyEntity, String> {
    
    List<AbacPolicyEntity> findByTenantIdAndResourceAndAction(String tenantId, String resource, String action);
    
    Optional<AbacPolicyEntity> findByTenantIdAndPolicyId(String tenantId, String policyId);
    
    List<AbacPolicyEntity> findByTenantId(String tenantId);
    
    void deleteByTenantIdAndPolicyId(String tenantId, String policyId);
    
    boolean existsByTenantIdAndName(String tenantId, String name);
}

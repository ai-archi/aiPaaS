package com.aixone.tech.auth.authentication.infrastructure.persistence.repository;

import com.aixone.tech.auth.authentication.infrastructure.persistence.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Client JPA Repository
 */
@Repository
public interface ClientJpaRepository extends JpaRepository<ClientEntity, Long> {
    
    Optional<ClientEntity> findByClientIdAndTenantId(String clientId, String tenantId);
    
    List<ClientEntity> findByTenantId(String tenantId);
    
    boolean existsByClientIdAndTenantId(String clientId, String tenantId);
    
    Optional<ClientEntity> findByClientId(String clientId);
    
    void deleteByClientIdAndTenantId(String clientId, String tenantId);
}

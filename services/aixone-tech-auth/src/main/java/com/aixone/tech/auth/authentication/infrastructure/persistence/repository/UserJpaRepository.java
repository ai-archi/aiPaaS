package com.aixone.tech.auth.authentication.infrastructure.persistence.repository;

import com.aixone.tech.auth.authentication.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * User JPA Repository
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {
    
    Optional<UserEntity> findByUsernameAndTenantId(String username, String tenantId);
    
    boolean existsByUsernameAndTenantId(String username, String tenantId);
    
    Optional<UserEntity> findByEmailAndTenantId(String email, String tenantId);
    
    void deleteByUsernameAndTenantId(String username, String tenantId);
    
    java.util.List<UserEntity> findByTenantId(String tenantId);
}

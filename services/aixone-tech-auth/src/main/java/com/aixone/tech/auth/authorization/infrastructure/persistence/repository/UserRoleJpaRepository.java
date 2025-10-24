package com.aixone.tech.auth.authorization.infrastructure.persistence.repository;

import com.aixone.tech.auth.authorization.infrastructure.persistence.entity.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * UserRole JPA Repository
 */
@Repository
public interface UserRoleJpaRepository extends JpaRepository<UserRoleEntity, String> {
    
    List<UserRoleEntity> findByTenantIdAndUserId(String tenantId, String userId);
    
    List<UserRoleEntity> findByTenantIdAndRoleId(String tenantId, String roleId);
    
    void deleteByTenantIdAndUserId(String tenantId, String userId);
    
    void deleteByTenantIdAndUserIdAndRoleId(String tenantId, String userId, String roleId);
    
    boolean existsByTenantIdAndUserIdAndRoleId(String tenantId, String userId, String roleId);
}

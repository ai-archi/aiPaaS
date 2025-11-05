package com.aixone.tech.auth.authentication.domain.repository;

import com.aixone.tech.auth.authentication.domain.model.User;

import java.util.Optional;
import java.util.UUID;

/**
 * 用户仓库接口
 */
public interface UserRepository {
    
    Optional<User> findByUsernameAndTenantId(String username, String tenantId);
    
    Optional<User> findById(UUID id);
    
    User save(User user);
    
    void deleteById(UUID id);
    
    boolean existsByUsernameAndTenantId(String username, String tenantId);
    
    /**
     * 根据租户ID查找所有用户
     */
    java.util.List<User> findByTenantId(String tenantId);
}

package com.aixone.tech.auth.authentication.infrastructure.persistence.repository;

import com.aixone.tech.auth.authentication.infrastructure.persistence.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Token JPA Repository
 */
@Repository
public interface TokenJpaRepository extends JpaRepository<TokenEntity, Long> {
    
    Optional<TokenEntity> findByToken(String token);
    
    List<TokenEntity> findByUserIdAndTenantId(String userId, String tenantId);
    
    List<TokenEntity> findByClientIdAndTenantId(String clientId, String tenantId);
    
    List<TokenEntity> findByUserIdAndClientIdAndTenantId(String userId, String clientId, String tenantId);
    
    boolean existsByToken(String token);
    
    @Query("SELECT t FROM TokenEntity t WHERE t.expiresAt < :now")
    List<TokenEntity> findExpiredTokens(@Param("now") LocalDateTime now);
    
    @Query("SELECT t FROM TokenEntity t WHERE t.tenantId = :tenantId")
    List<TokenEntity> findByTenantId(@Param("tenantId") String tenantId);
    
    void deleteByUserIdAndTenantId(String userId, String tenantId);
    
    void deleteByClientIdAndTenantId(String clientId, String tenantId);
    
    void deleteByToken(String token);
    
    @Modifying
    @Query("DELETE FROM TokenEntity t WHERE t.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);
}

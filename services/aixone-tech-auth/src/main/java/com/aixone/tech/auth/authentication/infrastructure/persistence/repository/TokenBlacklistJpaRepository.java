package com.aixone.tech.auth.authentication.infrastructure.persistence.repository;

import com.aixone.tech.auth.authentication.infrastructure.persistence.entity.TokenBlacklistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * TokenBlacklist JPA Repository
 */
@Repository
public interface TokenBlacklistJpaRepository extends JpaRepository<TokenBlacklistEntity, Long> {
    
    Optional<TokenBlacklistEntity> findByToken(String token);
    
    boolean existsByTokenAndTenantIdAndExpiresAtAfter(String token, String tenantId, LocalDateTime expiresAt);
    
    Optional<TokenBlacklistEntity> findByTokenAndTenantIdAndExpiresAtAfter(String token, String tenantId, LocalDateTime expiresAt);
    
    void deleteByExpiresAtBefore(LocalDateTime expiresAt);
}

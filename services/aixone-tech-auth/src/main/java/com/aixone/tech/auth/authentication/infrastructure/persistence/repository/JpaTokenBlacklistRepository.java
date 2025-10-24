package com.aixone.tech.auth.authentication.infrastructure.persistence.repository;

import com.aixone.tech.auth.authentication.domain.model.TokenBlacklist;
import com.aixone.tech.auth.authentication.domain.repository.TokenBlacklistRepository;
import com.aixone.tech.auth.authentication.infrastructure.persistence.entity.TokenBlacklistEntity;
import com.aixone.tech.auth.authentication.infrastructure.persistence.mapper.TokenBlacklistMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * TokenBlacklist JPA 仓储实现
 */
@Repository
public class JpaTokenBlacklistRepository implements TokenBlacklistRepository {

    private final TokenBlacklistJpaRepository jpaRepository;
    private final TokenBlacklistMapper mapper;

    public JpaTokenBlacklistRepository(TokenBlacklistJpaRepository jpaRepository, TokenBlacklistMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public TokenBlacklist save(TokenBlacklist tokenBlacklist) {
        TokenBlacklistEntity entity = mapper.toEntity(tokenBlacklist);
        TokenBlacklistEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        return jpaRepository.existsByTokenAndTenantIdAndExpiresAtAfter(token, "", LocalDateTime.now());
    }

    @Override
    public Optional<TokenBlacklist> findByToken(String token) {
        return jpaRepository.findByToken(token)
                .map(mapper::toDomain);
    }

    @Override
    public void deleteExpiredTokens() {
        jpaRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }

    @Override
    public void deleteExpiredTokensByTenantId(String tenantId) {
        // 由于 JPA 接口没有按租户删除的方法，这里先实现为删除所有过期的
        jpaRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }

    @Override
    public void deleteByCreatedAtBefore(LocalDateTime timestamp) {
        // 由于 JPA 接口没有按创建时间删除的方法，这里先实现为删除所有过期的
        jpaRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }

    @Override
    public boolean isTokenBlacklistedByTenant(String token, String tenantId) {
        return jpaRepository.existsByTokenAndTenantIdAndExpiresAtAfter(token, tenantId, LocalDateTime.now());
    }

    @Override
    public Optional<TokenBlacklist> findByTokenAndTenantIdAndExpiresAtAfter(String token, String tenantId, LocalDateTime now) {
        return jpaRepository.findByTokenAndTenantIdAndExpiresAtAfter(token, tenantId, now)
                .map(mapper::toDomain);
    }

    @Override
    public void deleteByExpiresAtBefore(LocalDateTime timestamp) {
        jpaRepository.deleteByExpiresAtBefore(timestamp);
    }

    @Override
    public void cleanupExpiredTokens() {
        deleteExpiredTokens();
    }
}
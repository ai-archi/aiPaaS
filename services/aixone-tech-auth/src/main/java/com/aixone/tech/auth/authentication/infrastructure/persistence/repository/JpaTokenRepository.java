package com.aixone.tech.auth.authentication.infrastructure.persistence.repository;

import com.aixone.tech.auth.authentication.domain.model.Token;
import com.aixone.tech.auth.authentication.domain.repository.TokenRepository;
import com.aixone.tech.auth.authentication.infrastructure.persistence.entity.TokenEntity;
import com.aixone.tech.auth.authentication.infrastructure.persistence.mapper.TokenMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Token JPA 仓储实现
 */
@Repository
public class JpaTokenRepository implements TokenRepository {

    private final TokenJpaRepository jpaRepository;
    private final TokenMapper mapper;

    public JpaTokenRepository(TokenJpaRepository jpaRepository, TokenMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Token> findByToken(String token) {
        return jpaRepository.findByToken(token)
                .map(mapper::toDomain);
    }

    @Override
    public List<Token> findByUserIdAndTenantId(String userId, String tenantId) {
        return jpaRepository.findByUserIdAndTenantId(userId, tenantId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Token> findByClientIdAndTenantId(String clientId, String tenantId) {
        return jpaRepository.findByClientIdAndTenantId(clientId, tenantId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Token save(Token token) {
        TokenEntity entity = mapper.toEntity(token);
        TokenEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void delete(String token) {
        jpaRepository.deleteByToken(token);
    }

    @Override
    public void deleteByUserIdAndTenantId(String userId, String tenantId) {
        jpaRepository.deleteByUserIdAndTenantId(userId, tenantId);
    }

    @Override
    public void deleteExpiredTokens() {
        jpaRepository.deleteExpiredTokens(LocalDateTime.now());
    }

    @Override
    public List<Token> findByUserIdAndClientIdAndTenantId(String userId, String clientId, String tenantId) {
        return jpaRepository.findByUserIdAndClientIdAndTenantId(userId, clientId, tenantId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteByClientIdAndTenantId(String clientId, String tenantId) {
        jpaRepository.deleteByClientIdAndTenantId(clientId, tenantId);
    }

    @Override
    public boolean existsByToken(String token) {
        return jpaRepository.existsByToken(token);
    }

    @Override
    public List<Token> findExpiredTokens() {
        return jpaRepository.findExpiredTokens(LocalDateTime.now())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}

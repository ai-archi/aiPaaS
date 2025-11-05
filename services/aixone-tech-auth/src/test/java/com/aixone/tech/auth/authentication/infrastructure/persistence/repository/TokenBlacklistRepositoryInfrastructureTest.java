package com.aixone.tech.auth.authentication.infrastructure.persistence.repository;

import com.aixone.tech.auth.authentication.domain.model.TokenBlacklist;
import com.aixone.tech.auth.authentication.domain.repository.TokenBlacklistRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TokenBlacklist Repository 基础设施层测试
 */
@DataJpaTest
@ActiveProfiles("test")
@ComponentScan(basePackages = "com.aixone.tech.auth")
public class TokenBlacklistRepositoryInfrastructureTest {

    @Autowired
    private TokenBlacklistRepository tokenBlacklistRepository;

    @Test
    public void testSaveAndFindTokenBlacklist() {
        // Given
        TokenBlacklist tokenBlacklist = new TokenBlacklist();
        tokenBlacklist.setToken("test-token");
        tokenBlacklist.setTenantId("test-tenant");
        tokenBlacklist.setExpiresAt(LocalDateTime.now().plusHours(1));
        tokenBlacklist.setCreatedAt(LocalDateTime.now());

        // When
        TokenBlacklist savedBlacklist = tokenBlacklistRepository.save(tokenBlacklist);
        Optional<TokenBlacklist> foundBlacklist = tokenBlacklistRepository.findByToken("test-token");

        // Then
        assertThat(savedBlacklist).isNotNull();
        assertThat(savedBlacklist.getToken()).isEqualTo("test-token");
        assertThat(foundBlacklist).isPresent();
        assertThat(foundBlacklist.get().getToken()).isEqualTo("test-token");
        assertThat(foundBlacklist.get().getTenantId()).isEqualTo("test-tenant");
    }

    @Test
    public void testIsTokenBlacklisted_TokenNotBlacklisted() {
        // Given
        String token = "test-token";
        String tenantId = "test-tenant";

        // When
        boolean isBlacklisted = tokenBlacklistRepository.isTokenBlacklistedByTenant(token, tenantId);

        // Then
        assertThat(isBlacklisted).isFalse();
    }

    @Test
    public void testIsTokenBlacklisted_TokenBlacklisted() {
        // Given
        TokenBlacklist tokenBlacklist = new TokenBlacklist();
        tokenBlacklist.setToken("test-token");
        tokenBlacklist.setTenantId("test-tenant");
        tokenBlacklist.setExpiresAt(LocalDateTime.now().plusHours(1));
        tokenBlacklist.setCreatedAt(LocalDateTime.now());

        tokenBlacklistRepository.save(tokenBlacklist);

        // When
        boolean isBlacklisted = tokenBlacklistRepository.isTokenBlacklistedByTenant("test-token", "test-tenant");

        // Then
        assertThat(isBlacklisted).isTrue();
    }

    @Test
    public void testIsTokenBlacklisted_TokenExpired() {
        // Given
        TokenBlacklist expiredBlacklist = new TokenBlacklist();
        expiredBlacklist.setToken("expired-token");
        expiredBlacklist.setTenantId("test-tenant");
        expiredBlacklist.setExpiresAt(LocalDateTime.now().minusHours(1)); // 已过期
        expiredBlacklist.setCreatedAt(LocalDateTime.now().minusHours(2));

        tokenBlacklistRepository.save(expiredBlacklist);

        // When
        boolean isBlacklisted = tokenBlacklistRepository.isTokenBlacklistedByTenant("expired-token", "test-tenant");

        // Then
        assertThat(isBlacklisted).isFalse();
    }

    @Test
    public void testCleanupExpiredTokens() {
        // Given
        TokenBlacklist validBlacklist = new TokenBlacklist();
        validBlacklist.setToken("valid-token");
        validBlacklist.setTenantId("test-tenant");
        validBlacklist.setExpiresAt(LocalDateTime.now().plusHours(1));
        validBlacklist.setCreatedAt(LocalDateTime.now());

        TokenBlacklist expiredBlacklist = new TokenBlacklist();
        expiredBlacklist.setToken("expired-token");
        expiredBlacklist.setTenantId("test-tenant");
        expiredBlacklist.setExpiresAt(LocalDateTime.now().minusHours(1)); // 已过期
        expiredBlacklist.setCreatedAt(LocalDateTime.now().minusHours(2));

        tokenBlacklistRepository.save(validBlacklist);
        tokenBlacklistRepository.save(expiredBlacklist);

        // When
        tokenBlacklistRepository.cleanupExpiredTokens();

        // Then
        Optional<TokenBlacklist> foundValid = tokenBlacklistRepository.findByToken("valid-token");
        Optional<TokenBlacklist> foundExpired = tokenBlacklistRepository.findByToken("expired-token");

        assertThat(foundValid).isPresent();
        assertThat(foundExpired).isEmpty();
    }
}

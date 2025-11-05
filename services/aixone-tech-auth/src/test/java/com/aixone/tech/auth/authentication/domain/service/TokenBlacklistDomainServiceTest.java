package com.aixone.tech.auth.authentication.domain.service;

import com.aixone.tech.auth.authentication.domain.model.TokenBlacklist;
import com.aixone.tech.auth.authentication.domain.repository.TokenBlacklistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Token 黑名单领域服务测试
 */
@ExtendWith(MockitoExtension.class)
class TokenBlacklistDomainServiceTest {

    @Mock
    private TokenBlacklistRepository tokenBlacklistRepository;

    @InjectMocks
    private TokenBlacklistDomainService tokenBlacklistDomainService;

    private TokenBlacklist testTokenBlacklist;

    @BeforeEach
    void setUp() {
        testTokenBlacklist = new TokenBlacklist();
        testTokenBlacklist.setToken("test-token");
        testTokenBlacklist.setTenantId("test-tenant");
        testTokenBlacklist.setExpiresAt(LocalDateTime.now().plusHours(1));
        testTokenBlacklist.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testIsTokenBlacklisted_TokenNotBlacklisted() {
        // Given
        String token = "test-token";
        String tenantId = "test-tenant";

        when(tokenBlacklistRepository.isTokenBlacklistedByTenant(token, tenantId))
                .thenReturn(false);

        // When
        boolean isBlacklisted = tokenBlacklistDomainService.isTokenBlacklistedByTenant(token, tenantId);

        // Then
        assertThat(isBlacklisted).isFalse();

        verify(tokenBlacklistRepository).isTokenBlacklistedByTenant(token, tenantId);
    }

    @Test
    void testIsTokenBlacklisted_TokenBlacklisted() {
        // Given
        String token = "test-token";
        String tenantId = "test-tenant";

        when(tokenBlacklistRepository.isTokenBlacklistedByTenant(token, tenantId))
                .thenReturn(true);

        // When
        boolean isBlacklisted = tokenBlacklistDomainService.isTokenBlacklistedByTenant(token, tenantId);

        // Then
        assertThat(isBlacklisted).isTrue();

        verify(tokenBlacklistRepository).isTokenBlacklistedByTenant(token, tenantId);
    }

    @Test
    void testBlacklistToken() {
        // Given
        String token = "test-token";
        String tenantId = "test-tenant";
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);

        when(tokenBlacklistRepository.save(any(TokenBlacklist.class)))
                .thenReturn(testTokenBlacklist);

        // When
        tokenBlacklistDomainService.blacklistToken(token, tenantId, expiresAt);

        // Then
        verify(tokenBlacklistRepository).save(any(TokenBlacklist.class));
    }

    @Test
    void testCleanupExpiredTokens() {
        // When
        tokenBlacklistDomainService.cleanupExpiredTokens();

        // Then
        verify(tokenBlacklistRepository).deleteExpiredTokens();
    }
}

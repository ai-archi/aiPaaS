package com.aixone.tech.auth.authentication.domain.service;

import com.aixone.tech.auth.authentication.domain.model.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Token 领域服务测试
 */
class TokenDomainServiceTest {

    private TokenDomainService tokenDomainService;

    @BeforeEach
    void setUp() {
        tokenDomainService = new TokenDomainService();
    }

    @Test
    void testGenerateToken() {
        // Given
        String userId = "test-user";
        String clientId = "test-client";
        String tenantId = "test-tenant";

        // When
        Token token = tokenDomainService.generateToken(tenantId, userId, clientId);

        // Then
        assertThat(token).isNotNull();
        assertThat(token.getToken()).isNotBlank();
        assertThat(token.getUserId()).isEqualTo(userId);
        assertThat(token.getClientId()).isEqualTo(clientId);
        assertThat(token.getTenantId()).isEqualTo(tenantId);
        assertThat(token.getType()).isEqualTo(Token.TokenType.ACCESS);
        assertThat(token.getExpiresAt()).isAfter(LocalDateTime.now());
        assertThat(token.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void testGenerateRefreshToken() {
        // Given
        String userId = "test-user";
        String clientId = "test-client";
        String tenantId = "test-tenant";

        // When
        Token refreshToken = tokenDomainService.generateRefreshToken(tenantId, userId, clientId);

        // Then
        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken.getToken()).isNotBlank();
        assertThat(refreshToken.getUserId()).isEqualTo(userId);
        assertThat(refreshToken.getClientId()).isEqualTo(clientId);
        assertThat(refreshToken.getTenantId()).isEqualTo(tenantId);
        assertThat(refreshToken.getType()).isEqualTo(Token.TokenType.REFRESH);
        assertThat(refreshToken.getExpiresAt()).isAfter(LocalDateTime.now().plusDays(6));
        assertThat(refreshToken.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void testIsTokenExpired_NotExpired() {
        // Given
        Token token = new Token();
        token.setExpiresAt(LocalDateTime.now().plusHours(1));

        // When
        boolean isExpired = tokenDomainService.isTokenExpired(token);

        // Then
        assertThat(isExpired).isFalse();
    }

    @Test
    void testIsTokenExpired_Expired() {
        // Given
        Token token = new Token();
        token.setExpiresAt(LocalDateTime.now().minusHours(1));

        // When
        boolean isExpired = tokenDomainService.isTokenExpired(token);

        // Then
        assertThat(isExpired).isTrue();
    }

    @Test
    void testIsTokenExpired_JustExpired() {
        // Given
        Token token = new Token();
        token.setExpiresAt(LocalDateTime.now().minusSeconds(1));

        // When
        boolean isExpired = tokenDomainService.isTokenExpired(token);

        // Then
        assertThat(isExpired).isTrue();
    }
}

package com.aixone.tech.auth.authentication.domain.service;

import com.aixone.tech.auth.authentication.domain.model.Token;
import com.aixone.common.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Token 领域服务测试
 */
class TokenDomainServiceTest {

    private TokenDomainService tokenDomainService;
    
    @Mock
    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tokenDomainService = new TokenDomainService(jwtUtils);
        
        // Mock JWT generation
        when(jwtUtils.generateAccessToken(org.mockito.ArgumentMatchers.anyString(), 
                                        org.mockito.ArgumentMatchers.anyString(), 
                                        org.mockito.ArgumentMatchers.anyString(), 
                                        org.mockito.ArgumentMatchers.any(Set.class), 
                                        org.mockito.ArgumentMatchers.any(Set.class), 
                                        org.mockito.ArgumentMatchers.any(Map.class)))
            .thenReturn("mock-access-token");
        when(jwtUtils.generateRefreshToken(org.mockito.ArgumentMatchers.anyString(), 
                                         org.mockito.ArgumentMatchers.anyString(), 
                                         org.mockito.ArgumentMatchers.anyString()))
            .thenReturn("mock-refresh-token");
    }

    @Test
    void testGenerateToken() {
        // Given
        String userId = "test-user";
        String clientId = "test-client";
        String tenantId = "test-tenant";
        
        when(jwtUtils.generateAccessToken(anyString(), anyString(), anyString(), any(), any(), any()))
            .thenReturn("mock-access-token");

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
        
        verify(jwtUtils).generateAccessToken(eq(userId), eq(tenantId), eq(clientId), any(), any(), any());
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
    void testIsTokenExpired_NullToken() {
        // When
        boolean isExpired = tokenDomainService.isTokenExpired(null);

        // Then
        assertThat(isExpired).isTrue();
    }

    @Test
    void testValidateToken_Valid() {
        // Given
        Token token = new Token();
        token.setToken("valid-token");
        token.setExpiresAt(LocalDateTime.now().plusHours(1));

        when(jwtUtils.validateToken("valid-token")).thenReturn(true);

        // When
        boolean isValid = tokenDomainService.validateToken(token);

        // Then
        assertThat(isValid).isTrue();
        verify(jwtUtils).validateToken("valid-token");
    }

    @Test
    void testValidateToken_Invalid() {
        // Given
        Token token = new Token();
        token.setToken("invalid-token");
        token.setExpiresAt(LocalDateTime.now().plusHours(1));

        when(jwtUtils.validateToken("invalid-token")).thenReturn(false);

        // When
        boolean isValid = tokenDomainService.validateToken(token);

        // Then
        assertThat(isValid).isFalse();
        verify(jwtUtils).validateToken("invalid-token");
    }

    @Test
    void testValidateToken_NullToken() {
        // When
        boolean isValid = tokenDomainService.validateToken(null);

        // Then
        assertThat(isValid).isFalse();
        verifyNoInteractions(jwtUtils);
    }

    @Test
    void testIsTokenForTenant_Valid() {
        // Given
        Token token = new Token();
        token.setTenantId("test-tenant");

        // When
        boolean isForTenant = tokenDomainService.isTokenForTenant(token, "test-tenant");

        // Then
        assertThat(isForTenant).isTrue();
    }

    @Test
    void testIsTokenForTenant_Invalid() {
        // Given
        Token token = new Token();
        token.setTenantId("test-tenant");

        // When
        boolean isForTenant = tokenDomainService.isTokenForTenant(token, "wrong-tenant");

        // Then
        assertThat(isForTenant).isFalse();
    }

    @Test
    void testIsTokenForTenant_NullToken() {
        // When
        boolean isForTenant = tokenDomainService.isTokenForTenant(null, "test-tenant");

        // Then
        assertThat(isForTenant).isFalse();
    }

    @Test
    void testIsTokenForTenant_NullTenantId() {
        // Given
        Token token = new Token();
        token.setTenantId("test-tenant");

        // When
        boolean isForTenant = tokenDomainService.isTokenForTenant(token, null);

        // Then
        assertThat(isForTenant).isFalse();
    }

    @Test
    void testIsTokenForUser_Valid() {
        // Given
        Token token = new Token();
        token.setUserId("test-user");

        // When
        boolean isForUser = tokenDomainService.isTokenForUser(token, "test-user");

        // Then
        assertThat(isForUser).isTrue();
    }

    @Test
    void testIsTokenForUser_Invalid() {
        // Given
        Token token = new Token();
        token.setUserId("test-user");

        // When
        boolean isForUser = tokenDomainService.isTokenForUser(token, "wrong-user");

        // Then
        assertThat(isForUser).isFalse();
    }

    @Test
    void testIsTokenForUser_NullToken() {
        // When
        boolean isForUser = tokenDomainService.isTokenForUser(null, "test-user");

        // Then
        assertThat(isForUser).isFalse();
    }

    @Test
    void testIsTokenForClient_Valid() {
        // Given
        Token token = new Token();
        token.setClientId("test-client");

        // When
        boolean isForClient = tokenDomainService.isTokenForClient(token, "test-client");

        // Then
        assertThat(isForClient).isTrue();
    }

    @Test
    void testIsTokenForClient_Invalid() {
        // Given
        Token token = new Token();
        token.setClientId("test-client");

        // When
        boolean isForClient = tokenDomainService.isTokenForClient(token, "wrong-client");

        // Then
        assertThat(isForClient).isFalse();
    }

    @Test
    void testIsTokenForClient_NullToken() {
        // When
        boolean isForClient = tokenDomainService.isTokenForClient(null, "test-client");

        // Then
        assertThat(isForClient).isFalse();
    }
}

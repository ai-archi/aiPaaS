package com.aixone.tech.auth.authentication.application.service;

import com.aixone.tech.auth.authentication.application.command.LoginCommand;
import com.aixone.tech.auth.authentication.application.command.RefreshTokenCommand;
import com.aixone.tech.auth.authentication.application.dto.auth.TokenResponse;
import com.aixone.tech.auth.authentication.domain.model.Client;
import com.aixone.tech.auth.authentication.domain.model.Token;
import com.aixone.tech.auth.authentication.domain.repository.ClientRepository;
import com.aixone.tech.auth.authentication.domain.repository.TokenRepository;
import com.aixone.tech.auth.authentication.domain.service.TokenDomainService;
import com.aixone.tech.auth.authentication.domain.service.TokenBlacklistDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.context.ApplicationEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 认证应用服务测试
 */
@ExtendWith(MockitoExtension.class)
class AuthenticationApplicationServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private TokenDomainService tokenDomainService;

    @Mock
    private TokenBlacklistDomainService tokenBlacklistDomainService;


    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private AuthenticationApplicationService authenticationService;

    private Client testClient;
    private Token testToken;

    @BeforeEach
    void setUp() {
        testClient = new Client();
        testClient.setClientId("test-client");
        testClient.setTenantId("test-tenant");
        testClient.setClientSecret("test-secret");
        testClient.setRedirectUri("http://localhost:8080/callback");
        testClient.setScopes("read,write");
        testClient.setGrantTypes("authorization_code,refresh_token");

        testToken = new Token();
        testToken.setToken("test-token");
        testToken.setUserId("test-user");
        testToken.setClientId("test-client");
        testToken.setTenantId("test-tenant");
        testToken.setType(Token.TokenType.ACCESS);
        testToken.setExpiresAt(LocalDateTime.now().plusHours(1));
    }

    @Test
    void testLogin_Success() {
        // Given
        LoginCommand command = new LoginCommand();
        command.setTenantId("test-tenant");
        command.setUsername("test-user");
        command.setPassword("test-password");
        command.setClientId("test-client");
        command.setClientSecret("test-secret");

        when(clientRepository.findByClientIdAndTenantId("test-client", "test-tenant"))
                .thenReturn(Optional.of(testClient));
        when(tokenDomainService.generateAccessToken(eq("test-tenant"), eq("user-test-user-test-tenant"), eq("test-client"), any(LocalDateTime.class)))
                .thenReturn(testToken);
        when(tokenDomainService.generateRefreshToken(eq("test-tenant"), eq("user-test-user-test-tenant"), eq("test-client"), any(LocalDateTime.class)))
                .thenReturn(testToken);
        when(tokenRepository.save(any(Token.class))).thenReturn(testToken);

        // When
        TokenResponse response = authenticationService.login(command);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("test-token");
        assertThat(response.getTenantId()).isEqualTo("test-tenant");
        assertThat(response.getUserId()).isEqualTo("user-test-user-test-tenant");

        verify(clientRepository).findByClientIdAndTenantId("test-client", "test-tenant");
        verify(tokenDomainService).generateAccessToken(eq("test-tenant"), eq("user-test-user-test-tenant"), eq("test-client"), any(LocalDateTime.class));
        verify(tokenDomainService).generateRefreshToken(eq("test-tenant"), eq("user-test-user-test-tenant"), eq("test-client"), any(LocalDateTime.class));
        verify(tokenRepository, atLeast(2)).save(any(Token.class));
    }

    @Test
    void testLogin_ClientNotFound() {
        // Given
        LoginCommand command = new LoginCommand();
        command.setTenantId("test-tenant");
        command.setUsername("test-user");
        command.setPassword("test-password");
        command.setClientId("invalid-client");
        command.setClientSecret("test-secret");

        when(clientRepository.findByClientIdAndTenantId("invalid-client", "test-tenant"))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authenticationService.login(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("客户端不存在");

        verify(clientRepository).findByClientIdAndTenantId("invalid-client", "test-tenant");
        verifyNoInteractions(tokenDomainService);
        verifyNoInteractions(tokenRepository);
    }

    @Test
    void testLogin_InvalidClientSecret() {
        // Given
        LoginCommand command = new LoginCommand();
        command.setTenantId("test-tenant");
        command.setUsername("test-user");
        command.setPassword("test-password");
        command.setClientId("test-client");
        command.setClientSecret("invalid-secret");

        when(clientRepository.findByClientIdAndTenantId("test-client", "test-tenant"))
                .thenReturn(Optional.of(testClient));

        // When & Then
        assertThatThrownBy(() -> authenticationService.login(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("客户端密钥错误");

        verify(clientRepository).findByClientIdAndTenantId("test-client", "test-tenant");
        verifyNoInteractions(tokenDomainService);
        verifyNoInteractions(tokenRepository);
    }

    @Test
    void testRefreshToken_Success() {
        // Given
        RefreshTokenCommand command = new RefreshTokenCommand();
        command.setTenantId("test-tenant");
        command.setRefreshToken("test-refresh-token");
        command.setClientId("test-client");
        command.setClientSecret("test-secret");

        Token refreshToken = new Token();
        refreshToken.setToken("test-refresh-token");
        refreshToken.setUserId("test-user");
        refreshToken.setClientId("test-client");
        refreshToken.setTenantId("test-tenant");
        refreshToken.setType(Token.TokenType.REFRESH);
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7));

        when(clientRepository.findByClientIdAndTenantId("test-client", "test-tenant"))
                .thenReturn(Optional.of(testClient));
        when(tokenRepository.findByToken("test-refresh-token"))
                .thenReturn(Optional.of(refreshToken));
        when(tokenDomainService.validateToken(refreshToken)).thenReturn(true);
        when(tokenDomainService.isTokenForTenant(refreshToken, "test-tenant")).thenReturn(true);
        when(tokenDomainService.generateAccessToken(eq("test-tenant"), eq("test-user"), eq("test-client"), any(LocalDateTime.class)))
                .thenReturn(testToken);
        when(tokenDomainService.generateRefreshToken(eq("test-tenant"), eq("test-user"), eq("test-client"), any(LocalDateTime.class)))
                .thenReturn(refreshToken);
        when(tokenRepository.save(any(Token.class))).thenReturn(testToken);

        // When
        TokenResponse response = authenticationService.refreshToken(command);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("test-token");
        assertThat(response.getTenantId()).isEqualTo("test-tenant");
        assertThat(response.getUserId()).isEqualTo("test-user");

        verify(clientRepository).findByClientIdAndTenantId("test-client", "test-tenant");
        verify(tokenRepository).findByToken("test-refresh-token");
        verify(tokenDomainService).generateAccessToken(eq("test-tenant"), eq("test-user"), eq("test-client"), any(LocalDateTime.class));
        verify(tokenDomainService).generateRefreshToken(eq("test-tenant"), eq("test-user"), eq("test-client"), any(LocalDateTime.class));
        verify(tokenRepository, atLeast(2)).save(any(Token.class));
    }

    @Test
    void testRefreshToken_InvalidRefreshToken() {
        // Given
        RefreshTokenCommand command = new RefreshTokenCommand();
        command.setTenantId("test-tenant");
        command.setRefreshToken("invalid-refresh-token");
        command.setClientId("test-client");
        command.setClientSecret("test-secret");

        when(clientRepository.findByClientIdAndTenantId("test-client", "test-tenant"))
                .thenReturn(Optional.of(testClient));
        when(tokenRepository.findByToken("invalid-refresh-token"))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authenticationService.refreshToken(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("刷新令牌不存在");

        verify(clientRepository).findByClientIdAndTenantId("test-client", "test-tenant");
        verify(tokenRepository).findByToken("invalid-refresh-token");
        verifyNoInteractions(tokenDomainService);
    }

    @Test
    void testValidateToken_Success() {
        // Given
        String token = "test-token";
        String tenantId = "test-tenant";

        when(tokenRepository.findByToken("test-token"))
                .thenReturn(Optional.of(testToken));
        when(tokenBlacklistDomainService.isTokenBlacklistedByTenant("test-token", "test-tenant"))
                .thenReturn(false);
        when(tokenDomainService.validateToken(testToken)).thenReturn(true);
        when(tokenDomainService.isTokenForTenant(testToken, "test-tenant")).thenReturn(true);

        // When
        boolean isValid = authenticationService.validateToken(token, tenantId);

        // Then
        assertThat(isValid).isTrue();

        verify(tokenRepository).findByToken("test-token");
        verify(tokenBlacklistDomainService).isTokenBlacklistedByTenant("test-token", "test-tenant");
    }

    @Test
    void testValidateToken_TokenNotFound() {
        // Given
        String token = "invalid-token";
        String tenantId = "test-tenant";

        when(tokenBlacklistDomainService.isTokenBlacklistedByTenant("invalid-token", "test-tenant"))
                .thenReturn(false);
        when(tokenRepository.findByToken("invalid-token"))
                .thenReturn(Optional.empty());

        // When
        boolean isValid = authenticationService.validateToken(token, tenantId);

        // Then
        assertThat(isValid).isFalse();

        verify(tokenBlacklistDomainService).isTokenBlacklistedByTenant("invalid-token", "test-tenant");
        verify(tokenRepository).findByToken("invalid-token");
    }

    @Test
    void testValidateToken_TokenBlacklisted() {
        // Given
        String token = "test-token";
        String tenantId = "test-tenant";

        when(tokenBlacklistDomainService.isTokenBlacklistedByTenant("test-token", "test-tenant"))
                .thenReturn(true);

        // When
        boolean isValid = authenticationService.validateToken(token, tenantId);

        // Then
        assertThat(isValid).isFalse();

        verify(tokenBlacklistDomainService).isTokenBlacklistedByTenant("test-token", "test-tenant");
        verifyNoInteractions(tokenRepository);
    }

    @Test
    void testLogout_Success() {
        // Given
        String token = "test-token";
        String tenantId = "test-tenant";

        when(tokenRepository.findByToken("test-token"))
                .thenReturn(Optional.of(testToken));

        // When
        authenticationService.logout(token, tenantId);

        // Then
        verify(tokenRepository).findByToken("test-token");
        verify(tokenBlacklistDomainService).addToBlacklist(eq("test-token"), eq("test-tenant"), eq("LOGOUT"), any(LocalDateTime.class));
    }
}

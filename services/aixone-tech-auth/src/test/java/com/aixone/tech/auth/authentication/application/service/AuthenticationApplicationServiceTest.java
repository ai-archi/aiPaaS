package com.aixone.tech.auth.authentication.application.service;

import com.aixone.tech.auth.authentication.application.command.LoginCommand;
import com.aixone.tech.auth.authentication.application.command.RefreshTokenCommand;
import com.aixone.tech.auth.authentication.application.dto.auth.TokenResponse;
import com.aixone.tech.auth.authentication.domain.model.Client;
import com.aixone.tech.auth.authentication.domain.model.Token;
import com.aixone.tech.auth.authentication.domain.model.User;
import com.aixone.tech.auth.authentication.domain.repository.ClientRepository;
import com.aixone.tech.auth.authentication.domain.repository.TokenRepository;
import com.aixone.tech.auth.authentication.domain.service.TokenDomainService;
import com.aixone.tech.auth.authentication.domain.service.TokenBlacklistDomainService;
import com.aixone.tech.auth.authentication.infrastructure.persistence.repository.JpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
    private JpaUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

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

        User testUser = new User();
        testUser.setId(java.util.UUID.randomUUID());
        testUser.setUsername("test-user");
        testUser.setHashedPassword("$2a$10$hashedPassword"); // BCrypt hash
        testUser.setTenantId("test-tenant");
        testUser.setStatus("ACTIVE");

        when(clientRepository.findByClientIdAndTenantId("test-client", "test-tenant"))
                .thenReturn(Optional.of(testClient));
        when(userRepository.findByUsernameAndTenantId("test-user", "test-tenant"))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("test-password", "$2a$10$hashedPassword"))
                .thenReturn(true);
        when(tokenDomainService.generateAccessToken(eq("test-tenant"), anyString(), eq("test-client"), any(LocalDateTime.class)))
                .thenReturn(testToken);
        when(tokenDomainService.generateRefreshToken(eq("test-tenant"), anyString(), eq("test-client"), any(LocalDateTime.class)))
                .thenReturn(testToken);
        when(tokenRepository.save(any(Token.class))).thenReturn(testToken);

        // When
        TokenResponse response = authenticationService.login(command);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("test-token");
        assertThat(response.getTenantId()).isEqualTo("test-tenant");
        assertThat(response.getUserId()).isNotNull();

        verify(clientRepository).findByClientIdAndTenantId("test-client", "test-tenant");
        verify(userRepository).findByUsernameAndTenantId("test-user", "test-tenant");
        verify(passwordEncoder).matches("test-password", "$2a$10$hashedPassword");
        verify(tokenDomainService).generateAccessToken(eq("test-tenant"), anyString(), eq("test-client"), any(LocalDateTime.class));
        verify(tokenDomainService).generateRefreshToken(eq("test-tenant"), anyString(), eq("test-client"), any(LocalDateTime.class));
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
        verifyNoInteractions(userRepository);
        verifyNoInteractions(tokenDomainService);
        verifyNoInteractions(tokenRepository);
    }

    @Test
    void testLogin_UserNotFound() {
        // Given
        LoginCommand command = new LoginCommand();
        command.setTenantId("test-tenant");
        command.setUsername("invalid-user");
        command.setPassword("test-password");
        command.setClientId("test-client");
        command.setClientSecret("test-secret");

        when(clientRepository.findByClientIdAndTenantId("test-client", "test-tenant"))
                .thenReturn(Optional.of(testClient));
        when(userRepository.findByUsernameAndTenantId("invalid-user", "test-tenant"))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authenticationService.login(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("用户名或密码错误");

        verify(clientRepository).findByClientIdAndTenantId("test-client", "test-tenant");
        verify(userRepository).findByUsernameAndTenantId("invalid-user", "test-tenant");
        verifyNoInteractions(tokenDomainService);
        verifyNoInteractions(tokenRepository);
    }

    @Test
    void testLogin_UserInactive() {
        // Given
        LoginCommand command = new LoginCommand();
        command.setTenantId("test-tenant");
        command.setUsername("test-user");
        command.setPassword("test-password");
        command.setClientId("test-client");
        command.setClientSecret("test-secret");

        User inactiveUser = new User();
        inactiveUser.setId(UUID.randomUUID());
        inactiveUser.setUsername("test-user");
        inactiveUser.setHashedPassword("$2a$10$hashedPassword");
        inactiveUser.setTenantId("test-tenant");
        inactiveUser.setStatus("INACTIVE");

        when(clientRepository.findByClientIdAndTenantId("test-client", "test-tenant"))
                .thenReturn(Optional.of(testClient));
        when(userRepository.findByUsernameAndTenantId("test-user", "test-tenant"))
                .thenReturn(Optional.of(inactiveUser));

        // When & Then
        assertThatThrownBy(() -> authenticationService.login(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("用户账户已被禁用");

        verify(clientRepository).findByClientIdAndTenantId("test-client", "test-tenant");
        verify(userRepository).findByUsernameAndTenantId("test-user", "test-tenant");
        verifyNoInteractions(tokenDomainService);
        verifyNoInteractions(tokenRepository);
    }

    @Test
    void testLogin_InvalidPassword() {
        // Given
        LoginCommand command = new LoginCommand();
        command.setTenantId("test-tenant");
        command.setUsername("test-user");
        command.setPassword("wrong-password");
        command.setClientId("test-client");
        command.setClientSecret("test-secret");

        User testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setUsername("test-user");
        testUser.setHashedPassword("$2a$10$hashedPassword");
        testUser.setTenantId("test-tenant");
        testUser.setStatus("ACTIVE");

        when(clientRepository.findByClientIdAndTenantId("test-client", "test-tenant"))
                .thenReturn(Optional.of(testClient));
        when(userRepository.findByUsernameAndTenantId("test-user", "test-tenant"))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrong-password", "$2a$10$hashedPassword"))
                .thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authenticationService.login(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("用户名或密码错误");

        verify(clientRepository).findByClientIdAndTenantId("test-client", "test-tenant");
        verify(userRepository).findByUsernameAndTenantId("test-user", "test-tenant");
        verify(passwordEncoder).matches("wrong-password", "$2a$10$hashedPassword");
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
    void testRefreshToken_ExpiredToken() {
        // Given
        RefreshTokenCommand command = new RefreshTokenCommand();
        command.setTenantId("test-tenant");
        command.setRefreshToken("expired-refresh-token");
        command.setClientId("test-client");
        command.setClientSecret("test-secret");

        Token expiredToken = new Token();
        expiredToken.setToken("expired-refresh-token");
        expiredToken.setUserId("test-user");
        expiredToken.setClientId("test-client");
        expiredToken.setTenantId("test-tenant");
        expiredToken.setType(Token.TokenType.REFRESH);
        expiredToken.setExpiresAt(LocalDateTime.now().minusHours(1));

        when(clientRepository.findByClientIdAndTenantId("test-client", "test-tenant"))
                .thenReturn(Optional.of(testClient));
        when(tokenRepository.findByToken("expired-refresh-token"))
                .thenReturn(Optional.of(expiredToken));
        when(tokenDomainService.validateToken(expiredToken)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authenticationService.refreshToken(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("刷新令牌无效");

        verify(clientRepository).findByClientIdAndTenantId("test-client", "test-tenant");
        verify(tokenRepository).findByToken("expired-refresh-token");
        verify(tokenDomainService).validateToken(expiredToken);
    }

    @Test
    void testRefreshToken_WrongTenant() {
        // Given
        RefreshTokenCommand command = new RefreshTokenCommand();
        command.setTenantId("wrong-tenant");
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

        when(clientRepository.findByClientIdAndTenantId("test-client", "wrong-tenant"))
                .thenReturn(Optional.of(testClient));
        when(tokenRepository.findByToken("test-refresh-token"))
                .thenReturn(Optional.of(refreshToken));
        when(tokenDomainService.validateToken(refreshToken)).thenReturn(true);
        when(tokenDomainService.isTokenForTenant(refreshToken, "wrong-tenant")).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authenticationService.refreshToken(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("刷新令牌不属于指定租户");

        verify(clientRepository).findByClientIdAndTenantId("test-client", "wrong-tenant");
        verify(tokenRepository).findByToken("test-refresh-token");
        verify(tokenDomainService).validateToken(refreshToken);
        verify(tokenDomainService).isTokenForTenant(refreshToken, "wrong-tenant");
    }

    @Test
    void testRefreshToken_WrongType() {
        // Given
        RefreshTokenCommand command = new RefreshTokenCommand();
        command.setTenantId("test-tenant");
        command.setRefreshToken("access-token");
        command.setClientId("test-client");
        command.setClientSecret("test-secret");

        Token accessToken = new Token();
        accessToken.setToken("access-token");
        accessToken.setUserId("test-user");
        accessToken.setClientId("test-client");
        accessToken.setTenantId("test-tenant");
        accessToken.setType(Token.TokenType.ACCESS); // 不是REFRESH类型
        accessToken.setExpiresAt(LocalDateTime.now().plusHours(1));

        when(clientRepository.findByClientIdAndTenantId("test-client", "test-tenant"))
                .thenReturn(Optional.of(testClient));
        when(tokenRepository.findByToken("access-token"))
                .thenReturn(Optional.of(accessToken));
        when(tokenDomainService.validateToken(accessToken)).thenReturn(true);
        when(tokenDomainService.isTokenForTenant(accessToken, "test-tenant")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authenticationService.refreshToken(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("令牌类型错误");

        verify(clientRepository).findByClientIdAndTenantId("test-client", "test-tenant");
        verify(tokenRepository).findByToken("access-token");
        verify(tokenDomainService).validateToken(accessToken);
        verify(tokenDomainService).isTokenForTenant(accessToken, "test-tenant");
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
    void testValidateToken_ExpiredToken() {
        // Given
        String token = "expired-token";
        String tenantId = "test-tenant";

        Token expiredToken = new Token();
        expiredToken.setToken("expired-token");
        expiredToken.setTenantId("test-tenant");
        expiredToken.setExpiresAt(LocalDateTime.now().minusHours(1));

        when(tokenBlacklistDomainService.isTokenBlacklistedByTenant("expired-token", "test-tenant"))
                .thenReturn(false);
        when(tokenRepository.findByToken("expired-token"))
                .thenReturn(Optional.of(expiredToken));
        when(tokenDomainService.validateToken(expiredToken)).thenReturn(false);

        // When
        boolean isValid = authenticationService.validateToken(token, tenantId);

        // Then
        assertThat(isValid).isFalse();

        verify(tokenBlacklistDomainService).isTokenBlacklistedByTenant("expired-token", "test-tenant");
        verify(tokenRepository).findByToken("expired-token");
        verify(tokenDomainService).validateToken(expiredToken);
    }

    @Test
    void testValidateToken_WrongTenant() {
        // Given
        String token = "test-token";
        String tenantId = "wrong-tenant";

        when(tokenBlacklistDomainService.isTokenBlacklistedByTenant("test-token", "wrong-tenant"))
                .thenReturn(false);
        when(tokenRepository.findByToken("test-token"))
                .thenReturn(Optional.of(testToken));
        when(tokenDomainService.validateToken(testToken)).thenReturn(true);
        when(tokenDomainService.isTokenForTenant(testToken, "wrong-tenant")).thenReturn(false);

        // When
        boolean isValid = authenticationService.validateToken(token, tenantId);

        // Then
        assertThat(isValid).isFalse();

        verify(tokenBlacklistDomainService).isTokenBlacklistedByTenant("test-token", "wrong-tenant");
        verify(tokenRepository).findByToken("test-token");
        verify(tokenDomainService).validateToken(testToken);
        verify(tokenDomainService).isTokenForTenant(testToken, "wrong-tenant");
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
        verify(tokenRepository).delete("test-token");
        verify(tokenRepository).deleteByUserIdAndTenantId(testToken.getUserId(), tenantId);
    }

    @Test
    void testLogout_TokenNotFound() {
        // Given
        String token = "invalid-token";
        String tenantId = "test-tenant";

        when(tokenRepository.findByToken("invalid-token"))
                .thenReturn(Optional.empty());

        // When
        authenticationService.logout(token, tenantId);

        // Then
        verify(tokenRepository).findByToken("invalid-token");
        verifyNoInteractions(tokenBlacklistDomainService);
    }

    @Test
    void testLogout_WrongTenant() {
        // Given
        String token = "test-token";
        String tenantId = "wrong-tenant";

        when(tokenRepository.findByToken("test-token"))
                .thenReturn(Optional.of(testToken));

        // When & Then
        assertThatThrownBy(() -> authenticationService.logout(token, tenantId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("令牌不属于指定租户");

        verify(tokenRepository).findByToken("test-token");
        verifyNoInteractions(tokenBlacklistDomainService);
    }
}

package com.aixone.tech.auth.authentication.application.service;

import com.aixone.tech.auth.authentication.application.dto.management.*;
import com.aixone.tech.auth.authentication.domain.model.Token;
import com.aixone.tech.auth.authentication.domain.model.User;
import com.aixone.tech.auth.authentication.domain.repository.TokenRepository;
import com.aixone.tech.auth.authentication.domain.repository.UserRepository;
import com.aixone.tech.auth.authentication.infrastructure.persistence.repository.JpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * 管理应用服务测试
 */
@ExtendWith(MockitoExtension.class)
class ManagementApplicationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JpaUserRepository jpaUserRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private ManagementApplicationService managementService;

    private User testUser;
    private Token testToken;

    @BeforeEach
    void setUp() {
        // 手动创建service实例，确保mock正确注入
        managementService = new ManagementApplicationService(
            userRepository,
            jpaUserRepository,
            tokenRepository,
            passwordEncoder
        );
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setUsername("test-user");
        testUser.setHashedPassword("$2a$10$hashedPassword");
        testUser.setEmail("test@example.com");
        testUser.setPhone("13800138000");
        testUser.setTenantId("test-tenant");
        testUser.setStatus("ACTIVE");
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        testToken = new Token();
        testToken.setToken("test-token");
        testToken.setUserId(testUser.getId().toString());
        testToken.setClientId("test-client");
        testToken.setTenantId("test-tenant");
        testToken.setType(Token.TokenType.ACCESS);
        testToken.setExpiresAt(LocalDateTime.now().plusHours(1));
        testToken.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateUser_Success() {
        // Given
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("new-user");
        request.setPassword("new-password");
        request.setEmail("new@example.com");
        request.setPhone("13900139000");
        request.setTenantId("test-tenant");

        when(userRepository.existsByUsernameAndTenantId("new-user", "test-tenant"))
                .thenReturn(false);
        when(passwordEncoder.encode("new-password"))
                .thenReturn("$2a$10$encodedPassword");
        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setUsername("new-user");
        savedUser.setEmail("new@example.com");
        savedUser.setPhone("13900139000");
        savedUser.setTenantId("test-tenant");
        savedUser.setStatus("ACTIVE");
        savedUser.setCreatedAt(LocalDateTime.now());
        savedUser.setUpdatedAt(LocalDateTime.now());
        
        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        // When
        UserResponse response = managementService.createUser(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("new-user");
        assertThat(response.getEmail()).isEqualTo("new@example.com");
        assertThat(response.getPhone()).isEqualTo("13900139000");
        assertThat(response.getTenantId()).isEqualTo("test-tenant");
        assertThat(response.getStatus()).isEqualTo("ACTIVE");

        verify(userRepository).existsByUsernameAndTenantId("new-user", "test-tenant");
        verify(passwordEncoder).encode("new-password");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testCreateUser_UsernameExists() {
        // Given
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("test-user");
        request.setPassword("password");
        request.setTenantId("test-tenant");

        when(userRepository.existsByUsernameAndTenantId("test-user", "test-tenant"))
                .thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> managementService.createUser(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("用户名已存在");

        verify(userRepository).existsByUsernameAndTenantId("test-user", "test-tenant");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testGetUserById_Success() {
        // Given
        UUID userId = testUser.getId();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(testUser));

        // When
        Optional<UserResponse> response = managementService.getUserById(userId);

        // Then
        assertThat(response).isPresent();
        assertThat(response.get().getUsername()).isEqualTo("test-user");
        assertThat(response.get().getEmail()).isEqualTo("test@example.com");

        verify(userRepository).findById(userId);
    }

    @Test
    void testGetUserById_NotFound() {
        // Given
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        // When
        Optional<UserResponse> response = managementService.getUserById(userId);

        // Then
        assertThat(response).isEmpty();

        verify(userRepository).findById(userId);
    }

    @Test
    void testUpdateUser_Success() {
        // Given
        UUID userId = testUser.getId();
        UserUpdateRequest request = new UserUpdateRequest();
        request.setEmail("updated@example.com");
        request.setPhone("13900139000");
        request.setStatus("INACTIVE");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        UserResponse response = managementService.updateUser(userId, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("updated@example.com");
        assertThat(response.getPhone()).isEqualTo("13900139000");
        assertThat(response.getStatus()).isEqualTo("INACTIVE");

        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdateUser_UserNotFound() {
        // Given
        UUID userId = UUID.randomUUID();
        UserUpdateRequest request = new UserUpdateRequest();

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> managementService.updateUser(userId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("用户不存在");

        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteUser_Success() {
        // Given
        UUID userId = testUser.getId();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).deleteById(userId);
        doNothing().when(tokenRepository).deleteByUserIdAndTenantId(userId.toString(), "test-tenant");

        // When
        managementService.deleteUser(userId);

        // Then
        verify(userRepository).findById(userId);
        verify(tokenRepository).deleteByUserIdAndTenantId(userId.toString(), "test-tenant");
        verify(userRepository).deleteById(userId);
    }

    @Test
    void testRevokeToken_Success() {
        // Given
        String token = "test-token";
        String tenantId = "test-tenant";

        when(tokenRepository.findByToken(token))
                .thenReturn(Optional.of(testToken));
        doNothing().when(tokenRepository).delete(token);

        // When
        managementService.revokeToken(token, tenantId);

        // Then
        verify(tokenRepository).findByToken(token);
        verify(tokenRepository).delete(token);
    }

    @Test
    void testRevokeToken_TokenNotFound() {
        // Given
        String token = "invalid-token";
        String tenantId = "test-tenant";

        when(tokenRepository.findByToken(token))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> managementService.revokeToken(token, tenantId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Token不存在");

        verify(tokenRepository).findByToken(token);
        verifyNoMoreInteractions(tokenRepository);
    }

    @Test
    void testRevokeUserTokens_Success() {
        // Given
        String userId = testUser.getId().toString();
        String tenantId = "test-tenant";

        doNothing().when(tokenRepository).deleteByUserIdAndTenantId(userId, tenantId);

        // When
        managementService.revokeUserTokens(userId, tenantId);

        // Then
        verify(tokenRepository).deleteByUserIdAndTenantId(userId, tenantId);
    }

    @Test
    void testLogoutAllDevices_Success() {
        // Given
        String userId = testUser.getId().toString();
        String tenantId = "test-tenant";

        doNothing().when(tokenRepository).deleteByUserIdAndTenantId(userId, tenantId);

        // When
        managementService.logoutAllDevices(userId, tenantId);

        // Then
        // logoutAllDevices内部调用revokeUserTokens，所以验证的是同一个方法
        verify(tokenRepository).deleteByUserIdAndTenantId(userId, tenantId);
    }

    @Test
    void testGetActiveUsers_Success() {
        // Given
        String tenantId = "test-tenant";

        Token activeToken = new Token();
        activeToken.setToken("active-token");
        activeToken.setUserId(testUser.getId().toString());
        activeToken.setClientId("test-client");
        activeToken.setTenantId(tenantId);
        activeToken.setType(Token.TokenType.ACCESS);
        activeToken.setExpiresAt(LocalDateTime.now().plusHours(1));
        activeToken.setCreatedAt(LocalDateTime.now());

        when(tokenRepository.findByTenantId(tenantId))
                .thenReturn(List.of(activeToken));
        when(userRepository.findById(testUser.getId()))
                .thenReturn(Optional.of(testUser));
        when(tokenRepository.findByUserIdAndTenantId(testUser.getId().toString(), tenantId))
                .thenReturn(List.of(activeToken));

        // When
        List<ActiveUserResponse> response = managementService.getActiveUsers(tenantId);

        // Then
        assertThat(response).isNotEmpty();
        assertThat(response.get(0).getUsername()).isEqualTo("test-user");
        assertThat(response.get(0).getActiveDeviceCount()).isEqualTo(1);

        verify(tokenRepository).findByTenantId(tenantId);
        verify(userRepository).findById(testUser.getId());
    }

    @Test
    void testGetActiveUsers_NoActiveUsers() {
        // Given
        String tenantId = "test-tenant";

        when(tokenRepository.findByTenantId(tenantId))
                .thenReturn(List.of());

        // When
        List<ActiveUserResponse> response = managementService.getActiveUsers(tenantId);

        // Then
        assertThat(response).isEmpty();

        verify(tokenRepository).findByTenantId(tenantId);
    }

    @Test
    void testGetUserDevices_Success() {
        // Given
        String userId = testUser.getId().toString();
        String tenantId = "test-tenant";

        Token device1 = new Token();
        device1.setToken("token1-12345678901234567890");
        device1.setClientId("client1");
        device1.setCreatedAt(LocalDateTime.now().minusHours(1));
        device1.setExpiresAt(LocalDateTime.now().plusHours(1));
        device1.setType(Token.TokenType.ACCESS);

        Token device2 = new Token();
        device2.setToken("token2-12345678901234567890");
        device2.setClientId("client2");
        device2.setCreatedAt(LocalDateTime.now().minusMinutes(30));
        device2.setExpiresAt(LocalDateTime.now().plusHours(1));
        device2.setType(Token.TokenType.ACCESS);

        when(tokenRepository.findByUserIdAndTenantId(userId, tenantId))
                .thenReturn(List.of(device1, device2));

        // When
        List<ActiveUserResponse.DeviceInfo> devices = managementService.getUserDevices(userId, tenantId);

        // Then
        assertThat(devices).hasSize(2);
        assertThat(devices.get(0).getClientId()).isEqualTo("client1");
        assertThat(devices.get(1).getClientId()).isEqualTo("client2");

        verify(tokenRepository).findByUserIdAndTenantId(userId, tenantId);
    }

    @Test
    void testGetUserDevices_NoDevices() {
        // Given
        String userId = testUser.getId().toString();
        String tenantId = "test-tenant";

        when(tokenRepository.findByUserIdAndTenantId(userId, tenantId))
                .thenReturn(List.of());

        // When
        List<ActiveUserResponse.DeviceInfo> devices = managementService.getUserDevices(userId, tenantId);

        // Then
        assertThat(devices).isEmpty();

        verify(tokenRepository).findByUserIdAndTenantId(userId, tenantId);
    }

    @Test
    void testGetUsers_Success() {
        // Given
        String tenantId = "test-tenant";
        PageRequest pageRequest = PageRequest.of(0, 10);

        when(jpaUserRepository.findByTenantId(tenantId))
                .thenReturn(List.of(testUser));

        // When
        Page<UserResponse> response = managementService.getUsers(tenantId, pageRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getUsername()).isEqualTo("test-user");

        verify(jpaUserRepository).findByTenantId(tenantId);
    }

    @Test
    void testUpdateUserPassword_Success() {
        // Given
        UUID userId = testUser.getId();
        String newPassword = "newPassword123";

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(newPassword))
                .thenReturn("$2a$10$newEncodedPassword");
        when(userRepository.save(any(User.class)))
                .thenReturn(testUser);

        // When
        managementService.updateUserPassword(userId, newPassword);

        // Then
        verify(userRepository).findById(userId);
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdateUserPassword_UserNotFound() {
        // Given
        UUID userId = UUID.randomUUID();
        String newPassword = "newPassword123";

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> managementService.updateUserPassword(userId, newPassword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("用户不存在");

        verify(userRepository).findById(userId);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void testResetUserPassword_Success() {
        // Given
        UUID userId = testUser.getId();
        
        // 确保testUser有所有必要的字段
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("TempPassword123!"))
                .thenReturn("$2a$10$resetEncodedPassword");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        managementService.resetUserPassword(userId);

        // Then
        verify(userRepository).findById(userId);
        verify(passwordEncoder).encode("TempPassword123!");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testResetUserPassword_UserNotFound() {
        // Given
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> managementService.resetUserPassword(userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("用户不存在");

        verify(userRepository).findById(userId);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void testRevokeToken_WrongTenant() {
        // Given
        String token = "test-token";
        String tenantId = "wrong-tenant";

        Token tokenWithDifferentTenant = new Token();
        tokenWithDifferentTenant.setToken("test-token");
        tokenWithDifferentTenant.setTenantId("test-tenant");

        when(tokenRepository.findByToken(token))
                .thenReturn(Optional.of(tokenWithDifferentTenant));

        // When & Then
        assertThatThrownBy(() -> managementService.revokeToken(token, tenantId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Token不属于指定租户");

        verify(tokenRepository).findByToken(token);
        verifyNoMoreInteractions(tokenRepository);
    }

    @Test
    void testLogoutDevice_Success() {
        // Given
        String userId = testUser.getId().toString();
        String deviceId = "test-token-1234567890"; // token的前20位
        String tenantId = "test-tenant";

        Token deviceToken = new Token();
        deviceToken.setToken("test-token-1234567890-remainder");
        deviceToken.setUserId(userId);
        deviceToken.setTenantId(tenantId);

        when(tokenRepository.findByUserIdAndTenantId(userId, tenantId))
                .thenReturn(List.of(deviceToken));
        doNothing().when(tokenRepository).delete(deviceToken.getToken());

        // When
        managementService.logoutDevice(userId, deviceId, tenantId);

        // Then
        verify(tokenRepository).findByUserIdAndTenantId(userId, tenantId);
        verify(tokenRepository).delete(deviceToken.getToken());
    }

    @Test
    void testLogoutDevice_DeviceNotFound() {
        // Given
        String userId = testUser.getId().toString();
        String deviceId = "non-existent-device";
        String tenantId = "test-tenant";

        when(tokenRepository.findByUserIdAndTenantId(userId, tenantId))
                .thenReturn(List.of());

        // When & Then
        assertThatThrownBy(() -> managementService.logoutDevice(userId, deviceId, tenantId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("设备不存在");

        verify(tokenRepository).findByUserIdAndTenantId(userId, tenantId);
        verifyNoMoreInteractions(tokenRepository);
    }

    @Test
    void testGetTokens_Success() {
        // Given
        String tenantId = "test-tenant";

        Token token1 = new Token();
        token1.setToken("token1");
        token1.setUserId("user1");
        token1.setClientId("client1");
        token1.setTenantId(tenantId);
        token1.setType(Token.TokenType.ACCESS);
        token1.setExpiresAt(LocalDateTime.now().plusHours(1));
        token1.setCreatedAt(LocalDateTime.now());

        Token token2 = new Token();
        token2.setToken("token2");
        token2.setUserId("user2");
        token2.setClientId("client2");
        token2.setTenantId(tenantId);
        token2.setType(Token.TokenType.REFRESH);
        token2.setExpiresAt(LocalDateTime.now().plusDays(7));
        token2.setCreatedAt(LocalDateTime.now());

        when(tokenRepository.findByTenantId(tenantId))
                .thenReturn(List.of(token1, token2));

        // When
        List<TokenInfoResponse> response = managementService.getTokens(tenantId);

        // Then
        assertThat(response).hasSize(2);
        assertThat(response.get(0).getToken()).isEqualTo("token1");
        assertThat(response.get(1).getToken()).isEqualTo("token2");

        verify(tokenRepository).findByTenantId(tenantId);
    }
}


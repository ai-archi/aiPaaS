package com.aixone.tech.auth.config;

import com.aixone.audit.application.AuditService;
import com.aixone.audit.domain.AuditLog;
import com.aixone.audit.domain.AuditLogRepository;
import com.aixone.audit.infrastructure.AuditEventPublisher;
import com.aixone.common.security.JwtUtils;
import com.aixone.tech.auth.authentication.domain.model.Client;
import com.aixone.tech.auth.authentication.domain.model.Token;
import com.aixone.tech.auth.authentication.domain.model.User;
import com.aixone.tech.auth.authentication.domain.repository.ClientRepository;
import com.aixone.tech.auth.authentication.domain.repository.TokenRepository;
import com.aixone.tech.auth.authentication.domain.repository.UserRepository;
import com.aixone.tech.auth.authentication.infrastructure.persistence.mapper.UserMapper;
import com.aixone.tech.auth.authentication.infrastructure.persistence.repository.JpaUserRepository;
import com.aixone.tech.auth.authentication.infrastructure.persistence.repository.UserJpaRepository;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 测试数据配置
 */
@TestConfiguration
public class TestDataConfig {


    @Bean
    @Primary
    public ClientRepository testClientRepository() {
        return new ClientRepository() {
            @Override
            public Optional<Client> findByClientIdAndTenantId(String clientId, String tenantId) {
                if ("test-client".equals(clientId) && "test-tenant".equals(tenantId)) {
                    Client client = new Client();
                    client.setClientId("test-client");
                    client.setTenantId("test-tenant");
                    client.setClientSecret("test-secret");
                    client.setRedirectUri("http://localhost:3000/callback");
                    client.setScopes("read write");
                    client.setGrantTypes("password refresh_token");
                    return Optional.of(client);
                }
                return Optional.empty();
            }

            @Override
            public java.util.List<Client> findByTenantId(String tenantId) {
                return java.util.List.of();
            }

            @Override
            public Client save(Client client) {
                return client;
            }

            @Override
            public Client update(Client client) {
                return client;
            }

            @Override
            public void delete(String clientId, String tenantId) {
                // 测试实现
            }

            @Override
            public boolean existsByClientIdAndTenantId(String clientId, String tenantId) {
                return "test-client".equals(clientId) && "test-tenant".equals(tenantId);
            }

            @Override
            public Optional<Client> findByClientId(String clientId) {
                if ("test-client".equals(clientId)) {
                    Client client = new Client();
                    client.setClientId("test-client");
                    client.setTenantId("test-tenant");
                    client.setClientSecret("test-secret");
                    client.setRedirectUri("http://localhost:3000/callback");
                    client.setScopes("read write");
                    client.setGrantTypes("password refresh_token");
                    return Optional.of(client);
                }
                return Optional.empty();
            }
        };
    }

    @Bean
    @Primary
    public TokenRepository testTokenRepository(org.springframework.context.ApplicationContext applicationContext) {
        return new TokenRepository() {
            // 生成有效的JWT refresh token（仅用于测试）
            private String generateTestRefreshToken() {
                // 从ApplicationContext获取JwtUtils
                JwtUtils jwtUtils = applicationContext.getBean(JwtUtils.class);
                if (jwtUtils != null) {
                    // 使用JwtUtils生成有效的JWT refresh token
                    String userId = "test-user-id"; // 使用固定的用户ID
                    String tenantId = "test-tenant";
                    String clientId = "test-client";
                    return jwtUtils.generateRefreshToken(userId, tenantId, clientId);
                }
                // 如果JwtUtils不可用，返回一个占位符（这种情况不应该发生）
                return "test-refresh-token";
            }

            @Override
            public Optional<Token> findByToken(String token) {
                // 如果请求的是 "test-refresh-token"，生成一个有效的JWT token
                if ("test-refresh-token".equals(token)) {
                    String validJwtToken = generateTestRefreshToken();
                    Token refreshToken = new Token();
                    refreshToken.setToken(validJwtToken);
                    refreshToken.setTenantId("test-tenant");
                    refreshToken.setUserId("test-user-id"); // 使用与JWT中一致的userId
                    refreshToken.setClientId("test-client");
                    refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7)); // Refresh token有效期7天
                    refreshToken.setType(Token.TokenType.REFRESH);
                    return Optional.of(refreshToken);
                }
                // 如果传入的是有效的JWT token，也尝试查找
                try {
                    JwtUtils jwtUtils = applicationContext.getBean(JwtUtils.class);
                    if (jwtUtils != null && jwtUtils.validateToken(token)) {
                        // 这是一个有效的JWT token，创建对应的Token对象
                        String userId = jwtUtils.getUserIdFromToken(token);
                        String tenantId = jwtUtils.getTenantIdFromToken(token);
                        String clientId = jwtUtils.getClientIdFromToken(token);
                        if (userId != null && tenantId != null && clientId != null) {
                            Token refreshToken = new Token();
                            refreshToken.setToken(token);
                            refreshToken.setTenantId(tenantId);
                            refreshToken.setUserId(userId);
                            refreshToken.setClientId(clientId);
                            refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7)); // Refresh token有效期7天
                            refreshToken.setType(Token.TokenType.REFRESH);
                            return Optional.of(refreshToken);
                        }
                    }
                } catch (Exception e) {
                    // 忽略异常，继续查找
                }
                return Optional.empty();
            }

            @Override
            public java.util.List<Token> findByUserIdAndTenantId(String userId, String tenantId) {
                return java.util.List.of();
            }

            @Override
            public java.util.List<Token> findByClientIdAndTenantId(String clientId, String tenantId) {
                return java.util.List.of();
            }

            @Override
            public Token save(Token token) {
                return token;
            }

            @Override
            public void delete(String token) {
                // 测试实现
            }

            @Override
            public void deleteByUserIdAndTenantId(String userId, String tenantId) {
                // 测试实现
            }

            @Override
            public void deleteExpiredTokens() {
                // 测试实现
            }

            @Override
            public List<Token> findByUserIdAndClientIdAndTenantId(String userId, String clientId, String tenantId) {
                return List.of();
            }

            @Override
            public void deleteByClientIdAndTenantId(String clientId, String tenantId) {
                // 测试实现
            }

            @Override
            public boolean existsByToken(String token) {
                // 如果token是 "test-refresh-token" 或者是有效的JWT token，返回true
                if ("test-refresh-token".equals(token)) {
                    return true;
                }
                // 也可以检查是否是有效的JWT token（可选）
                try {
                    JwtUtils jwtUtils = applicationContext.getBean(JwtUtils.class);
                    if (jwtUtils != null && jwtUtils.validateToken(token)) {
                        return true;
                    }
                } catch (Exception e) {
                    // 忽略异常
                }
                return false;
            }

            @Override
            public List<Token> findExpiredTokens() {
                return List.of();
            }

            @Override
            public List<Token> findByTenantId(String tenantId) {
                return List.of();
            }
        };
    }

    @Bean(name = "testUserRepository")
    public UserRepository testUserRepository() {
        return new UserRepository() {
            @Override
            public Optional<User> findByUsernameAndTenantId(String username, String tenantId) {
                if ("test-user".equals(username) && "test-tenant".equals(tenantId)) {
                    // 使用固定的UUID，确保测试一致性
                    UUID userId = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11");
                    User user = new User(
                        userId,
                        "test-user",
                        "$2a$10$JEABMlBbva9pw5YQ0LlCHeidpfHK1U0LZbdIIrvKQPgG5ydVPoJaW", // "test-password" 的hash
                        "test@example.com",
                        "test-tenant"
                    );
                    return Optional.of(user);
                }
                return Optional.empty();
            }

            @Override
            public Optional<User> findById(UUID id) {
                User user = new User(
                    id,
                    "test-user",
                    "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy", // "test-password" 的hash
                    "test@example.com",
                    "test-tenant"
                );
                return Optional.of(user);
            }

            @Override
            public User save(User user) {
                if (user.getId() == null) {
                    user.setId(UUID.randomUUID());
                }
                return user;
            }

            @Override
            public void deleteById(UUID id) {
                // 测试实现
            }

            @Override
            public boolean existsByUsernameAndTenantId(String username, String tenantId) {
                return "test-user".equals(username) && "test-tenant".equals(tenantId);
            }

            @Override
            public List<User> findByTenantId(String tenantId) {
                if ("test-tenant".equals(tenantId)) {
                    // 使用固定的UUID，确保测试一致性
                    UUID userId = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11");
                    User user = new User(
                        userId,
                        "test-user",
                        "$2a$10$JEABMlBbva9pw5YQ0LlCHeidpfHK1U0LZbdIIrvKQPgG5ydVPoJaW", // "test-password" 的hash
                        "test@example.com",
                        "test-tenant"
                    );
                    return List.of(user);
                }
                return List.of();
            }
        };
    }

    @Bean
    @Primary
    public JpaUserRepository testJpaUserRepository(@org.springframework.beans.factory.annotation.Qualifier("testUserRepository") UserRepository userRepository) {
        // 创建一个简单的实现，委托给testUserRepository
        UserJpaRepository jpaRepository = Mockito.mock(UserJpaRepository.class);
        UserMapper mapper = Mockito.mock(UserMapper.class);
        
        // 配置mapper行为：domain和entity之间的转换直接返回原对象
        // 注意：这里不需要mock toDomain，因为JpaUserRepository会直接调用testUserRepository
        Mockito.when(mapper.toEntity(Mockito.any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        return new JpaUserRepository(jpaRepository, mapper) {
            @Override
            public Optional<User> findByUsernameAndTenantId(String username, String tenantId) {
                // 直接委托给testUserRepository，确保使用正确的mock数据
                return userRepository.findByUsernameAndTenantId(username, tenantId);
            }

            @Override
            public Optional<User> findById(UUID id) {
                return userRepository.findById(id);
            }

            @Override
            public User save(User user) {
                return userRepository.save(user);
            }

            @Override
            public void deleteById(UUID id) {
                userRepository.deleteById(id);
            }

            @Override
            public boolean existsByUsernameAndTenantId(String username, String tenantId) {
                return userRepository.existsByUsernameAndTenantId(username, tenantId);
            }

            @Override
            public List<User> findByTenantId(String tenantId) {
                return userRepository.findByTenantId(tenantId);
            }
        };
    }

    @Bean
    @Primary
    public AuditService testAuditService() {
        // 创建Mock的AuditLogRepository和AuditEventPublisher
        AuditLogRepository mockRepository = Mockito.mock(AuditLogRepository.class);
        AuditEventPublisher mockPublisher = Mockito.mock(AuditEventPublisher.class);
        
        // 创建AuditService实例，但需要拦截AuditLog的创建
        AuditService auditService = new AuditService(mockRepository, mockPublisher) {
            // 重写方法以确保AuditLog在创建时就有ID
            @Override
            public AuditLog logLoginSuccess(String userId, String clientIp, String userAgent) {
                AuditLog auditLog = new AuditLog(1L, userId, "LOGIN", "AUTH_SERVICE", "SUCCESS");
                auditLog.setClientIp(clientIp);
                auditLog.setUserAgent(userAgent);
                auditLog.setTimestamp(java.time.LocalDateTime.now());
                return mockRepository.save(auditLog);
            }
            
            @Override
            public AuditLog logLoginFailure(String userId, String reason, String clientIp, String userAgent) {
                AuditLog auditLog = new AuditLog(1L, userId, "LOGIN", "AUTH_SERVICE", "FAILURE");
                auditLog.setClientIp(clientIp);
                auditLog.setUserAgent(userAgent);
                auditLog.setErrorMessage(reason);
                auditLog.setTimestamp(java.time.LocalDateTime.now());
                return mockRepository.save(auditLog);
            }
            
            @Override
            public AuditLog logLogout(String userId, String clientIp, String userAgent) {
                AuditLog auditLog = new AuditLog(1L, userId, "LOGOUT", "AUTH_SERVICE", "SUCCESS");
                auditLog.setClientIp(clientIp);
                auditLog.setUserAgent(userAgent);
                auditLog.setTimestamp(java.time.LocalDateTime.now());
                return mockRepository.save(auditLog);
            }
        };
        
        // 配置Mock行为：当调用save时返回传入的auditLog
        Mockito.when(mockRepository.save(Mockito.any(AuditLog.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        
        return auditService;
    }
}

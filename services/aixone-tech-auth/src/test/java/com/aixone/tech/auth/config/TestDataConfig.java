package com.aixone.tech.auth.config;

import com.aixone.tech.auth.authentication.domain.model.Client;
import com.aixone.tech.auth.authentication.domain.model.Token;
import com.aixone.tech.auth.authentication.domain.repository.ClientRepository;
import com.aixone.tech.auth.authentication.domain.repository.TokenRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    public TokenRepository testTokenRepository() {
        return new TokenRepository() {
            @Override
            public Optional<Token> findByToken(String token) {
                if ("test-refresh-token".equals(token)) {
                    Token refreshToken = new Token();
                    refreshToken.setToken("test-refresh-token");
                    refreshToken.setTenantId("test-tenant");
                    refreshToken.setUserId("test-user");
                    refreshToken.setClientId("test-client");
                    refreshToken.setExpiresAt(LocalDateTime.now().plusHours(1));
                    refreshToken.setType(Token.TokenType.REFRESH);
                    return Optional.of(refreshToken);
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
                return "test-refresh-token".equals(token);
            }

            @Override
            public List<Token> findExpiredTokens() {
                return List.of();
            }
        };
    }
}

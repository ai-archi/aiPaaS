package com.aixone.tech.auth.authentication.application.service;

import com.aixone.tech.auth.authentication.application.command.LoginCommand;
import com.aixone.tech.auth.authentication.application.command.RefreshTokenCommand;
import com.aixone.tech.auth.authentication.application.dto.auth.LoginRequest;
import com.aixone.tech.auth.authentication.application.dto.auth.TokenResponse;
import com.aixone.tech.auth.authentication.domain.event.UserLoginEvent;
import com.aixone.tech.auth.authentication.domain.event.TokenIssuedEvent;
import com.aixone.tech.auth.authentication.domain.model.Client;
import com.aixone.tech.auth.authentication.domain.model.Token;
import com.aixone.tech.auth.authentication.domain.repository.ClientRepository;
import com.aixone.tech.auth.authentication.domain.repository.TokenRepository;
import com.aixone.tech.auth.authentication.domain.service.TokenDomainService;
import com.aixone.tech.auth.authentication.domain.service.TokenBlacklistDomainService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 认证应用服务
 * 负责协调认证相关的业务用例
 */
@Service
@Transactional
public class AuthenticationApplicationService {

    private final ClientRepository clientRepository;
    private final TokenRepository tokenRepository;
    private final TokenDomainService tokenDomainService;
    private final TokenBlacklistDomainService tokenBlacklistDomainService;
    private final ApplicationEventPublisher eventPublisher;

    public AuthenticationApplicationService(ClientRepository clientRepository,
                                          TokenRepository tokenRepository,
                                          TokenDomainService tokenDomainService,
                                          TokenBlacklistDomainService tokenBlacklistDomainService,
                                          ApplicationEventPublisher eventPublisher) {
        this.clientRepository = clientRepository;
        this.tokenRepository = tokenRepository;
        this.tokenDomainService = tokenDomainService;
        this.tokenBlacklistDomainService = tokenBlacklistDomainService;
        this.eventPublisher = eventPublisher;
    }

    /**
     * 用户登录
     */
    public TokenResponse login(LoginCommand command) {
        // 1. 验证客户端
        Client client = validateClient(command.getClientId(), command.getTenantId(), command.getClientSecret());
        
        // 2. 验证用户凭据（这里需要调用目录服务）
        String userId = validateUserCredentials(command.getTenantId(), command.getUsername(), command.getPassword());
        
        // 3. 生成令牌
        TokenResponse tokenResponse = generateTokens(command.getTenantId(), userId, command.getClientId());
        
        // 4. 发布登录事件
        UserLoginEvent loginEvent = new UserLoginEvent(
            userId, command.getTenantId(), command.getClientId(), 
            "password", command.getClientIp(), command.getUserAgent()
        );
        eventPublisher.publishEvent(loginEvent);
        
        return tokenResponse;
    }

    /**
     * 刷新令牌
     */
    public TokenResponse refreshToken(RefreshTokenCommand command) {
        // 1. 验证客户端
        Client client = validateClient(command.getClientId(), command.getTenantId(), command.getClientSecret());
        
        // 2. 验证刷新令牌
        Token refreshToken = validateRefreshToken(command.getRefreshToken(), command.getTenantId());
        
        // 3. 生成新的令牌
        TokenResponse tokenResponse = generateTokens(command.getTenantId(), refreshToken.getUserId(), command.getClientId());
        
        // 4. 删除旧的刷新令牌
        tokenRepository.delete(refreshToken.getToken());
        
        return tokenResponse;
    }

    /**
     * 用户登出
     */
    public void logout(String token, String tenantId) {
        // 1. 查找令牌
        Optional<Token> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isPresent()) {
            Token tokenEntity = tokenOpt.get();
            
            // 2. 验证租户
            if (!tokenEntity.belongsToTenant(tenantId)) {
                throw new IllegalArgumentException("令牌不属于指定租户");
            }
            
            // 3. 将令牌加入黑名单
            tokenBlacklistDomainService.addToBlacklist(
                token, 
                tenantId, 
                "LOGOUT", 
                tokenEntity.getExpiresAt()
            );
            
            // 4. 删除令牌
            tokenRepository.delete(token);
            
            // 5. 删除用户的所有令牌
            tokenRepository.deleteByUserIdAndTenantId(tokenEntity.getUserId(), tenantId);
        }
    }

    /**
     * 验证令牌
     */
    public boolean validateToken(String token, String tenantId) {
        // 首先检查令牌是否在黑名单中
        if (tokenBlacklistDomainService.isTokenBlacklistedByTenant(token, tenantId)) {
            return false;
        }
        
        Optional<Token> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isEmpty()) {
            return false;
        }
        
        Token tokenEntity = tokenOpt.get();
        return tokenDomainService.validateToken(tokenEntity) && 
               tokenDomainService.isTokenForTenant(tokenEntity, tenantId);
    }

    /**
     * 验证客户端
     */
    private Client validateClient(String clientId, String tenantId, String clientSecret) {
        Optional<Client> clientOpt = clientRepository.findByClientIdAndTenantId(clientId, tenantId);
        if (clientOpt.isEmpty()) {
            throw new IllegalArgumentException("客户端不存在");
        }
        
        Client client = clientOpt.get();
        if (!client.getClientSecret().equals(clientSecret)) {
            throw new IllegalArgumentException("客户端密钥错误");
        }
        
        return client;
    }

    /**
     * 验证用户凭据
     */
    private String validateUserCredentials(String tenantId, String username, String password) {
        // 这里需要调用目录服务来验证用户凭据
        // 暂时返回一个模拟的用户ID
        return "user-" + username + "-" + tenantId;
    }

    /**
     * 验证刷新令牌
     */
    private Token validateRefreshToken(String refreshToken, String tenantId) {
        Optional<Token> tokenOpt = tokenRepository.findByToken(refreshToken);
        if (tokenOpt.isEmpty()) {
            throw new IllegalArgumentException("刷新令牌不存在");
        }
        
        Token token = tokenOpt.get();
        if (!tokenDomainService.validateToken(token)) {
            throw new IllegalArgumentException("刷新令牌无效");
        }
        
        if (!tokenDomainService.isTokenForTenant(token, tenantId)) {
            throw new IllegalArgumentException("刷新令牌不属于指定租户");
        }
        
        if (!token.isRefreshToken()) {
            throw new IllegalArgumentException("令牌类型错误");
        }
        
        return token;
    }

    /**
     * 生成令牌
     */
    private TokenResponse generateTokens(String tenantId, String userId, String clientId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime accessTokenExpiresAt = now.plusHours(1);
        LocalDateTime refreshTokenExpiresAt = now.plusDays(7);
        
        // 生成访问令牌
        Token accessToken = tokenDomainService.generateAccessToken(tenantId, userId, clientId, accessTokenExpiresAt);
        tokenRepository.save(accessToken);
        
        // 生成刷新令牌
        Token refreshToken = tokenDomainService.generateRefreshToken(tenantId, userId, clientId, refreshTokenExpiresAt);
        tokenRepository.save(refreshToken);
        
        // 发布令牌颁发事件
        TokenIssuedEvent event = new TokenIssuedEvent(
            accessToken.getToken(), userId, tenantId, clientId, 
            "ACCESS", accessTokenExpiresAt
        );
        eventPublisher.publishEvent(event);
        
        return new TokenResponse(
            accessToken.getToken(),
            refreshToken.getToken(),
            "Bearer",
            3600L,
            "read write",
            tenantId,
            userId
        );
    }
}

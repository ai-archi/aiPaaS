package com.aixone.tech.auth.authentication.application.service;

import com.aixone.tech.auth.authentication.application.command.LoginCommand;
import com.aixone.tech.auth.authentication.application.command.RefreshTokenCommand;
import com.aixone.tech.auth.authentication.application.dto.auth.LoginRequest;
import com.aixone.tech.auth.authentication.application.dto.auth.TokenResponse;
import com.aixone.tech.auth.authentication.domain.event.UserLoginEvent;
import com.aixone.tech.auth.authentication.domain.event.TokenIssuedEvent;
import com.aixone.tech.auth.authentication.domain.model.Client;
import com.aixone.tech.auth.authentication.domain.model.Token;
import com.aixone.tech.auth.authentication.domain.model.User;
import com.aixone.tech.auth.authentication.domain.repository.ClientRepository;
import com.aixone.tech.auth.authentication.domain.repository.TokenRepository;
import com.aixone.tech.auth.authentication.infrastructure.persistence.repository.JpaUserRepository;
import com.aixone.tech.auth.authentication.domain.service.TokenDomainService;
import com.aixone.tech.auth.authentication.domain.service.TokenBlacklistDomainService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final JpaUserRepository userRepository;
    private final TokenDomainService tokenDomainService;
    private final TokenBlacklistDomainService tokenBlacklistDomainService;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    public AuthenticationApplicationService(ClientRepository clientRepository,
                                          TokenRepository tokenRepository,
                                          JpaUserRepository userRepository,
                                          TokenDomainService tokenDomainService,
                                          TokenBlacklistDomainService tokenBlacklistDomainService,
                                          PasswordEncoder passwordEncoder,
                                          ApplicationEventPublisher eventPublisher) {
        this.clientRepository = clientRepository;
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.tokenDomainService = tokenDomainService;
        this.tokenBlacklistDomainService = tokenBlacklistDomainService;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }

    /**
     * 用户登录
     */
    public TokenResponse login(LoginCommand command) {
        System.out.println("DEBUG: login method called");
        
        try {
            // 1. 验证客户端
            System.out.println("DEBUG: Step 1 - Validating client");
            Client client = validateClient(command.getClientId(), command.getTenantId(), command.getClientSecret());
            System.out.println("DEBUG: Step 1 completed - Client validated");
            
            // 2. 验证用户凭据（这里需要调用目录服务）
            System.out.println("DEBUG: Step 2 - Validating user credentials");
            String userId = validateUserCredentials(command.getTenantId(), command.getUsername(), command.getPassword());
            System.out.println("DEBUG: Step 2 completed - User credentials validated, userId=" + userId);
            
            // 3. 生成令牌
            System.out.println("DEBUG: Step 3 - Generating tokens");
            TokenResponse tokenResponse = generateTokens(command.getTenantId(), userId, command.getClientId());
            System.out.println("DEBUG: Step 3 completed - Tokens generated");
            
            // 4. 发布登录事件
            System.out.println("DEBUG: Step 4 - Publishing login event");
            UserLoginEvent loginEvent = new UserLoginEvent(
                userId, command.getTenantId(), command.getClientId(), 
                "password", command.getClientIp(), command.getUserAgent()
            );
            eventPublisher.publishEvent(loginEvent);
            System.out.println("DEBUG: Step 4 completed - Login event published");
            
            System.out.println("DEBUG: Login method completed successfully");
            return tokenResponse;
        } catch (Exception e) {
            System.out.println("DEBUG: Exception in login method: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
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
        System.out.println("DEBUG: validateClient called with clientId=" + clientId + ", tenantId=" + tenantId);
        
        Optional<Client> clientOpt = clientRepository.findByClientIdAndTenantId(clientId, tenantId);
        if (clientOpt.isEmpty()) {
            System.out.println("DEBUG: Client not found");
            throw new IllegalArgumentException("客户端不存在");
        }
        
        Client client = clientOpt.get();
        System.out.println("DEBUG: Client found, secret matches=" + client.getClientSecret().equals(clientSecret));
        
        if (!client.getClientSecret().equals(clientSecret)) {
            throw new IllegalArgumentException("客户端密钥错误");
        }
        
        System.out.println("DEBUG: Client validation successful");
        return client;
    }

    /**
     * 验证用户凭据
     */
    private String validateUserCredentials(String tenantId, String username, String password) {
        System.out.println("DEBUG: validateUserCredentials called with tenantId=" + tenantId + ", username=" + username);
        
        // 从数据库获取用户信息
        Optional<User> userOpt = userRepository.findByUsernameAndTenantId(username, tenantId);
        
        if (userOpt.isEmpty()) {
            System.out.println("DEBUG: User not found");
            throw new IllegalArgumentException("用户名或密码错误");
        }
        
        User user = userOpt.get();
        System.out.println("DEBUG: User found, id=" + user.getId() + ", status=" + user.getStatus());
        
        // 检查用户状态
        if (!user.isActive()) {
            System.out.println("DEBUG: User is not active");
            throw new IllegalArgumentException("用户账户已被禁用");
        }
        
        // 验证密码
        boolean passwordMatches = passwordEncoder.matches(password, user.getHashedPassword());
        System.out.println("DEBUG: Password matches=" + passwordMatches);
        
        if (!passwordMatches) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        
        String userId = user.getId().toString();
        System.out.println("DEBUG: Returning userId=" + userId);
        return userId;
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
     * 获取用户信息（测试用）
     */
    public Optional<User> getUserByUsername(String username, String tenantId) {
        return userRepository.findByUsernameAndTenantId(username, tenantId);
    }
    
    public boolean passwordMatches(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
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

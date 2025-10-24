package com.aixone.tech.auth.authentication.domain.service;

import com.aixone.tech.auth.authentication.domain.model.Token;
import com.aixone.tech.auth.infrastructure.security.JwtUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.Map;
import java.util.UUID;

/**
 * 令牌领域服务
 * 负责令牌相关的业务逻辑
 */
@Service
public class TokenDomainService {
    
    private final JwtUtils jwtUtils;
    
    public TokenDomainService(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    /**
     * 生成访问令牌
     */
    public Token generateAccessToken(String tenantId, String userId, String clientId, 
                                   LocalDateTime expiresAt) {
        return generateAccessToken(tenantId, userId, clientId, expiresAt, null, null, null);
    }
    
    /**
     * 生成访问令牌（带角色和权限）
     */
    public Token generateAccessToken(String tenantId, String userId, String clientId, 
                                   LocalDateTime expiresAt, Set<String> roles, 
                                   Set<String> permissions, Map<String, Object> abacAttributes) {
        String tokenValue = jwtUtils.generateAccessToken(userId, tenantId, clientId, 
                                                        roles, permissions, abacAttributes);
        return new Token(tokenValue, tenantId, userId, clientId, expiresAt, Token.TokenType.ACCESS);
    }

    /**
     * 生成令牌（兼容测试代码）
     */
    public Token generateToken(String tenantId, String userId, String clientId) {
        return generateAccessToken(tenantId, userId, clientId, 
                                 LocalDateTime.now().plusHours(1));
    }

    /**
     * 生成刷新令牌
     */
    public Token generateRefreshToken(String tenantId, String userId, String clientId, 
                                    LocalDateTime expiresAt) {
        String tokenValue = jwtUtils.generateRefreshToken(userId, tenantId, clientId);
        return new Token(tokenValue, tenantId, userId, clientId, expiresAt, Token.TokenType.REFRESH);
    }

    /**
     * 生成刷新令牌（兼容测试代码）
     */
    public Token generateRefreshToken(String tenantId, String userId, String clientId) {
        return generateRefreshToken(tenantId, userId, clientId, 
                                  LocalDateTime.now().plusDays(7));
    }

    /**
     * 验证令牌有效性
     */
    public boolean validateToken(Token token) {
        if (token == null) {
            return false;
        }
        return jwtUtils.validateToken(token.getToken()) && token.isValid();
    }

    /**
     * 检查令牌是否过期
     */
    public boolean isTokenExpired(Token token) {
        if (token == null) {
            return true;
        }
        return token.isExpired();
    }

    /**
     * 检查令牌是否属于指定租户
     */
    public boolean isTokenForTenant(Token token, String tenantId) {
        if (token == null || tenantId == null) {
            return false;
        }
        return token.belongsToTenant(tenantId);
    }

    /**
     * 检查令牌是否属于指定用户
     */
    public boolean isTokenForUser(Token token, String userId) {
        if (token == null || userId == null) {
            return false;
        }
        return token.belongsToUser(userId);
    }

    /**
     * 检查令牌是否属于指定客户端
     */
    public boolean isTokenForClient(Token token, String clientId) {
        if (token == null || clientId == null) {
            return false;
        }
        return token.belongsToClient(clientId);
    }

    /**
     * 生成令牌值
     */
    private String generateTokenValue() {
        return UUID.randomUUID().toString().replace("-", "") + 
               System.currentTimeMillis();
    }
}

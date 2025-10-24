package com.aixone.tech.auth.authentication.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 令牌聚合根
 * 负责管理JWT令牌的生命周期
 */
public class Token {
    private String token;
    private String tenantId;
    private String userId;
    private String clientId;
    private LocalDateTime expiresAt;
    private TokenType type;
    private LocalDateTime createdAt;

    public Token() {}

    public Token(String token, String tenantId, String userId, String clientId, 
                 LocalDateTime expiresAt, TokenType type) {
        this.token = token;
        this.tenantId = tenantId;
        this.userId = userId;
        this.clientId = clientId;
        this.expiresAt = expiresAt;
        this.type = type;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * 检查令牌是否过期
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * 检查令牌是否有效
     */
    public boolean isValid() {
        return !isExpired() && token != null && !token.isEmpty();
    }

    /**
     * 检查是否为访问令牌
     */
    public boolean isAccessToken() {
        return TokenType.ACCESS.equals(type);
    }

    /**
     * 检查是否为刷新令牌
     */
    public boolean isRefreshToken() {
        return TokenType.REFRESH.equals(type);
    }

    /**
     * 检查令牌是否属于指定租户
     */
    public boolean belongsToTenant(String tenantId) {
        return Objects.equals(this.tenantId, tenantId);
    }

    /**
     * 检查令牌是否属于指定用户
     */
    public boolean belongsToUser(String userId) {
        return Objects.equals(this.userId, userId);
    }

    /**
     * 检查令牌是否属于指定客户端
     */
    public boolean belongsToClient(String clientId) {
        return Objects.equals(this.clientId, clientId);
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token1 = (Token) o;
        return Objects.equals(token, token1.token) && 
               Objects.equals(tenantId, token1.tenantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, tenantId);
    }

    @Override
    public String toString() {
        return "Token{" +
                "token='" + token.substring(0, Math.min(20, token.length())) + "..." + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", userId='" + userId + '\'' +
                ", clientId='" + clientId + '\'' +
                ", expiresAt=" + expiresAt +
                ", type=" + type +
                '}';
    }

    /**
     * 令牌类型枚举
     */
    public enum TokenType {
        ACCESS, REFRESH
    }
}

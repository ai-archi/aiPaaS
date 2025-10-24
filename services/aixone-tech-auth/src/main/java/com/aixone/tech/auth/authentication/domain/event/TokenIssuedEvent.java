package com.aixone.tech.auth.authentication.domain.event;

import java.time.LocalDateTime;

/**
 * 令牌颁发事件
 */
public class TokenIssuedEvent {
    private String tokenId;
    private String userId;
    private String tenantId;
    private String clientId;
    private String tokenType;
    private LocalDateTime expiresAt;
    private LocalDateTime timestamp;

    public TokenIssuedEvent() {}

    public TokenIssuedEvent(String tokenId, String userId, String tenantId, 
                           String clientId, String tokenType, LocalDateTime expiresAt) {
        this.tokenId = tokenId;
        this.userId = userId;
        this.tenantId = tenantId;
        this.clientId = clientId;
        this.tokenType = tokenType;
        this.expiresAt = expiresAt;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "TokenIssuedEvent{" +
                "tokenId='" + tokenId + '\'' +
                ", userId='" + userId + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", clientId='" + clientId + '\'' +
                ", tokenType='" + tokenType + '\'' +
                ", expiresAt=" + expiresAt +
                ", timestamp=" + timestamp +
                '}';
    }
}

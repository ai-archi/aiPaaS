package com.aixone.tech.auth.authentication.domain.model;

import java.time.LocalDateTime;

/**
 * 令牌黑名单领域模型
 */
public class TokenBlacklist {
    
    private String id;
    private String token;
    private String tenantId;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private String reason; // LOGOUT, SECURITY, ADMIN
    
    public TokenBlacklist() {}
    
    public TokenBlacklist(String token, String tenantId, LocalDateTime expiresAt, String reason) {
        this.token = token;
        this.tenantId = tenantId;
        this.expiresAt = expiresAt;
        this.reason = reason;
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * 检查令牌是否在黑名单中
     */
    public boolean isBlacklisted() {
        return LocalDateTime.now().isBefore(expiresAt);
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
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
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
}

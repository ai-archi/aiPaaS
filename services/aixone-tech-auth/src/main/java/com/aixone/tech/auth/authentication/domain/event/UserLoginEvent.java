package com.aixone.tech.auth.authentication.domain.event;

import java.time.LocalDateTime;

/**
 * 用户登录事件
 */
public class UserLoginEvent {
    private String userId;
    private String tenantId;
    private String clientId;
    private String loginMethod;
    private String clientIp;
    private String userAgent;
    private LocalDateTime timestamp;

    public UserLoginEvent() {}

    public UserLoginEvent(String userId, String tenantId, String clientId, 
                         String loginMethod, String clientIp, String userAgent) {
        this.userId = userId;
        this.tenantId = tenantId;
        this.clientId = clientId;
        this.loginMethod = loginMethod;
        this.clientIp = clientIp;
        this.userAgent = userAgent;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
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

    public String getLoginMethod() {
        return loginMethod;
    }

    public void setLoginMethod(String loginMethod) {
        this.loginMethod = loginMethod;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "UserLoginEvent{" +
                "userId='" + userId + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", clientId='" + clientId + '\'' +
                ", loginMethod='" + loginMethod + '\'' +
                ", clientIp='" + clientIp + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}

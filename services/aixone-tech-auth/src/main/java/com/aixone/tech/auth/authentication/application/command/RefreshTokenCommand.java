package com.aixone.tech.auth.authentication.application.command;

import jakarta.validation.constraints.NotBlank;

/**
 * 刷新令牌命令
 */
public class RefreshTokenCommand {
    @NotBlank(message = "租户ID不能为空")
    private String tenantId;
    
    @NotBlank(message = "刷新令牌不能为空")
    private String refreshToken;
    
    @NotBlank(message = "客户端ID不能为空")
    private String clientId;
    
    private String clientSecret;
    private String clientIp;
    private String userAgent;

    public RefreshTokenCommand() {}

    public RefreshTokenCommand(String tenantId, String refreshToken, String clientId) {
        this.tenantId = tenantId;
        this.refreshToken = refreshToken;
        this.clientId = clientId;
    }

    // Getters and Setters
    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
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

    @Override
    public String toString() {
        return "RefreshTokenCommand{" +
                "tenantId='" + tenantId + '\'' +
                ", clientId='" + clientId + '\'' +
                ", clientIp='" + clientIp + '\'' +
                '}';
    }
}

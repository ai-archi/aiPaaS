package com.aixone.tech.auth.authentication.application.dto.auth;

import java.time.LocalDateTime;

/**
 * 登录请求DTO
 */
public class LoginRequest {
    private String tenantId;
    private String username;
    private String password;
    private String clientId;
    private String clientSecret;
    private String loginMethod; // password, sms, email, oauth2
    private String verificationCode; // 用于短信或邮箱验证码登录
    private String provider; // 用于OAuth2登录

    public LoginRequest() {}

    // Getters and Setters
    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getLoginMethod() {
        return loginMethod;
    }

    public void setLoginMethod(String loginMethod) {
        this.loginMethod = loginMethod;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                "tenantId='" + tenantId + '\'' +
                ", username='" + username + '\'' +
                ", clientId='" + clientId + '\'' +
                ", loginMethod='" + loginMethod + '\'' +
                ", provider='" + provider + '\'' +
                '}';
    }
}

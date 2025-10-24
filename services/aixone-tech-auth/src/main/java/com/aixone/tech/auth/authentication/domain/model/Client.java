package com.aixone.tech.auth.authentication.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 客户端聚合根
 * 负责管理OAuth2客户端信息
 */
public class Client {
    private String clientId;
    private String tenantId;
    private String clientSecret;
    private String redirectUri;
    private String scopes;
    private String grantTypes;
    private boolean enabled = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Client() {}

    public Client(String clientId, String tenantId, String clientSecret, 
                  String redirectUri, String scopes, String grantTypes) {
        this.clientId = clientId;
        this.tenantId = tenantId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.scopes = scopes;
        this.grantTypes = grantTypes;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 验证客户端密钥
     */
    public boolean validateSecret(String secret) {
        return Objects.equals(this.clientSecret, secret);
    }

    /**
     * 验证重定向URI
     */
    public boolean validateRedirectUri(String uri) {
        return Objects.equals(this.redirectUri, uri);
    }

    /**
     * 检查是否支持指定的授权类型
     */
    public boolean supportsGrantType(String grantType) {
        return this.grantTypes != null && this.grantTypes.contains(grantType);
    }

    /**
     * 检查是否包含指定的权限范围
     */
    public boolean hasScope(String scope) {
        return this.scopes != null && this.scopes.contains(scope);
    }

    /**
     * 更新客户端信息
     */
    public void update(String redirectUri, String scopes, String grantTypes) {
        this.redirectUri = redirectUri;
        this.scopes = scopes;
        this.grantTypes = grantTypes;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getScopes() {
        return scopes;
    }

    public void setScopes(String scopes) {
        this.scopes = scopes;
    }

    public String getGrantTypes() {
        return grantTypes;
    }

    public void setGrantTypes(String grantTypes) {
        this.grantTypes = grantTypes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(clientId, client.clientId) && 
               Objects.equals(tenantId, client.tenantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, tenantId);
    }

    @Override
    public String toString() {
        return "Client{" +
                "clientId='" + clientId + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", redirectUri='" + redirectUri + '\'' +
                ", scopes='" + scopes + '\'' +
                ", grantTypes='" + grantTypes + '\'' +
                '}';
    }
}

package com.aixone.tech.auth.authentication.domain.service;

import com.aixone.tech.auth.authentication.domain.model.Client;
import com.aixone.tech.auth.authentication.domain.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * 客户端领域服务
 * 负责客户端相关的业务逻辑
 */
@Service
public class ClientDomainService {

    private final ClientRepository clientRepository;

    public ClientDomainService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    /**
     * 验证客户端凭据
     */
    public Client validateClient(String clientId, String tenantId, String clientSecret) {
        Client client = clientRepository.findByClientIdAndTenantId(clientId, tenantId)
            .orElseThrow(() -> new IllegalArgumentException("客户端不存在"));
        if (!client.getClientSecret().equals(clientSecret)) {
            throw new IllegalArgumentException("客户端密钥错误");
        }
        if (!client.isEnabled()) {
            throw new IllegalArgumentException("客户端已被禁用");
        }
        return client;
    }

    /**
     * 创建客户端
     */
    public Client createClient(String tenantId, String clientId, String clientSecret, 
                              String redirectUri, String scopes, String grantTypes) {
        // 检查客户端是否已存在
        if (clientRepository.existsByClientIdAndTenantId(clientId, tenantId)) {
            throw new IllegalArgumentException("客户端ID已存在");
        }

        Client client = new Client(
            clientId,
            tenantId,
            clientSecret,
            redirectUri,
            scopes,
            grantTypes
        );
        return clientRepository.save(client);
    }

    /**
     * 更新客户端信息
     */
    public Client updateClient(String clientId, String tenantId, String clientSecret, 
                              String redirectUri, String scopes, String grantTypes) {
        Client client = clientRepository.findByClientIdAndTenantId(clientId, tenantId)
            .orElseThrow(() -> new IllegalArgumentException("客户端不存在"));

        client.setClientSecret(clientSecret);
        client.setRedirectUri(redirectUri);
        client.setScopes(scopes);
        client.setGrantTypes(grantTypes);
        
        return clientRepository.save(client);
    }

    /**
     * 启用客户端
     */
    public Client enableClient(String clientId, String tenantId) {
        Client client = clientRepository.findByClientIdAndTenantId(clientId, tenantId)
            .orElseThrow(() -> new IllegalArgumentException("客户端不存在"));
        client.setEnabled(true);
        return clientRepository.save(client);
    }

    /**
     * 禁用客户端
     */
    public Client disableClient(String clientId, String tenantId) {
        Client client = clientRepository.findByClientIdAndTenantId(clientId, tenantId)
            .orElseThrow(() -> new IllegalArgumentException("客户端不存在"));
        client.setEnabled(false);
        return clientRepository.save(client);
    }

    /**
     * 获取租户的所有客户端
     */
    public List<Client> getClientsByTenant(String tenantId) {
        return clientRepository.findByTenantId(tenantId);
    }

    /**
     * 检查客户端是否支持指定的授权类型
     */
    public boolean supportsGrantType(Client client, String grantType) {
        if (client == null || grantType == null) {
            return false;
        }
        String grantTypes = client.getGrantTypes();
        if (grantTypes == null || grantTypes.isEmpty()) {
            return false;
        }
        return grantTypes.contains(grantType);
    }

    /**
     * 检查客户端是否支持指定的作用域
     */
    public boolean supportsScope(Client client, String scope) {
        if (client == null || scope == null) {
            return false;
        }
        String scopes = client.getScopes();
        if (scopes == null || scopes.isEmpty()) {
            return false;
        }
        return scopes.contains(scope);
    }

    /**
     * 验证重定向URI
     */
    public boolean isValidRedirectUri(Client client, String redirectUri) {
        if (client == null || redirectUri == null) {
            return false;
        }
        String allowedRedirectUri = client.getRedirectUri();
        if (allowedRedirectUri == null || allowedRedirectUri.isEmpty()) {
            return false;
        }
        return allowedRedirectUri.equals(redirectUri);
    }

    /**
     * 生成客户端密钥
     */
    public String generateClientSecret() {
        return UUID.randomUUID().toString().replace("-", "") + 
               System.currentTimeMillis();
    }
}

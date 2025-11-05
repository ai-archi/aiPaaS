package com.aixone.tech.auth.authentication.interfaces.rest;

import com.aixone.tech.auth.authentication.domain.model.Client;
import com.aixone.tech.auth.authentication.domain.service.ClientDomainService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客户端管理控制器（内部接口）
 * 注意：管理功能由Workbench服务对外提供，此接口仅用于内部调用
 * 实际使用时，Workbench服务应调用此接口或直接调用DomainService
 */
@RestController
@RequestMapping("/api/v1/auth/internal/clients")
public class ClientManagementController {

    private final ClientDomainService clientDomainService;

    public ClientManagementController(ClientDomainService clientDomainService) {
        this.clientDomainService = clientDomainService;
    }

    /**
     * 创建客户端（内部接口）
     * 注意：此接口仅用于内部调用，对外管理功能由Workbench服务提供
     */
    @PostMapping
    public ResponseEntity<Client> createClient(@RequestBody CreateClientRequest request) {
        Client client = clientDomainService.createClient(
            request.getTenantId(),
            request.getClientId(),
            request.getClientSecret(),
            request.getRedirectUri(),
            request.getScopes(),
            request.getGrantTypes()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(client);
    }

    /**
     * 获取租户的所有客户端（内部接口）
     * 注意：此接口仅用于内部调用，对外管理功能由Workbench服务提供
     */
    @GetMapping
    public ResponseEntity<List<Client>> getClients(@RequestParam String tenantId) {
        List<Client> clients = clientDomainService.getClientsByTenant(tenantId);
        return ResponseEntity.ok(clients);
    }

    /**
     * 创建客户端请求
     */
    public static class CreateClientRequest {
        private String tenantId;
        private String clientId;
        private String clientSecret;
        private String redirectUri;
        private String scopes;
        private String grantTypes;

        // Getters and Setters
        public String getTenantId() { return tenantId; }
        public void setTenantId(String tenantId) { this.tenantId = tenantId; }
        public String getClientId() { return clientId; }
        public void setClientId(String clientId) { this.clientId = clientId; }
        public String getClientSecret() { return clientSecret; }
        public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }
        public String getRedirectUri() { return redirectUri; }
        public void setRedirectUri(String redirectUri) { this.redirectUri = redirectUri; }
        public String getScopes() { return scopes; }
        public void setScopes(String scopes) { this.scopes = scopes; }
        public String getGrantTypes() { return grantTypes; }
        public void setGrantTypes(String grantTypes) { this.grantTypes = grantTypes; }
    }
}

package com.aixone.tech.auth.authentication.domain.service;

import com.aixone.tech.auth.authentication.domain.model.Client;
import com.aixone.tech.auth.authentication.domain.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * 客户端领域服务测试
 */
@ExtendWith(MockitoExtension.class)
class ClientDomainServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientDomainService clientDomainService;

    private String tenantId;
    private String clientId;
    private String clientSecret;
    private Client testClient;

    @BeforeEach
    void setUp() {
        tenantId = "test-tenant";
        clientId = "test-client";
        clientSecret = "test-secret";

        testClient = new Client(
            clientId,
            tenantId,
            clientSecret,
            "http://localhost:3000/callback",
            "read write",
            "authorization_code password"
        );
        testClient.setEnabled(true);
    }

    @Test
    void testValidateClient_Success() {
        // Arrange
        when(clientRepository.findByClientIdAndTenantId(clientId, tenantId)).thenReturn(Optional.of(testClient));

        // Act
        Client result = clientDomainService.validateClient(clientId, tenantId, clientSecret);

        // Assert
        assertNotNull(result);
        assertEquals(clientId, result.getClientId());
        assertEquals(tenantId, result.getTenantId());
        assertEquals(clientSecret, result.getClientSecret());
        assertTrue(result.isEnabled());

        verify(clientRepository).findByClientIdAndTenantId(clientId, tenantId);
    }

    @Test
    void testValidateClient_ClientNotFound_ThrowsException() {
        // Arrange
        when(clientRepository.findByClientIdAndTenantId(clientId, tenantId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            clientDomainService.validateClient(clientId, tenantId, clientSecret);
        });

        verify(clientRepository).findByClientIdAndTenantId(clientId, tenantId);
    }

    @Test
    void testValidateClient_WrongSecret_ThrowsException() {
        // Arrange
        when(clientRepository.findByClientIdAndTenantId(clientId, tenantId)).thenReturn(Optional.of(testClient));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            clientDomainService.validateClient(clientId, tenantId, "wrong-secret");
        });

        verify(clientRepository).findByClientIdAndTenantId(clientId, tenantId);
    }

    @Test
    void testValidateClient_ClientDisabled_ThrowsException() {
        // Arrange
        testClient.setEnabled(false);
        when(clientRepository.findByClientIdAndTenantId(clientId, tenantId)).thenReturn(Optional.of(testClient));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            clientDomainService.validateClient(clientId, tenantId, clientSecret);
        });

        verify(clientRepository).findByClientIdAndTenantId(clientId, tenantId);
    }

    @Test
    void testCreateClient_Success() {
        // Arrange
        String redirectUri = "http://localhost:3000/callback";
        String scopes = "read write";
        String grantTypes = "authorization_code password";

        when(clientRepository.existsByClientIdAndTenantId(clientId, tenantId)).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenReturn(testClient);

        // Act
        Client result = clientDomainService.createClient(tenantId, clientId, clientSecret, redirectUri, scopes, grantTypes);

        // Assert
        assertNotNull(result);
        assertEquals(clientId, result.getClientId());
        assertEquals(tenantId, result.getTenantId());
        assertEquals(clientSecret, result.getClientSecret());
        assertEquals(redirectUri, result.getRedirectUri());
        assertEquals(scopes, result.getScopes());
        assertEquals(grantTypes, result.getGrantTypes());

        verify(clientRepository).existsByClientIdAndTenantId(clientId, tenantId);
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    void testCreateClient_ClientExists_ThrowsException() {
        // Arrange
        when(clientRepository.existsByClientIdAndTenantId(clientId, tenantId)).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            clientDomainService.createClient(tenantId, clientId, clientSecret, "http://localhost:3000/callback", "read write", "authorization_code");
        });

        verify(clientRepository).existsByClientIdAndTenantId(clientId, tenantId);
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void testUpdateClient_Success() {
        // Arrange
        String newSecret = "new-secret";
        String newRedirectUri = "http://localhost:3001/callback";
        String newScopes = "read write admin";
        String newGrantTypes = "authorization_code password refresh_token";

        when(clientRepository.findByClientIdAndTenantId(clientId, tenantId)).thenReturn(Optional.of(testClient));
        when(clientRepository.save(any(Client.class))).thenReturn(testClient);

        // Act
        Client result = clientDomainService.updateClient(clientId, tenantId, newSecret, newRedirectUri, newScopes, newGrantTypes);

        // Assert
        assertNotNull(result);
        assertEquals(newSecret, result.getClientSecret());
        assertEquals(newRedirectUri, result.getRedirectUri());
        assertEquals(newScopes, result.getScopes());
        assertEquals(newGrantTypes, result.getGrantTypes());

        verify(clientRepository).findByClientIdAndTenantId(clientId, tenantId);
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    void testUpdateClient_ClientNotFound_ThrowsException() {
        // Arrange
        when(clientRepository.findByClientIdAndTenantId(clientId, tenantId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            clientDomainService.updateClient(clientId, tenantId, "new-secret", "http://localhost:3000/callback", "read write", "authorization_code");
        });

        verify(clientRepository).findByClientIdAndTenantId(clientId, tenantId);
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void testEnableClient_Success() {
        // Arrange
        testClient.setEnabled(false);
        when(clientRepository.findByClientIdAndTenantId(clientId, tenantId)).thenReturn(Optional.of(testClient));
        when(clientRepository.save(any(Client.class))).thenReturn(testClient);

        // Act
        Client result = clientDomainService.enableClient(clientId, tenantId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEnabled());

        verify(clientRepository).findByClientIdAndTenantId(clientId, tenantId);
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    void testDisableClient_Success() {
        // Arrange
        when(clientRepository.findByClientIdAndTenantId(clientId, tenantId)).thenReturn(Optional.of(testClient));
        when(clientRepository.save(any(Client.class))).thenReturn(testClient);

        // Act
        Client result = clientDomainService.disableClient(clientId, tenantId);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEnabled());

        verify(clientRepository).findByClientIdAndTenantId(clientId, tenantId);
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    void testGetClientsByTenant_Success() {
        // Arrange
        Client client1 = new Client("client1", tenantId, "secret1", "http://localhost:3000", "read", "authorization_code");
        Client client2 = new Client("client2", tenantId, "secret2", "http://localhost:3001", "write", "password");
        List<Client> clients = Arrays.asList(client1, client2);

        when(clientRepository.findByTenantId(tenantId)).thenReturn(clients);

        // Act
        List<Client> result = clientDomainService.getClientsByTenant(tenantId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("client1", result.get(0).getClientId());
        assertEquals("client2", result.get(1).getClientId());

        verify(clientRepository).findByTenantId(tenantId);
    }

    @Test
    void testSupportsGrantType_ReturnsTrue() {
        // Arrange
        String grantType = "authorization_code";

        // Act
        boolean result = clientDomainService.supportsGrantType(testClient, grantType);

        // Assert
        assertTrue(result);
    }

    @Test
    void testSupportsGrantType_ReturnsFalse() {
        // Arrange
        String grantType = "implicit";

        // Act
        boolean result = clientDomainService.supportsGrantType(testClient, grantType);

        // Assert
        assertFalse(result);
    }

    @Test
    void testSupportsScope_ReturnsTrue() {
        // Arrange
        String scope = "read";

        // Act
        boolean result = clientDomainService.supportsScope(testClient, scope);

        // Assert
        assertTrue(result);
    }

    @Test
    void testSupportsScope_ReturnsFalse() {
        // Arrange
        String scope = "admin";

        // Act
        boolean result = clientDomainService.supportsScope(testClient, scope);

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsValidRedirectUri_ReturnsTrue() {
        // Arrange
        String redirectUri = "http://localhost:3000/callback";

        // Act
        boolean result = clientDomainService.isValidRedirectUri(testClient, redirectUri);

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsValidRedirectUri_ReturnsFalse() {
        // Arrange
        String redirectUri = "http://localhost:3001/callback";

        // Act
        boolean result = clientDomainService.isValidRedirectUri(testClient, redirectUri);

        // Assert
        assertFalse(result);
    }

    @Test
    void testGenerateClientSecret_ReturnsValidSecret() {
        // Act
        String secret = clientDomainService.generateClientSecret();

        // Assert
        assertNotNull(secret);
        assertFalse(secret.isEmpty());
        assertTrue(secret.length() > 20); // Should be reasonably long
    }
}

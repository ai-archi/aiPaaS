package com.aixone.tech.auth.authentication.infrastructure.persistence.repository;

import com.aixone.tech.auth.authentication.domain.model.Client;
import com.aixone.tech.auth.authentication.domain.repository.ClientRepository;
import com.aixone.tech.auth.authentication.infrastructure.persistence.entity.ClientEntity;
import com.aixone.tech.auth.authentication.infrastructure.persistence.mapper.ClientMapper;
import com.aixone.tech.auth.authentication.infrastructure.persistence.repository.ClientJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Client Repository 基础设施层测试
 */
@SpringBootTest
@ActiveProfiles("test")
public class ClientRepositoryInfrastructureTest {

    @Autowired
    private ClientRepository clientRepository;
    
    @Autowired
    private ClientMapper clientMapper;
    
    @Autowired
    private ClientJpaRepository jpaRepository;

    @Test
    public void testMapper() {
        // Given
        Client client = new Client();
        client.setClientId("test-client");
        client.setTenantId("test-tenant");
        client.setClientSecret("test-secret");
        client.setRedirectUri("http://localhost:8080/callback");
        client.setScopes("read,write");
        client.setGrantTypes("authorization_code,refresh_token");
        client.setCreatedAt(LocalDateTime.now());
        client.setUpdatedAt(LocalDateTime.now());
        
        // When
        ClientEntity entity = clientMapper.toEntity(client);
        Client mappedClient = clientMapper.toDomain(entity);
        
        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getClientId()).isEqualTo("test-client");
        assertThat(entity.getTenantId()).isEqualTo("test-tenant");
        
        assertThat(mappedClient).isNotNull();
        assertThat(mappedClient.getClientId()).isEqualTo("test-client");
        assertThat(mappedClient.getTenantId()).isEqualTo("test-tenant");
    }

    @Test
    public void testSaveAndFindClient() {
        // Given
        Client client = new Client();
        client.setClientId("test-client");
        client.setTenantId("test-tenant");
        client.setClientSecret("test-secret");
        client.setRedirectUri("http://localhost:8080/callback");
        client.setScopes("read,write");
        client.setGrantTypes("authorization_code,refresh_token");
        client.setCreatedAt(LocalDateTime.now());
        client.setUpdatedAt(LocalDateTime.now());

        // When
        Client savedClient = clientRepository.save(client);
        Optional<Client> foundClient = clientRepository.findByClientIdAndTenantId("test-client", "test-tenant");

        // Then
        assertThat(savedClient).isNotNull();
        assertThat(savedClient.getClientId()).isEqualTo("test-client");
        assertThat(foundClient).isPresent();
        assertThat(foundClient.get().getClientId()).isEqualTo("test-client");
        assertThat(foundClient.get().getTenantId()).isEqualTo("test-tenant");
    }

    @Test
    @Transactional
    @Rollback(false)
    public void testExistsByClientIdAndTenantId() {
        // Given
        Client client = new Client();
        client.setClientId("test-client-2");
        client.setTenantId("test-tenant-2");
        client.setClientSecret("test-secret-2");
        client.setRedirectUri("http://localhost:8080/callback");
        client.setScopes("read,write");
        client.setGrantTypes("authorization_code,refresh_token");
        client.setCreatedAt(LocalDateTime.now());
        client.setUpdatedAt(LocalDateTime.now());
        
        // When - save and immediately test within same transaction
        Client savedClient = clientRepository.save(client);
        
        // Test direct JPA repository query
        Optional<ClientEntity> foundEntity = jpaRepository.findByClientIdAndTenantId("test-client-2", "test-tenant-2");
        
        // Test findByClientIdAndTenantId
        Optional<Client> foundClient = clientRepository.findByClientIdAndTenantId("test-client-2", "test-tenant-2");

        // Test exists methods
        boolean exists = clientRepository.existsByClientIdAndTenantId("test-client-2", "test-tenant-2");
        boolean notExists = clientRepository.existsByClientIdAndTenantId("non-existent", "test-tenant-2");

        // Then
        assertThat(savedClient).isNotNull();
        assertThat(foundEntity).isPresent();
        assertThat(foundClient).isPresent();
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}
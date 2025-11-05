package com.aixone.tech.auth.authentication.infrastructure.persistence.repository;

import com.aixone.tech.auth.authentication.domain.model.Client;
import com.aixone.tech.auth.authentication.infrastructure.persistence.entity.ClientEntity;
import com.aixone.tech.auth.authentication.infrastructure.persistence.mapper.ClientMapper;
import com.aixone.tech.auth.authentication.infrastructure.persistence.repository.ClientJpaRepository;
import com.aixone.tech.auth.authentication.infrastructure.persistence.repository.JpaClientRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Client Repository 基础设施层测试
 */
@DataJpaTest
@ActiveProfiles("test")
@ComponentScan(
    basePackages = "com.aixone.tech.auth",
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = com.aixone.tech.auth.config.TestDataConfig.class
        ),
        @ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = "com\\.aixone\\.tech\\.auth\\.config\\.TestDataConfig"
        )
    }
)
@Rollback(false) // 禁用自动回滚，确保数据在测试中可见
public class ClientRepositoryInfrastructureTest {
    
    @Autowired
    private JpaClientRepository jpaClientRepository;
    
    @Autowired
    private ClientMapper clientMapper;
    
    @Autowired
    private ClientJpaRepository jpaRepository;
    
    @Autowired
    private EntityManager entityManager;

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
        ClientEntity entity = new ClientEntity();
        entity.setClientId("test-client");
        entity.setTenantId("test-tenant");
        entity.setClientSecret("test-secret");
        entity.setRedirectUri("http://localhost:8080/callback");
        entity.setScopes("read,write");
        entity.setGrantTypes("authorization_code,refresh_token");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        // When
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        Optional<Client> foundClient = jpaClientRepository.findByClientIdAndTenantId("test-client", "test-tenant");

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getClientId()).isEqualTo("test-client");
        assertThat(foundClient).isPresent();
        assertThat(foundClient.get().getClientId()).isEqualTo("test-client");
        assertThat(foundClient.get().getTenantId()).isEqualTo("test-tenant");
    }

    @Test
    public void testExistsByClientIdAndTenantId() {
        // Given - exactly copy testSaveAndFindClient structure, only change identifiers
        ClientEntity entity = new ClientEntity();
        entity.setClientId("test-client-exists");
        entity.setTenantId("test-tenant-exists");
        entity.setClientSecret("test-secret");
        entity.setRedirectUri("http://localhost:8080/callback");
        entity.setScopes("read,write");
        entity.setGrantTypes("authorization_code,refresh_token");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        // When - exactly same as testSaveAndFindClient
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        
        // First verify JPA repository can find the entity
        Optional<ClientEntity> foundEntity = jpaRepository.findByClientIdAndTenantId("test-client-exists", "test-tenant-exists");
        assertThat(foundEntity).as("JPA repository should find the entity").isPresent();
        assertThat(foundEntity.get().getClientId()).isEqualTo("test-client-exists");
        assertThat(foundEntity.get().getTenantId()).isEqualTo("test-tenant-exists");
        
        // Then verify mapper can convert it
        Client mappedClient = clientMapper.toDomain(foundEntity.get());
        assertThat(mappedClient).as("Mapper should convert Entity to Domain").isNotNull();
        assertThat(mappedClient.getClientId()).isEqualTo("test-client-exists");
        assertThat(mappedClient.getTenantId()).isEqualTo("test-tenant-exists");
        
        // Test using jpaClientRepository directly (the real implementation)
        Optional<Client> foundClientDirect = jpaClientRepository.findByClientIdAndTenantId("test-client-exists", "test-tenant-exists");
        assertThat(foundClientDirect).as("JpaClientRepository should find the client when called directly").isPresent();
        assertThat(foundClientDirect.get().getClientId()).isEqualTo("test-client-exists");
        assertThat(foundClientDirect.get().getTenantId()).isEqualTo("test-tenant-exists");
        
        // Test exists methods using jpaClientRepository (the real implementation)
        boolean exists = jpaClientRepository.existsByClientIdAndTenantId("test-client-exists", "test-tenant-exists");
        boolean notExists = jpaClientRepository.existsByClientIdAndTenantId("non-existent", "test-tenant-exists");
        assertThat(exists).as("existsByClientIdAndTenantId should return true").isTrue();
        assertThat(notExists).as("existsByClientIdAndTenantId should return false for non-existent client").isFalse();
    }
}
package com.aixone.tech.auth.authentication.infrastructure.persistence.repository;

import com.aixone.tech.auth.authentication.domain.model.Token;
import com.aixone.tech.auth.authentication.infrastructure.persistence.repository.JpaTokenRepository;
import com.aixone.tech.auth.authentication.infrastructure.persistence.repository.TokenJpaRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Token Repository 基础设施层测试
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
public class TokenRepositoryInfrastructureTest {

    @Autowired
    private JpaTokenRepository jpaTokenRepository;
    
    @Autowired
    private TokenJpaRepository jpaRepository;
    
    @Autowired
    private EntityManager entityManager;

    @Test
    public void testSaveAndFindToken() {
        // Given
        com.aixone.tech.auth.authentication.infrastructure.persistence.entity.TokenEntity entity = 
            new com.aixone.tech.auth.authentication.infrastructure.persistence.entity.TokenEntity();
        entity.setToken("test-token-exists");
        entity.setUserId("test-user");
        entity.setClientId("test-client");
        entity.setTenantId("test-tenant");
        entity.setType("ACCESS");
        entity.setExpiresAt(LocalDateTime.now().plusHours(1));
        entity.setCreatedAt(LocalDateTime.now());

        // When - save using JPA repository to ensure persistence
        jpaRepository.save(entity);
        jpaRepository.flush();
        entityManager.clear(); // Clear to force a fresh query
        
        // Query using JPA repository directly
        Optional<com.aixone.tech.auth.authentication.infrastructure.persistence.entity.TokenEntity> foundEntity = 
            jpaRepository.findByToken("test-token-exists");
        
        // Query using domain repository (real implementation)
        Optional<Token> foundToken = jpaTokenRepository.findByToken("test-token-exists");

        // Then
        assertThat(foundEntity).as("Direct JPA query should find the entity").isPresent();
        assertThat(foundToken).as("Repository query should find the token").isPresent();
        assertThat(foundToken.get().getToken()).isEqualTo("test-token-exists");
        assertThat(foundToken.get().getUserId()).isEqualTo("test-user");
    }

    @Test
    public void testFindByUserIdAndTenantId() {
        // Given
        com.aixone.tech.auth.authentication.infrastructure.persistence.entity.TokenEntity entity1 = 
            new com.aixone.tech.auth.authentication.infrastructure.persistence.entity.TokenEntity();
        entity1.setToken("token-1");
        entity1.setUserId("test-user");
        entity1.setClientId("client-1");
        entity1.setTenantId("test-tenant");
        entity1.setType("ACCESS");
        entity1.setExpiresAt(LocalDateTime.now().plusHours(1));
        entity1.setCreatedAt(LocalDateTime.now());

        com.aixone.tech.auth.authentication.infrastructure.persistence.entity.TokenEntity entity2 = 
            new com.aixone.tech.auth.authentication.infrastructure.persistence.entity.TokenEntity();
        entity2.setToken("token-2");
        entity2.setUserId("test-user");
        entity2.setClientId("client-2");
        entity2.setTenantId("test-tenant");
        entity2.setType("REFRESH");
        entity2.setExpiresAt(LocalDateTime.now().plusDays(7));
        entity2.setCreatedAt(LocalDateTime.now());

        entityManager.persist(entity1);
        entityManager.persist(entity2);
        entityManager.flush();
        entityManager.clear();

        // When
        List<Token> tokens = jpaTokenRepository.findByUserIdAndTenantId("test-user", "test-tenant");

        // Then
        assertThat(tokens).hasSize(2);
        assertThat(tokens).extracting(Token::getToken).containsExactlyInAnyOrder("token-1", "token-2");
    }

    @Test
    public void testFindByClientIdAndTenantId() {
        // Given
        com.aixone.tech.auth.authentication.infrastructure.persistence.entity.TokenEntity entity1 = 
            new com.aixone.tech.auth.authentication.infrastructure.persistence.entity.TokenEntity();
        entity1.setToken("token-user-1");
        entity1.setUserId("user-1");
        entity1.setClientId("test-client");
        entity1.setTenantId("test-tenant");
        entity1.setType("ACCESS");
        entity1.setExpiresAt(LocalDateTime.now().plusHours(1));
        entity1.setCreatedAt(LocalDateTime.now());

        com.aixone.tech.auth.authentication.infrastructure.persistence.entity.TokenEntity entity2 = 
            new com.aixone.tech.auth.authentication.infrastructure.persistence.entity.TokenEntity();
        entity2.setToken("token-user-2");
        entity2.setUserId("user-2");
        entity2.setClientId("test-client");
        entity2.setTenantId("test-tenant");
        entity2.setType("REFRESH");
        entity2.setExpiresAt(LocalDateTime.now().plusDays(7));
        entity2.setCreatedAt(LocalDateTime.now());

        entityManager.persist(entity1);
        entityManager.persist(entity2);
        entityManager.flush();
        entityManager.clear();

        // When
        List<Token> tokens = jpaTokenRepository.findByClientIdAndTenantId("test-client", "test-tenant");

        // Then
        assertThat(tokens).hasSize(2);
        assertThat(tokens).extracting(Token::getToken).containsExactlyInAnyOrder("token-user-1", "token-user-2");
    }

    @Test
    public void testExistsByToken() {
        // Given
        com.aixone.tech.auth.authentication.infrastructure.persistence.entity.TokenEntity entity = 
            new com.aixone.tech.auth.authentication.infrastructure.persistence.entity.TokenEntity();
        entity.setToken("test-token-exists-check");
        entity.setUserId("test-user");
        entity.setClientId("test-client");
        entity.setTenantId("test-tenant");
        entity.setType("ACCESS");
        entity.setExpiresAt(LocalDateTime.now().plusHours(1));
        entity.setCreatedAt(LocalDateTime.now());

        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();

        // When
        boolean exists = jpaTokenRepository.existsByToken("test-token-exists-check");
        boolean notExists = jpaTokenRepository.existsByToken("non-existent-token");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    public void testDeleteByToken() {
        // Given
        Token token = new Token();
        token.setToken("test-token-delete");
        token.setUserId("test-user");
        token.setClientId("test-client");
        token.setTenantId("test-tenant");
        token.setType(Token.TokenType.ACCESS);
        token.setExpiresAt(LocalDateTime.now().plusHours(1));
        token.setCreatedAt(LocalDateTime.now());

        jpaTokenRepository.save(token);

        // When
        jpaTokenRepository.delete("test-token-delete");

        // Then
        Optional<Token> foundToken = jpaTokenRepository.findByToken("test-token-delete");
        assertThat(foundToken).isEmpty();
    }

    @Test
    public void testFindExpiredTokens() {
        // Given
        com.aixone.tech.auth.authentication.infrastructure.persistence.entity.TokenEntity expiredEntity = 
            new com.aixone.tech.auth.authentication.infrastructure.persistence.entity.TokenEntity();
        expiredEntity.setToken("expired-token");
        expiredEntity.setUserId("test-user");
        expiredEntity.setClientId("test-client");
        expiredEntity.setTenantId("test-tenant");
        expiredEntity.setType("ACCESS");
        expiredEntity.setExpiresAt(LocalDateTime.now().minusHours(1)); // 已过期
        expiredEntity.setCreatedAt(LocalDateTime.now().minusHours(2));

        com.aixone.tech.auth.authentication.infrastructure.persistence.entity.TokenEntity validEntity = 
            new com.aixone.tech.auth.authentication.infrastructure.persistence.entity.TokenEntity();
        validEntity.setToken("valid-token");
        validEntity.setUserId("test-user");
        validEntity.setClientId("test-client");
        validEntity.setTenantId("test-tenant");
        validEntity.setType("ACCESS");
        validEntity.setExpiresAt(LocalDateTime.now().plusHours(1)); // 未过期
        validEntity.setCreatedAt(LocalDateTime.now());

        entityManager.persist(expiredEntity);
        entityManager.persist(validEntity);
        entityManager.flush();
        entityManager.clear();

        // When
        List<Token> expiredTokens = jpaTokenRepository.findExpiredTokens();

        // Then
        assertThat(expiredTokens).hasSize(1);
        assertThat(expiredTokens.get(0).getToken()).isEqualTo("expired-token");
    }
}

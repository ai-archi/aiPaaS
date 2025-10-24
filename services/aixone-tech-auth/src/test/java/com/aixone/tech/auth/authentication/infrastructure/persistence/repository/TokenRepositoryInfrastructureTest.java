package com.aixone.tech.auth.authentication.infrastructure.persistence.repository;

import com.aixone.tech.auth.authentication.domain.model.Token;
import com.aixone.tech.auth.authentication.domain.repository.TokenRepository;
import com.aixone.tech.auth.authentication.infrastructure.persistence.entity.TokenEntity;
import com.aixone.tech.auth.authentication.infrastructure.persistence.mapper.TokenMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Token Repository 基础设施层测试
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TokenRepositoryInfrastructureTest {

    @Autowired
    private TokenRepository tokenRepository;

    @Test
    public void testSaveAndFindToken() {
        // Given
        Token token = new Token();
        token.setToken("test-token-exists");
        token.setUserId("test-user");
        token.setClientId("test-client");
        token.setTenantId("test-tenant");
        token.setType(Token.TokenType.ACCESS);
        token.setExpiresAt(LocalDateTime.now().plusHours(1));
        token.setCreatedAt(LocalDateTime.now());

        // When
        System.out.println("Before save: " + token);
        Token savedToken = tokenRepository.save(token);
        System.out.println("After save: " + savedToken);
        System.out.println("Before find");
        Optional<Token> foundToken = tokenRepository.findByToken("test-token-exists");
        System.out.println("Found token: " + foundToken);

        // Then
        assertThat(savedToken).isNotNull();
        assertThat(savedToken.getToken()).isEqualTo("test-token-exists");
        assertThat(foundToken).isPresent();
        assertThat(foundToken.get().getToken()).isEqualTo("test-token-exists");
        assertThat(foundToken.get().getUserId()).isEqualTo("test-user");
    }

    @Test
    public void testFindByUserIdAndTenantId() {
        // Given
        Token token1 = new Token();
        token1.setToken("token-1");
        token1.setUserId("test-user");
        token1.setClientId("client-1");
        token1.setTenantId("test-tenant");
        token1.setType(Token.TokenType.ACCESS);
        token1.setExpiresAt(LocalDateTime.now().plusHours(1));
        token1.setCreatedAt(LocalDateTime.now());

        Token token2 = new Token();
        token2.setToken("token-2");
        token2.setUserId("test-user");
        token2.setClientId("client-2");
        token2.setTenantId("test-tenant");
        token2.setType(Token.TokenType.REFRESH);
        token2.setExpiresAt(LocalDateTime.now().plusDays(7));
        token2.setCreatedAt(LocalDateTime.now());

        tokenRepository.save(token1);
        tokenRepository.save(token2);

        // When
        List<Token> tokens = tokenRepository.findByUserIdAndTenantId("test-user", "test-tenant");

        // Then
        assertThat(tokens).hasSize(2);
        assertThat(tokens).extracting(Token::getToken).containsExactlyInAnyOrder("token-1", "token-2");
    }

    @Test
    public void testFindByClientIdAndTenantId() {
        // Given
        Token token1 = new Token();
        token1.setToken("token-user-1");
        token1.setUserId("user-1");
        token1.setClientId("test-client");
        token1.setTenantId("test-tenant");
        token1.setType(Token.TokenType.ACCESS);
        token1.setExpiresAt(LocalDateTime.now().plusHours(1));
        token1.setCreatedAt(LocalDateTime.now());

        Token token2 = new Token();
        token2.setToken("token-user-2");
        token2.setUserId("user-2");
        token2.setClientId("test-client");
        token2.setTenantId("test-tenant");
        token2.setType(Token.TokenType.REFRESH);
        token2.setExpiresAt(LocalDateTime.now().plusDays(7));
        token2.setCreatedAt(LocalDateTime.now());

        tokenRepository.save(token1);
        tokenRepository.save(token2);

        // When
        List<Token> tokens = tokenRepository.findByClientIdAndTenantId("test-client", "test-tenant");

        // Then
        assertThat(tokens).hasSize(2);
        assertThat(tokens).extracting(Token::getToken).containsExactlyInAnyOrder("token-user-1", "token-user-2");
    }

    @Test
    public void testExistsByToken() {
        // Given
        Token token = new Token();
        token.setToken("test-token-exists-check");
        token.setUserId("test-user");
        token.setClientId("test-client");
        token.setTenantId("test-tenant");
        token.setType(Token.TokenType.ACCESS);
        token.setExpiresAt(LocalDateTime.now().plusHours(1));
        token.setCreatedAt(LocalDateTime.now());

        tokenRepository.save(token);

        // When
        boolean exists = tokenRepository.existsByToken("test-token-exists-check");
        boolean notExists = tokenRepository.existsByToken("non-existent-token");

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

        tokenRepository.save(token);

        // When
        tokenRepository.delete("test-token-delete");

        // Then
        Optional<Token> foundToken = tokenRepository.findByToken("test-token-delete");
        assertThat(foundToken).isEmpty();
    }

    @Test
    public void testFindExpiredTokens() {
        // Given
        Token expiredToken = new Token();
        expiredToken.setToken("expired-token");
        expiredToken.setUserId("test-user");
        expiredToken.setClientId("test-client");
        expiredToken.setTenantId("test-tenant");
        expiredToken.setType(Token.TokenType.ACCESS);
        expiredToken.setExpiresAt(LocalDateTime.now().minusHours(1)); // 已过期
        expiredToken.setCreatedAt(LocalDateTime.now().minusHours(2));

        Token validToken = new Token();
        validToken.setToken("valid-token");
        validToken.setUserId("test-user");
        validToken.setClientId("test-client");
        validToken.setTenantId("test-tenant");
        validToken.setType(Token.TokenType.ACCESS);
        validToken.setExpiresAt(LocalDateTime.now().plusHours(1)); // 未过期
        validToken.setCreatedAt(LocalDateTime.now());

        tokenRepository.save(expiredToken);
        tokenRepository.save(validToken);

        // When
        List<Token> expiredTokens = tokenRepository.findExpiredTokens();

        // Then
        assertThat(expiredTokens).hasSize(1);
        assertThat(expiredTokens.get(0).getToken()).isEqualTo("expired-token");
    }
}

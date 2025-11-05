package com.aixone.tech.auth.authentication.infrastructure.persistence.repository;

import com.aixone.tech.auth.authentication.domain.model.VerificationCode;
import com.aixone.tech.auth.authentication.domain.repository.VerificationCodeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * VerificationCode Repository 基础设施层测试
 */
@DataJpaTest
@ActiveProfiles("test")
@ComponentScan(basePackages = "com.aixone.tech.auth")
public class VerificationCodeRepositoryInfrastructureTest {

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    @Test
    public void testSaveAndFindVerificationCode() {
        // Given
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setId(java.util.UUID.randomUUID().toString()); // 手动设置ID
        verificationCode.setCode("123456");
        verificationCode.setPhone("13800138000");
        verificationCode.setEmail("test@example.com");
        verificationCode.setType("SMS");
        verificationCode.setTenantId("test-tenant");
        verificationCode.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        verificationCode.setUsed(false);
        verificationCode.setCreatedAt(LocalDateTime.now());

        // When
        VerificationCode savedCode = verificationCodeRepository.save(verificationCode);
        Optional<VerificationCode> foundCode = verificationCodeRepository.findByPhoneAndTenantIdAndCodeAndUsedFalseAndExpiresAtAfter(
                "13800138000", "test-tenant", "123456", LocalDateTime.now());

        // Then
        assertThat(savedCode).isNotNull();
        assertThat(savedCode.getCode()).isEqualTo("123456");
        assertThat(foundCode).isPresent();
        assertThat(foundCode.get().getCode()).isEqualTo("123456");
        assertThat(foundCode.get().getPhone()).isEqualTo("13800138000");
    }

    @Test
    public void testFindByPhoneAndTenantIdAndCodeAndUsedFalseAndExpiresAtAfter() {
        // Given
        VerificationCode validCode = new VerificationCode();
        validCode.setId(java.util.UUID.randomUUID().toString());
        validCode.setCode("123456");
        validCode.setPhone("13800138000");
        validCode.setType("SMS");
        validCode.setTenantId("test-tenant");
        validCode.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        validCode.setUsed(false);
        validCode.setCreatedAt(LocalDateTime.now());

        VerificationCode expiredCode = new VerificationCode();
        expiredCode.setId(java.util.UUID.randomUUID().toString());
        expiredCode.setCode("654321");
        expiredCode.setPhone("13800138000");
        expiredCode.setType("SMS");
        expiredCode.setTenantId("test-tenant");
        expiredCode.setExpiresAt(LocalDateTime.now().minusMinutes(1)); // 已过期
        expiredCode.setUsed(false);
        expiredCode.setCreatedAt(LocalDateTime.now().minusMinutes(2));

        verificationCodeRepository.save(validCode);
        verificationCodeRepository.save(expiredCode);

        // When
        Optional<VerificationCode> foundValidCode = verificationCodeRepository.findByPhoneAndTenantIdAndCodeAndUsedFalseAndExpiresAtAfter(
                "13800138000", "test-tenant", "123456", LocalDateTime.now());
        Optional<VerificationCode> foundExpiredCode = verificationCodeRepository.findByPhoneAndTenantIdAndCodeAndUsedFalseAndExpiresAtAfter(
                "13800138000", "test-tenant", "654321", LocalDateTime.now());

        // Then
        assertThat(foundValidCode).isPresent();
        assertThat(foundValidCode.get().getCode()).isEqualTo("123456");
        assertThat(foundExpiredCode).isEmpty();
    }

    @Test
    public void testFindByEmailAndTenantIdAndCodeAndUsedFalseAndExpiresAtAfter() {
        // Given
        VerificationCode validCode = new VerificationCode();
        validCode.setId(java.util.UUID.randomUUID().toString());
        validCode.setCode("123456");
        validCode.setEmail("test@example.com");
        validCode.setType("EMAIL");
        validCode.setTenantId("test-tenant");
        validCode.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        validCode.setUsed(false);
        validCode.setCreatedAt(LocalDateTime.now());

        verificationCodeRepository.save(validCode);

        // When
        Optional<VerificationCode> foundCode = verificationCodeRepository.findByEmailAndTenantIdAndCodeAndUsedFalseAndExpiresAtAfter(
                "test@example.com", "test-tenant", "123456", LocalDateTime.now());

        // Then
        assertThat(foundCode).isPresent();
        assertThat(foundCode.get().getCode()).isEqualTo("123456");
        assertThat(foundCode.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    public void testIsPhoneInCooldown() {
        // Given
        VerificationCode recentCode = new VerificationCode();
        recentCode.setId(java.util.UUID.randomUUID().toString());
        recentCode.setCode("123456");
        recentCode.setPhone("13800138000");
        recentCode.setType("SMS");
        recentCode.setTenantId("test-tenant");
        recentCode.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        recentCode.setUsed(false);
        recentCode.setCreatedAt(LocalDateTime.now().minusSeconds(30)); // 30秒前发送

        verificationCodeRepository.save(recentCode);

        // When
        boolean inCooldown = verificationCodeRepository.isPhoneInCooldown("13800138000", "test-tenant", 1);

        // Then
        assertThat(inCooldown).isTrue();
    }

    @Test
    public void testIsEmailInCooldown() {
        // Given
        VerificationCode recentCode = new VerificationCode();
        recentCode.setId(java.util.UUID.randomUUID().toString());
        recentCode.setCode("123456");
        recentCode.setEmail("test@example.com");
        recentCode.setType("EMAIL");
        recentCode.setTenantId("test-tenant");
        recentCode.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        recentCode.setUsed(false);
        recentCode.setCreatedAt(LocalDateTime.now().minusSeconds(30)); // 30秒前发送

        verificationCodeRepository.save(recentCode);

        // When
        boolean inCooldown = verificationCodeRepository.isEmailInCooldown("test@example.com", "test-tenant", 1);

        // Then
        assertThat(inCooldown).isTrue();
    }
}

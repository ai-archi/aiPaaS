package com.aixone.tech.auth.authentication.domain.service;

import com.aixone.tech.auth.authentication.domain.model.VerificationCode;
import com.aixone.tech.auth.authentication.domain.repository.VerificationCodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * 验证码领域服务测试
 */
@ExtendWith(MockitoExtension.class)
class VerificationCodeDomainServiceTest {

    @Mock
    private VerificationCodeRepository verificationCodeRepository;

    @InjectMocks
    private VerificationCodeDomainService verificationCodeDomainService;

    @Test
    void testGenerateCode() {
        // When
        String code = verificationCodeDomainService.generateCode();

        // Then
        assertThat(code).isNotNull();
        assertThat(code).hasSize(6);
        assertThat(code).matches("\\d{6}");
    }

    @Test
    void testIsPhoneInCooldown_NotInCooldown() {
        // Given
        String phone = "13800138000";
        String tenantId = "test-tenant";

        when(verificationCodeRepository.isPhoneInCooldown(phone, tenantId, 1))
                .thenReturn(false);

        // When
        boolean inCooldown = verificationCodeDomainService.isPhoneInCooldown(phone, tenantId);

        // Then
        assertThat(inCooldown).isFalse();
    }

    @Test
    void testIsPhoneInCooldown_InCooldown() {
        // Given
        String phone = "13800138000";
        String tenantId = "test-tenant";

        when(verificationCodeRepository.isPhoneInCooldown(phone, tenantId, 1))
                .thenReturn(true);

        // When
        boolean inCooldown = verificationCodeDomainService.isPhoneInCooldown(phone, tenantId);

        // Then
        assertThat(inCooldown).isTrue();
    }

    @Test
    void testIsEmailInCooldown_NotInCooldown() {
        // Given
        String email = "test@example.com";
        String tenantId = "test-tenant";

        when(verificationCodeRepository.isEmailInCooldown(email, tenantId, 1))
                .thenReturn(false);

        // When
        boolean inCooldown = verificationCodeDomainService.isEmailInCooldown(email, tenantId);

        // Then
        assertThat(inCooldown).isFalse();
    }

    @Test
    void testIsEmailInCooldown_InCooldown() {
        // Given
        String email = "test@example.com";
        String tenantId = "test-tenant";

        when(verificationCodeRepository.isEmailInCooldown(email, tenantId, 1))
                .thenReturn(true);

        // When
        boolean inCooldown = verificationCodeDomainService.isEmailInCooldown(email, tenantId);

        // Then
        assertThat(inCooldown).isTrue();
    }
}

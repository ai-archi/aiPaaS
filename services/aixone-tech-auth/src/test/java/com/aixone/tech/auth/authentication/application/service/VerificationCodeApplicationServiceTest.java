package com.aixone.tech.auth.authentication.application.service;

import com.aixone.tech.auth.authentication.application.command.SendVerificationCodeCommand;
import com.aixone.tech.auth.authentication.application.dto.verification.SendVerificationCodeResponse;
import com.aixone.tech.auth.authentication.domain.model.VerificationCode;
import com.aixone.tech.auth.authentication.domain.repository.VerificationCodeRepository;
import com.aixone.tech.auth.authentication.domain.service.VerificationCodeDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 验证码应用服务测试
 */
@ExtendWith(MockitoExtension.class)
class VerificationCodeApplicationServiceTest {

    @Mock
    private VerificationCodeRepository verificationCodeRepository;

    @Mock
    private VerificationCodeDomainService verificationCodeDomainService;

    @InjectMocks
    private VerificationCodeApplicationService verificationCodeService;

    private VerificationCode testVerificationCode;

    @BeforeEach
    void setUp() {
        testVerificationCode = new VerificationCode();
        testVerificationCode.setId("test-id");
        testVerificationCode.setCode("123456");
        testVerificationCode.setPhone("13800138000");
        testVerificationCode.setEmail("test@example.com");
        testVerificationCode.setType("SMS");
        testVerificationCode.setTenantId("test-tenant");
        testVerificationCode.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        testVerificationCode.setUsed(false);
    }

    @Test
    void testSendSmsCode_Success() {
        // Given
        SendVerificationCodeCommand command = new SendVerificationCodeCommand();
        command.setTenantId("test-tenant");
        command.setPhone("13800138000");
        command.setType("SMS");

        when(verificationCodeDomainService.generateSmsCode("13800138000", "test-tenant", 5))
                .thenReturn(testVerificationCode);

        // When
        SendVerificationCodeResponse response = verificationCodeService.sendSmsCode(command);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getCodeId()).isEqualTo("test-id");
        assertThat(response.getMessage()).contains("验证码发送成功");

        verify(verificationCodeDomainService).generateSmsCode("13800138000", "test-tenant", 5);
    }

    @Test
    void testSendSmsCode_PhoneInCooldown() {
        // Given
        SendVerificationCodeCommand command = new SendVerificationCodeCommand();
        command.setTenantId("test-tenant");
        command.setPhone("13800138000");
        command.setType("SMS");

        when(verificationCodeDomainService.generateSmsCode("13800138000", "test-tenant", 5))
                .thenThrow(new IllegalStateException("验证码发送过于频繁，请稍后再试"));

        // When
        SendVerificationCodeResponse response = verificationCodeService.sendSmsCode(command);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("验证码发送过于频繁，请稍后再试");

        verify(verificationCodeDomainService).generateSmsCode("13800138000", "test-tenant", 5);
    }

    @Test
    void testSendEmailCode_Success() {
        // Given
        SendVerificationCodeCommand command = new SendVerificationCodeCommand();
        command.setTenantId("test-tenant");
        command.setEmail("test@example.com");
        command.setType("EMAIL");

        when(verificationCodeDomainService.generateEmailCode("test@example.com", "test-tenant", 10))
                .thenReturn(testVerificationCode);

        // When
        SendVerificationCodeResponse response = verificationCodeService.sendEmailCode(command);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getCodeId()).isEqualTo("test-id");
        assertThat(response.getMessage()).contains("验证码发送成功");

        verify(verificationCodeDomainService).generateEmailCode("test@example.com", "test-tenant", 10);
    }

    @Test
    void testSendEmailCode_EmailInCooldown() {
        // Given
        SendVerificationCodeCommand command = new SendVerificationCodeCommand();
        command.setTenantId("test-tenant");
        command.setEmail("test@example.com");
        command.setType("EMAIL");

        when(verificationCodeDomainService.generateEmailCode("test@example.com", "test-tenant", 10))
                .thenThrow(new IllegalStateException("验证码发送过于频繁，请稍后再试"));

        // When
        SendVerificationCodeResponse response = verificationCodeService.sendEmailCode(command);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("验证码发送过于频繁，请稍后再试");

        verify(verificationCodeDomainService).generateEmailCode("test@example.com", "test-tenant", 10);
    }

    @Test
    void testVerifyCode_Success() {
        // Given
        String phone = "13800138000";
        String code = "123456";
        String tenantId = "test-tenant";

        when(verificationCodeDomainService.verifySmsCode(phone, tenantId, code))
                .thenReturn(true);

        // When
        boolean isValid = verificationCodeService.verifyCode(phone, tenantId, code);

        // Then
        assertThat(isValid).isTrue();

        verify(verificationCodeDomainService).verifySmsCode(phone, tenantId, code);
    }

    @Test
    void testVerifyCode_InvalidCode() {
        // Given
        String phone = "13800138000";
        String code = "invalid-code";
        String tenantId = "test-tenant";

        when(verificationCodeDomainService.verifySmsCode(phone, tenantId, code))
                .thenReturn(false);

        // When
        boolean isValid = verificationCodeService.verifyCode(phone, tenantId, code);

        // Then
        assertThat(isValid).isFalse();

        verify(verificationCodeDomainService).verifySmsCode(phone, tenantId, code);
    }
}

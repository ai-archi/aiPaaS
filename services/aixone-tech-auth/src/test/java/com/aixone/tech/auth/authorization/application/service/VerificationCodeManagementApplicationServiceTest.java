package com.aixone.tech.auth.authorization.application.service;

import com.aixone.tech.auth.authentication.application.dto.verification.SendVerificationCodeRequest;
import com.aixone.tech.auth.authentication.application.dto.verification.VerificationCodeResponse;
import com.aixone.tech.auth.authentication.application.dto.verification.VerifyCodeRequest;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * 验证码管理应用服务测试
 */
@ExtendWith(MockitoExtension.class)
class VerificationCodeManagementApplicationServiceTest {

    @Mock
    private VerificationCodeRepository verificationCodeRepository;

    @Mock
    private VerificationCodeDomainService verificationCodeDomainService;

    @InjectMocks
    private VerificationCodeManagementApplicationService verificationCodeManagementApplicationService;

    private String tenantId;
    private String userId;
    private String phone;
    private String email;
    private String code;
    private VerificationCode testVerificationCode;

    @BeforeEach
    void setUp() {
        tenantId = "test-tenant";
        userId = "test-user";
        phone = "+8613800138000";
        email = "test@example.com";
        code = "123456";

        testVerificationCode = new VerificationCode(
            UUID.randomUUID().toString(),
            tenantId,
            phone,
            null,
            code,
            "SMS",
            LocalDateTime.now().plusMinutes(5)
        );
    }

    @Test
    void testSendVerificationCode_SMS_Success() {
        // Arrange
        SendVerificationCodeRequest request = new SendVerificationCodeRequest(
            tenantId,
            phone,
            null,
            "SMS"
        );

        when(verificationCodeDomainService.isPhoneInCooldown(phone, tenantId)).thenReturn(false);
        when(verificationCodeDomainService.generateCode()).thenReturn(code);
        when(verificationCodeRepository.save(any(VerificationCode.class))).thenReturn(testVerificationCode);

        // Act
        VerificationCodeResponse response = verificationCodeManagementApplicationService.sendVerificationCode(request);

        // Assert
        assertNotNull(response);
        assertEquals(testVerificationCode.getCodeId(), response.getCodeId());
        assertEquals(tenantId, response.getTenantId());
        assertEquals("SMS", response.getType());
        assertEquals(phone, response.getDestination());
        assertEquals("LOGIN", response.getPurpose());
        assertEquals(code, response.getCode());
        assertFalse(response.isVerified());

        verify(verificationCodeDomainService).isPhoneInCooldown(phone, tenantId);
        verify(verificationCodeDomainService).generateCode();
        verify(verificationCodeRepository).save(any(VerificationCode.class));
    }

    @Test
    void testSendVerificationCode_EMAIL_Success() {
        // Arrange
        SendVerificationCodeRequest request = new SendVerificationCodeRequest(
            tenantId,
            null,
            email,
            "EMAIL"
        );

        when(verificationCodeDomainService.isEmailInCooldown(email, tenantId)).thenReturn(false);
        when(verificationCodeDomainService.generateCode()).thenReturn(code);
        when(verificationCodeRepository.save(any(VerificationCode.class))).thenReturn(testVerificationCode);

        // Act
        VerificationCodeResponse response = verificationCodeManagementApplicationService.sendVerificationCode(request);

        // Assert
        assertNotNull(response);
        assertEquals(testVerificationCode.getCodeId(), response.getCodeId());
        assertEquals(tenantId, response.getTenantId());
        assertEquals("EMAIL", response.getType());
        assertEquals(email, response.getDestination());
        assertEquals("LOGIN", response.getPurpose());
        assertEquals(code, response.getCode());
        assertFalse(response.isVerified());

        verify(verificationCodeDomainService).isEmailInCooldown(email, tenantId);
        verify(verificationCodeDomainService).generateCode();
        verify(verificationCodeRepository).save(any(VerificationCode.class));
    }

    @Test
    void testSendVerificationCode_PhoneInCooldown_ReturnsErrorResponse() {
        // Arrange
        SendVerificationCodeRequest request = new SendVerificationCodeRequest(
            tenantId,
            phone,
            null,
            "SMS"
        );

        when(verificationCodeDomainService.isPhoneInCooldown(phone, tenantId)).thenReturn(true);

        // Act
        VerificationCodeResponse response = verificationCodeManagementApplicationService.sendVerificationCode(request);

        // Assert
        assertNotNull(response);
        assertNull(response.getCodeId());
        assertEquals(tenantId, response.getTenantId());
        assertEquals("SMS", response.getType());
        assertEquals(phone, response.getDestination());
        assertEquals("LOGIN", response.getPurpose());
        assertNull(response.getCode());
        assertFalse(response.isVerified());

        verify(verificationCodeDomainService).isPhoneInCooldown(phone, tenantId);
        verify(verificationCodeDomainService, never()).generateCode();
        verify(verificationCodeRepository, never()).save(any(VerificationCode.class));
    }

    @Test
    void testSendVerificationCode_EmailInCooldown_ReturnsErrorResponse() {
        // Arrange
        SendVerificationCodeRequest request = new SendVerificationCodeRequest(
            tenantId,
            null,
            email,
            "EMAIL"
        );

        when(verificationCodeDomainService.isEmailInCooldown(email, tenantId)).thenReturn(true);

        // Act
        VerificationCodeResponse response = verificationCodeManagementApplicationService.sendVerificationCode(request);

        // Assert
        assertNotNull(response);
        assertNull(response.getCodeId());
        assertEquals(tenantId, response.getTenantId());
        assertEquals("EMAIL", response.getType());
        assertEquals(email, response.getDestination());
        assertEquals("LOGIN", response.getPurpose());
        assertNull(response.getCode());
        assertFalse(response.isVerified());

        verify(verificationCodeDomainService).isEmailInCooldown(email, tenantId);
        verify(verificationCodeDomainService, never()).generateCode();
        verify(verificationCodeRepository, never()).save(any(VerificationCode.class));
    }

    @Test
    void testVerifyCode_SMS_Success() {
        // Arrange
        VerifyCodeRequest request = new VerifyCodeRequest(
            tenantId,
            phone,
            null,
            code,
            "SMS"
        );

        when(verificationCodeRepository.findByPhoneAndTenantIdAndTypeAndExpiresAtAfter(
            eq(phone), eq(tenantId), eq("SMS"), any(LocalDateTime.class)))
            .thenReturn(testVerificationCode);

        // Act
        VerificationCodeResponse result = verificationCodeManagementApplicationService.verifyCode(request);

        // Assert
        assertTrue(result.isSuccess());
        verify(verificationCodeRepository).findByPhoneAndTenantIdAndTypeAndExpiresAtAfter(
            eq(phone), eq(tenantId), eq("SMS"), any(LocalDateTime.class));
        verify(verificationCodeRepository).deleteByCodeId(testVerificationCode.getCodeId());
    }

    @Test
    void testVerifyCode_EMAIL_Success() {
        // Arrange
        VerifyCodeRequest request = new VerifyCodeRequest(
            tenantId,
            null,
            email,
            code,
            "EMAIL"
        );

        when(verificationCodeRepository.findByEmailAndTenantIdAndTypeAndExpiresAtAfter(
            eq(email), eq(tenantId), eq("EMAIL"), any(LocalDateTime.class)))
            .thenReturn(testVerificationCode);

        // Act
        VerificationCodeResponse result = verificationCodeManagementApplicationService.verifyCode(request);

        // Assert
        assertTrue(result.isSuccess());
        verify(verificationCodeRepository).findByEmailAndTenantIdAndTypeAndExpiresAtAfter(
            eq(email), eq(tenantId), eq("EMAIL"), any(LocalDateTime.class));
        verify(verificationCodeRepository).deleteByCodeId(testVerificationCode.getCodeId());
    }

    @Test
    void testVerifyCode_CodeNotFound_ReturnsFalse() {
        // Arrange
        VerifyCodeRequest request = new VerifyCodeRequest(
            tenantId,
            phone,
            null,
            code,
            "SMS"
        );

        when(verificationCodeRepository.findByPhoneAndTenantIdAndTypeAndExpiresAtAfter(
            eq(phone), eq(tenantId), eq("SMS"), any(LocalDateTime.class)))
            .thenReturn(null);

        // Act
        VerificationCodeResponse result = verificationCodeManagementApplicationService.verifyCode(request);

        // Assert
        assertFalse(result.isSuccess());
        verify(verificationCodeRepository).findByPhoneAndTenantIdAndTypeAndExpiresAtAfter(
            eq(phone), eq(tenantId), eq("SMS"), any(LocalDateTime.class));
        verify(verificationCodeRepository, never()).deleteByCodeId(any());
    }

    @Test
    void testVerifyCode_WrongCode_ReturnsFalse() {
        // Arrange
        VerifyCodeRequest request = new VerifyCodeRequest(
            tenantId,
            phone,
            null,
            "wrong-code",
            "SMS"
        );

        when(verificationCodeRepository.findByPhoneAndTenantIdAndTypeAndExpiresAtAfter(
            eq(phone), eq(tenantId), eq("SMS"), any(LocalDateTime.class)))
            .thenReturn(testVerificationCode);

        // Act
        VerificationCodeResponse result = verificationCodeManagementApplicationService.verifyCode(request);

        // Assert
        assertFalse(result.isSuccess());
        verify(verificationCodeRepository).findByPhoneAndTenantIdAndTypeAndExpiresAtAfter(
            eq(phone), eq(tenantId), eq("SMS"), any(LocalDateTime.class));
        verify(verificationCodeRepository, never()).deleteByCodeId(any());
    }

    @Test
    void testVerifyCode_CodeAlreadyVerified_ReturnsFalse() {
        // Arrange
        testVerificationCode.setVerified(true);
        VerifyCodeRequest request = new VerifyCodeRequest(
            tenantId,
            phone,
            null,
            code,
            "SMS"
        );

        when(verificationCodeRepository.findByPhoneAndTenantIdAndTypeAndExpiresAtAfter(
            eq(phone), eq(tenantId), eq("SMS"), any(LocalDateTime.class)))
            .thenReturn(testVerificationCode);

        // Act
        VerificationCodeResponse result = verificationCodeManagementApplicationService.verifyCode(request);

        // Assert
        assertFalse(result.isSuccess());
        verify(verificationCodeRepository).findByPhoneAndTenantIdAndTypeAndExpiresAtAfter(
            eq(phone), eq(tenantId), eq("SMS"), any(LocalDateTime.class));
        verify(verificationCodeRepository, never()).deleteByCodeId(any());
    }

    @Test
    void testVerifyCode_ExpiredCode_ReturnsFalse() {
        // Arrange
        testVerificationCode.setExpiresAt(LocalDateTime.now().minusMinutes(1)); // Expired
        VerifyCodeRequest request = new VerifyCodeRequest(
            tenantId,
            phone,
            null,
            code,
            "SMS"
        );

        // findByPhoneAndTenantIdAndTypeAndExpiresAtAfter should return null for expired codes
        when(verificationCodeRepository.findByPhoneAndTenantIdAndTypeAndExpiresAtAfter(
            eq(phone), eq(tenantId), eq("SMS"), any(LocalDateTime.class)))
            .thenReturn(null);

        // Act
        VerificationCodeResponse result = verificationCodeManagementApplicationService.verifyCode(request);

        // Assert
        assertFalse(result.isSuccess());
        verify(verificationCodeRepository).findByPhoneAndTenantIdAndTypeAndExpiresAtAfter(
            eq(phone), eq(tenantId), eq("SMS"), any(LocalDateTime.class));
        verify(verificationCodeRepository, never()).deleteByCodeId(any());
    }
}

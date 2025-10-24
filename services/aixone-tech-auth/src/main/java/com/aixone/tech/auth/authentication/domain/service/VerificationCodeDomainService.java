package com.aixone.tech.auth.authentication.domain.service;

import com.aixone.tech.auth.authentication.domain.model.VerificationCode;
import com.aixone.tech.auth.authentication.domain.repository.VerificationCodeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * 验证码领域服务
 */
@Service
public class VerificationCodeDomainService {

    private final VerificationCodeRepository verificationCodeRepository;
    private final Random random = new Random();

    public VerificationCodeDomainService(VerificationCodeRepository verificationCodeRepository) {
        this.verificationCodeRepository = verificationCodeRepository;
    }

    /**
     * 生成短信验证码
     */
    public VerificationCode generateSmsCode(String phone, String tenantId, int expireMinutes) {
        // 检查冷却期
        if (verificationCodeRepository.isPhoneInCooldown(phone, tenantId, 1)) {
            throw new IllegalStateException("验证码发送过于频繁，请稍后再试");
        }

        // 生成6位数字验证码
        String code = String.format("%06d", random.nextInt(1000000));
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(expireMinutes);

        VerificationCode verificationCode = new VerificationCode(
            phone, null, tenantId, code, expiresAt, "SMS"
        );

        return verificationCodeRepository.save(verificationCode);
    }

    /**
     * 生成邮箱验证码
     */
    public VerificationCode generateEmailCode(String email, String tenantId, int expireMinutes) {
        // 检查冷却期
        if (verificationCodeRepository.isEmailInCooldown(email, tenantId, 1)) {
            throw new IllegalStateException("验证码发送过于频繁，请稍后再试");
        }

        // 生成6位数字验证码
        String code = String.format("%06d", random.nextInt(1000000));
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(expireMinutes);

        VerificationCode verificationCode = new VerificationCode(
            null, email, tenantId, code, expiresAt, "EMAIL"
        );

        return verificationCodeRepository.save(verificationCode);
    }

    /**
     * 验证短信验证码
     */
    public boolean verifySmsCode(String phone, String tenantId, String code) {
        return verificationCodeRepository.findByPhoneAndTenantIdAndCode(phone, tenantId, code)
                .map(verificationCode -> {
                    if (verificationCode.isValid()) {
                        verificationCode.markAsUsed();
                        verificationCodeRepository.save(verificationCode);
                        return true;
                    }
                    return false;
                })
                .orElse(false);
    }

    /**
     * 验证邮箱验证码
     */
    public boolean verifyEmailCode(String email, String tenantId, String code) {
        return verificationCodeRepository.findByEmailAndTenantIdAndCode(email, tenantId, code)
                .map(verificationCode -> {
                    if (verificationCode.isValid()) {
                        verificationCode.markAsUsed();
                        verificationCodeRepository.save(verificationCode);
                        return true;
                    }
                    return false;
                })
                .orElse(false);
    }

    /**
     * 清理过期验证码
     */
    public void cleanupExpiredCodes() {
        verificationCodeRepository.deleteExpiredCodes();
    }

    /**
     * 清理指定租户的过期验证码
     */
    public void cleanupExpiredCodesByTenant(String tenantId) {
        verificationCodeRepository.deleteExpiredCodesByTenantId(tenantId);
    }

    /**
     * 生成验证码（兼容测试代码）
     */
    public String generateCode() {
        return String.format("%06d", random.nextInt(1000000));
    }

    /**
     * 检查手机号是否在冷却期（兼容测试代码）
     */
    public boolean isPhoneInCooldown(String phone, String tenantId) {
        return verificationCodeRepository.isPhoneInCooldown(phone, tenantId, 1);
    }

    /**
     * 检查邮箱是否在冷却期（兼容测试代码）
     */
    public boolean isEmailInCooldown(String email, String tenantId) {
        return verificationCodeRepository.isEmailInCooldown(email, tenantId, 1);
    }
}

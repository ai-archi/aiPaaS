package com.aixone.tech.auth.authentication.application.service;

import com.aixone.tech.auth.authentication.application.command.SendVerificationCodeCommand;
import com.aixone.tech.auth.authentication.application.dto.verification.SendVerificationCodeResponse;
import com.aixone.tech.auth.authentication.domain.model.VerificationCode;
import com.aixone.tech.auth.authentication.domain.service.VerificationCodeDomainService;
import org.springframework.stereotype.Service;

/**
 * 验证码应用服务
 */
@Service
public class VerificationCodeApplicationService {

    private final VerificationCodeDomainService verificationCodeDomainService;

    public VerificationCodeApplicationService(VerificationCodeDomainService verificationCodeDomainService) {
        this.verificationCodeDomainService = verificationCodeDomainService;
    }

    /**
     * 发送短信验证码
     */
    public SendVerificationCodeResponse sendSmsCode(SendVerificationCodeCommand command) {
        try {
            VerificationCode verificationCode = verificationCodeDomainService.generateSmsCode(
                command.getPhone(), 
                command.getTenantId(), 
                5 // 5分钟过期
            );

            // TODO: 这里应该调用短信服务发送验证码
            // 目前只是生成并保存到数据库
            
            return new SendVerificationCodeResponse(
                true, 
                "验证码发送成功", 
                verificationCode.getId() != null ? verificationCode.getId() : "unknown", 
                5
            );
        } catch (Exception e) {
            return new SendVerificationCodeResponse(
                false, 
                e.getMessage(), 
                null, 
                0
            );
        }
    }

    /**
     * 发送邮箱验证码
     */
    public SendVerificationCodeResponse sendEmailCode(SendVerificationCodeCommand command) {
        try {
            VerificationCode verificationCode = verificationCodeDomainService.generateEmailCode(
                command.getEmail(), 
                command.getTenantId(), 
                10 // 10分钟过期
            );

            // TODO: 这里应该调用邮件服务发送验证码
            // 目前只是生成并保存到数据库
            
            return new SendVerificationCodeResponse(
                true, 
                "验证码发送成功", 
                verificationCode.getId() != null ? verificationCode.getId() : "unknown", 
                10
            );
        } catch (Exception e) {
            return new SendVerificationCodeResponse(
                false, 
                e.getMessage(), 
                null, 
                0
            );
        }
    }

    /**
     * 验证短信验证码
     */
    public boolean verifySmsCode(String phone, String tenantId, String code) {
        return verificationCodeDomainService.verifySmsCode(phone, tenantId, code);
    }

    /**
     * 验证邮箱验证码
     */
    public boolean verifyEmailCode(String email, String tenantId, String code) {
        return verificationCodeDomainService.verifyEmailCode(email, tenantId, code);
    }

    /**
     * 验证验证码（兼容测试代码）
     */
    public boolean verifyCode(String phone, String tenantId, String code) {
        if (phone != null && !phone.isEmpty()) {
            return verifySmsCode(phone, tenantId, code);
        }
        return false;
    }
}

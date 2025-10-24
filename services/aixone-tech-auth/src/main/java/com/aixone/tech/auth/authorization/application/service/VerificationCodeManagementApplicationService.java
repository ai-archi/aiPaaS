package com.aixone.tech.auth.authorization.application.service;

import com.aixone.tech.auth.authentication.application.dto.verification.SendVerificationCodeRequest;
import com.aixone.tech.auth.authentication.application.dto.verification.VerificationCodeResponse;
import com.aixone.tech.auth.authentication.application.dto.verification.VerifyCodeRequest;
import com.aixone.tech.auth.authentication.domain.model.VerificationCode;
import com.aixone.tech.auth.authentication.domain.repository.VerificationCodeRepository;
import com.aixone.tech.auth.authentication.domain.service.VerificationCodeDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class VerificationCodeManagementApplicationService {

    private final VerificationCodeRepository verificationCodeRepository;
    private final VerificationCodeDomainService verificationCodeDomainService;

    public VerificationCodeManagementApplicationService(
            VerificationCodeRepository verificationCodeRepository,
            VerificationCodeDomainService verificationCodeDomainService) {
        this.verificationCodeRepository = verificationCodeRepository;
        this.verificationCodeDomainService = verificationCodeDomainService;
    }

    public VerificationCodeResponse sendVerificationCode(SendVerificationCodeRequest request) {
        try {
            // Check cooldown period
            if ("SMS".equals(request.getType()) && request.getPhone() != null) {
            if (verificationCodeDomainService.isPhoneInCooldown(request.getPhone(), request.getTenantId())) {
                VerificationCodeResponse response = new VerificationCodeResponse(false, "请稍后再试，验证码发送过于频繁", null, 0);
                response.setTenantId(request.getTenantId());
                response.setType(request.getType());
                response.setDestination(request.getPhone());
                response.setPurpose("LOGIN");
                response.setVerified(false);
                return response;
            }
            } else if ("EMAIL".equals(request.getType()) && request.getEmail() != null) {
                if (verificationCodeDomainService.isEmailInCooldown(request.getEmail(), request.getTenantId())) {
                    VerificationCodeResponse response = new VerificationCodeResponse(false, "请稍后再试，验证码发送过于频繁", null, 0);
                    response.setTenantId(request.getTenantId());
                    response.setType(request.getType());
                    response.setDestination(request.getEmail());
                    response.setPurpose("LOGIN");
                    response.setVerified(false);
                    return response;
                }
            }

            // Generate verification code
            String code = verificationCodeDomainService.generateCode();
            String codeId = UUID.randomUUID().toString();
            
            // Create verification code entity
            VerificationCode verificationCode = new VerificationCode(
                codeId,
                request.getTenantId(),
                request.getPhone(),
                request.getEmail(),
                code,
                request.getType(),
                LocalDateTime.now().plusMinutes(5) // 5 minutes expiry
            );
            
            verificationCode = verificationCodeRepository.save(verificationCode);
            
            // Send code via SMS or Email (this would integrate with actual SMS/Email service)
            if ("SMS".equals(request.getType())) {
                // TODO: Integrate with SMS service
                System.out.println("SMS Code sent to " + request.getPhone() + ": " + code);
            } else if ("EMAIL".equals(request.getType())) {
                // TODO: Integrate with Email service
                System.out.println("Email Code sent to " + request.getEmail() + ": " + code);
            }
            
            VerificationCodeResponse response = new VerificationCodeResponse(true, "验证码发送成功", verificationCode.getCodeId(), 300);
            response.setTenantId(request.getTenantId());
            response.setType(request.getType());
            response.setDestination(request.getPhone() != null ? request.getPhone() : request.getEmail());
            response.setPurpose("LOGIN"); // 默认目的
            response.setCode(code);
            response.setVerified(false);
            return response;
            
        } catch (Exception e) {
            return new VerificationCodeResponse(false, "验证码发送失败: " + e.getMessage(), null, 0);
        }
    }

    public VerificationCodeResponse verifyCode(VerifyCodeRequest request) {
        try {
            VerificationCode verificationCode;
            
            if ("SMS".equals(request.getType()) && request.getPhone() != null) {
                verificationCode = verificationCodeRepository.findByPhoneAndTenantIdAndTypeAndExpiresAtAfter(
                    request.getPhone(), request.getTenantId(), request.getType(), LocalDateTime.now()
                );
            } else if ("EMAIL".equals(request.getType()) && request.getEmail() != null) {
                verificationCode = verificationCodeRepository.findByEmailAndTenantIdAndTypeAndExpiresAtAfter(
                    request.getEmail(), request.getTenantId(), request.getType(), LocalDateTime.now()
                );
            } else {
                return new VerificationCodeResponse(false, "验证码类型或联系方式不正确", null, 0);
            }
            
            if (verificationCode == null) {
                return new VerificationCodeResponse(false, "验证码不存在或已过期", null, 0);
            }
            
            if (!verificationCode.getCode().equals(request.getCode())) {
                return new VerificationCodeResponse(false, "验证码错误", null, 0);
            }
            
            if (verificationCode.isVerified()) {
                return new VerificationCodeResponse(false, "验证码已使用", null, 0);
            }
            
            // Mark as used and delete
            verificationCodeRepository.deleteByCodeId(verificationCode.getCodeId());
            
            return new VerificationCodeResponse(true, "验证码验证成功", verificationCode.getCodeId(), 0);
            
        } catch (Exception e) {
            return new VerificationCodeResponse(false, "验证码验证失败: " + e.getMessage(), null, 0);
        }
    }
}

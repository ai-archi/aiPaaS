package com.aixone.tech.auth.authentication.application.service;

import com.aixone.tech.auth.authentication.application.command.SendVerificationCodeCommand;
import com.aixone.tech.auth.authentication.application.dto.verification.SendVerificationCodeRequest;
import com.aixone.tech.auth.authentication.application.dto.verification.SendVerificationCodeResponse;
import com.aixone.tech.auth.authentication.application.dto.verification.VerificationCodeResponse;
import com.aixone.tech.auth.authentication.application.dto.verification.VerifyCodeRequest;
import com.aixone.tech.auth.authentication.domain.model.VerificationCode;
import com.aixone.tech.auth.authentication.domain.repository.VerificationCodeRepository;
import com.aixone.tech.auth.authentication.domain.service.VerificationCodeDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 验证码应用服务
 * 负责验证码的发送、验证等业务逻辑
 */
@Service
@Transactional
public class VerificationCodeApplicationService {

    private final VerificationCodeDomainService verificationCodeDomainService;
    private final VerificationCodeRepository verificationCodeRepository;

    public VerificationCodeApplicationService(
            VerificationCodeDomainService verificationCodeDomainService,
            VerificationCodeRepository verificationCodeRepository) {
        this.verificationCodeDomainService = verificationCodeDomainService;
        this.verificationCodeRepository = verificationCodeRepository;
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

    /**
     * 发送验证码（REST API接口）
     */
    public VerificationCodeResponse sendVerificationCode(SendVerificationCodeRequest request) {
        try {
            // 检查冷却期
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

            // 生成验证码
            String code = verificationCodeDomainService.generateCode();
            String codeId = UUID.randomUUID().toString();
            
            // 创建验证码实体
            VerificationCode verificationCode = new VerificationCode(
                codeId,
                request.getTenantId(),
                request.getPhone(),
                request.getEmail(),
                code,
                request.getType(),
                LocalDateTime.now().plusMinutes(5) // 5分钟过期
            );
            
            verificationCode = verificationCodeRepository.save(verificationCode);
            
            // 发送验证码（TODO: 集成短信/邮件服务）
            // 注意：实际生产环境需要集成短信和邮件服务发送验证码
            if ("SMS".equals(request.getType())) {
                // TODO: 集成短信服务发送验证码
            } else if ("EMAIL".equals(request.getType())) {
                // TODO: 集成邮件服务发送验证码
            }
            
            VerificationCodeResponse response = new VerificationCodeResponse(true, "验证码发送成功", verificationCode.getId(), 300);
            response.setTenantId(request.getTenantId());
            response.setType(request.getType());
            response.setDestination(request.getPhone() != null ? request.getPhone() : request.getEmail());
            response.setPurpose("LOGIN");
            response.setCode(code);
            response.setVerified(false);
            return response;
            
        } catch (Exception e) {
            return new VerificationCodeResponse(false, "验证码发送失败: " + e.getMessage(), null, 0);
        }
    }

    /**
     * 验证验证码（REST API接口）
     */
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
            
            // 标记为已使用并删除
            verificationCodeRepository.deleteByCodeId(verificationCode.getId());
            
            return new VerificationCodeResponse(true, "验证码验证成功", verificationCode.getId(), 0);
            
        } catch (Exception e) {
            return new VerificationCodeResponse(false, "验证码验证失败: " + e.getMessage(), null, 0);
        }
    }
}

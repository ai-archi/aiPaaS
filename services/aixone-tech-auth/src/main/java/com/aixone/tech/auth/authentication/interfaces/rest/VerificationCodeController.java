package com.aixone.tech.auth.authentication.interfaces.rest;

import com.aixone.common.api.ApiResponse;
import com.aixone.tech.auth.authentication.application.dto.verification.SendVerificationCodeRequest;
import com.aixone.tech.auth.authentication.application.dto.verification.VerificationCodeResponse;
import com.aixone.tech.auth.authentication.application.dto.verification.VerifyCodeRequest;
import com.aixone.tech.auth.authentication.application.service.VerificationCodeApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 验证码控制器
 * 负责验证码的发送和验证
 */
@RestController
@RequestMapping("/verification-codes")
public class VerificationCodeController {

    private final VerificationCodeApplicationService verificationCodeApplicationService;

    public VerificationCodeController(VerificationCodeApplicationService verificationCodeApplicationService) {
        this.verificationCodeApplicationService = verificationCodeApplicationService;
    }

    /**
     * 发送验证码
     */
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<VerificationCodeResponse>> sendVerificationCode(@Valid @RequestBody SendVerificationCodeRequest request) {
        try {
            VerificationCodeResponse response = verificationCodeApplicationService.sendVerificationCode(request);
            return ResponseEntity.ok(ApiResponse.success(response, "验证码发送成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(500, "验证码发送失败: " + e.getMessage()));
        }
    }

    /**
     * 验证验证码
     */
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<VerificationCodeResponse>> verifyCode(@Valid @RequestBody VerifyCodeRequest request) {
        try {
            VerificationCodeResponse response = verificationCodeApplicationService.verifyCode(request);
            return ResponseEntity.ok(ApiResponse.success(response, "验证码验证成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(400, "验证码验证失败: " + e.getMessage()));
        }
    }
}


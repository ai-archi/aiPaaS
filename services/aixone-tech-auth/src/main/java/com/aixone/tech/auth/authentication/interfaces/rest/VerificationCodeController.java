package com.aixone.tech.auth.authentication.interfaces.rest;

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
@RequestMapping("/api/v1/auth/verification-codes")
public class VerificationCodeController {

    private final VerificationCodeApplicationService verificationCodeApplicationService;

    public VerificationCodeController(VerificationCodeApplicationService verificationCodeApplicationService) {
        this.verificationCodeApplicationService = verificationCodeApplicationService;
    }

    /**
     * 发送验证码
     */
    @PostMapping("/send")
    public ResponseEntity<VerificationCodeResponse> sendVerificationCode(@Valid @RequestBody SendVerificationCodeRequest request) {
        VerificationCodeResponse response = verificationCodeApplicationService.sendVerificationCode(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 验证验证码
     */
    @PostMapping("/verify")
    public ResponseEntity<VerificationCodeResponse> verifyCode(@Valid @RequestBody VerifyCodeRequest request) {
        VerificationCodeResponse response = verificationCodeApplicationService.verifyCode(request);
        return ResponseEntity.ok(response);
    }
}


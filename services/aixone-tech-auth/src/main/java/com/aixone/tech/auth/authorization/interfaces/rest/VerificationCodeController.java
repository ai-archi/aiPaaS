package com.aixone.tech.auth.authorization.interfaces.rest;

import com.aixone.tech.auth.authentication.application.dto.verification.SendVerificationCodeRequest;
import com.aixone.tech.auth.authentication.application.dto.verification.VerificationCodeResponse;
import com.aixone.tech.auth.authentication.application.dto.verification.VerifyCodeRequest;
import com.aixone.tech.auth.authorization.application.service.VerificationCodeManagementApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/verification-codes")
public class VerificationCodeController {

    private final VerificationCodeManagementApplicationService verificationCodeManagementApplicationService;

    public VerificationCodeController(VerificationCodeManagementApplicationService verificationCodeManagementApplicationService) {
        this.verificationCodeManagementApplicationService = verificationCodeManagementApplicationService;
    }

    @PostMapping("/send")
    public ResponseEntity<VerificationCodeResponse> sendVerificationCode(@Valid @RequestBody SendVerificationCodeRequest request) {
        VerificationCodeResponse response = verificationCodeManagementApplicationService.sendVerificationCode(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<VerificationCodeResponse> verifyCode(@Valid @RequestBody VerifyCodeRequest request) {
        VerificationCodeResponse response = verificationCodeManagementApplicationService.verifyCode(request);
        return ResponseEntity.ok(response);
    }
}

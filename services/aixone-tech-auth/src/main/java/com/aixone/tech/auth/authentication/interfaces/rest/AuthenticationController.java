package com.aixone.tech.auth.authentication.interfaces.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aixone.audit.application.AuditService;
import com.aixone.tech.auth.authentication.application.command.LoginCommand;
import com.aixone.tech.auth.authentication.application.command.RefreshTokenCommand;
import com.aixone.tech.auth.authentication.application.dto.auth.LoginRequest;
import com.aixone.tech.auth.authentication.application.dto.auth.TokenResponse;
import com.aixone.tech.auth.authentication.application.service.AuthenticationApplicationService;
import com.aixone.tech.auth.authentication.application.service.VerificationCodeApplicationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationApplicationService authenticationService;
    private final VerificationCodeApplicationService verificationCodeService;
    private final AuditService auditService;

    public AuthenticationController(AuthenticationApplicationService authenticationService,
                                   VerificationCodeApplicationService verificationCodeService,
                                   AuditService auditService) {
        this.authenticationService = authenticationService;
        this.verificationCodeService = verificationCodeService;
        this.auditService = auditService;
    }

    /**
     * 用户名密码登录
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request,
                                               HttpServletRequest httpRequest) {
        LoginCommand command = new LoginCommand(
            request.getTenantId(),
            request.getUsername(),
            request.getPassword(),
            request.getClientId()
        );
        command.setClientSecret(request.getClientSecret());
        command.setClientIp(getClientIp(httpRequest));
        command.setUserAgent(httpRequest.getHeader("User-Agent"));

        try {
            TokenResponse response = authenticationService.login(command);
            // 记录登录成功审计日志
            auditService.logLoginSuccess(
                request.getUsername(),
                getClientIp(httpRequest),
                httpRequest.getHeader("User-Agent")
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 记录登录失败审计日志
            auditService.logLoginFailure(
                request.getUsername(),
                e.getMessage(),
                getClientIp(httpRequest),
                httpRequest.getHeader("User-Agent")
            );
            throw e;
        }
    }

    /**
     * 短信验证码登录
     */
    @PostMapping("/sms/login")
    public ResponseEntity<TokenResponse> smsLogin(@Valid @RequestBody LoginRequest request,
                                                HttpServletRequest httpRequest) {
        // 验证短信验证码
        boolean isValidCode = verificationCodeService.verifySmsCode(
            request.getUsername(), // 这里username是手机号
            request.getTenantId(),
            request.getVerificationCode()
        );
        
        if (!isValidCode) {
            throw new IllegalArgumentException("验证码无效或已过期");
        }
        
        // 实现短信验证码登录逻辑
        LoginCommand command = new LoginCommand(
            request.getTenantId(),
            request.getUsername(),
            request.getVerificationCode(),
            request.getClientId()
        );
        command.setClientSecret(request.getClientSecret());
        command.setClientIp(getClientIp(httpRequest));
        command.setUserAgent(httpRequest.getHeader("User-Agent"));

        TokenResponse response = authenticationService.login(command);
        return ResponseEntity.ok(response);
    }

    /**
     * 邮箱验证码登录
     */
    @PostMapping("/email/login")
    public ResponseEntity<TokenResponse> emailLogin(@Valid @RequestBody LoginRequest request,
                                                   HttpServletRequest httpRequest) {
        // 验证邮箱验证码
        boolean isValidCode = verificationCodeService.verifyEmailCode(
            request.getUsername(), // 这里username是邮箱
            request.getTenantId(),
            request.getVerificationCode()
        );
        
        if (!isValidCode) {
            throw new IllegalArgumentException("验证码无效或已过期");
        }
        
        // 实现邮箱验证码登录逻辑
        LoginCommand command = new LoginCommand(
            request.getTenantId(),
            request.getUsername(),
            request.getVerificationCode(),
            request.getClientId()
        );
        command.setClientSecret(request.getClientSecret());
        command.setClientIp(getClientIp(httpRequest));
        command.setUserAgent(httpRequest.getHeader("User-Agent"));

        TokenResponse response = authenticationService.login(command);
        return ResponseEntity.ok(response);
    }

    /**
     * 刷新令牌
     */
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@Valid @RequestBody RefreshTokenCommand command,
                                                      HttpServletRequest httpRequest) {
        command.setClientIp(getClientIp(httpRequest));
        command.setUserAgent(httpRequest.getHeader("User-Agent"));

        TokenResponse response = authenticationService.refreshToken(command);
        return ResponseEntity.ok(response);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authorization,
                                      @RequestParam String tenantId,
                                      HttpServletRequest httpRequest) {
        String token = extractTokenFromAuthorization(authorization);
        authenticationService.logout(token, tenantId);
        
        // 记录登出审计日志
        // 注意：这里需要从token中提取用户ID，实际实现中可能需要解析JWT token
        auditService.logLogout(
            "unknown", // 实际实现中应该从token中提取用户ID
            getClientIp(httpRequest),
            httpRequest.getHeader("User-Agent")
        );
        
        return ResponseEntity.ok().build();
    }

    /**
     * 验证令牌
     */
    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String authorization,
                                                @RequestParam String tenantId) {
        String token = extractTokenFromAuthorization(authorization);
        boolean isValid = authenticationService.validateToken(token, tenantId);
        return ResponseEntity.ok(isValid);
    }

    /**
     * 从Authorization头部提取令牌
     */
    private String extractTokenFromAuthorization(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        throw new IllegalArgumentException("无效的Authorization头部");
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}

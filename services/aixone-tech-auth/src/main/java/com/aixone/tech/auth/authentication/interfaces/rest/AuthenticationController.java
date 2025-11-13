package com.aixone.tech.auth.authentication.interfaces.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aixone.audit.application.AuditService;
import com.aixone.common.api.ApiResponse;
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
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request,
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
            TokenResponse tokenResponse = authenticationService.login(command);
            
            // 记录登录成功审计日志
            auditService.logLoginSuccess(
                request.getUsername(),
                request.getTenantId(),
                getClientIp(httpRequest),
                httpRequest.getHeader("User-Agent")
            );
            
            return ResponseEntity.ok(ApiResponse.success(tokenResponse, "登录成功"));
        } catch (Exception e) {
            // 记录登录失败审计日志
            auditService.logLoginFailure(
                request.getUsername(),
                e.getMessage(),
                request.getTenantId(),
                getClientIp(httpRequest),
                httpRequest.getHeader("User-Agent")
            );
            
            return ResponseEntity.ok(ApiResponse.error(401, "登录失败: " + e.getMessage()));
        }
    }

    /**
     * 短信验证码登录
     */
    @PostMapping("/sms/login")
    public ResponseEntity<ApiResponse<TokenResponse>> smsLogin(@Valid @RequestBody LoginRequest request,
                                                                HttpServletRequest httpRequest) {
        try {
            // 验证短信验证码
            boolean isValidCode = verificationCodeService.verifySmsCode(
                request.getUsername(), // 这里username是手机号
                request.getTenantId(),
                request.getVerificationCode()
            );
            
            if (!isValidCode) {
                return ResponseEntity.ok(ApiResponse.badRequest("验证码无效或已过期"));
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

            TokenResponse tokenResponse = authenticationService.login(command);
            return ResponseEntity.ok(ApiResponse.success(tokenResponse, "登录成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(401, "登录失败: " + e.getMessage()));
        }
    }

    /**
     * 邮箱验证码登录
     */
    @PostMapping("/email/login")
    public ResponseEntity<ApiResponse<TokenResponse>> emailLogin(@Valid @RequestBody LoginRequest request,
                                                                  HttpServletRequest httpRequest) {
        try {
            // 验证邮箱验证码
            boolean isValidCode = verificationCodeService.verifyEmailCode(
                request.getUsername(), // 这里username是邮箱
                request.getTenantId(),
                request.getVerificationCode()
            );
            
            if (!isValidCode) {
                return ResponseEntity.ok(ApiResponse.badRequest("验证码无效或已过期"));
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

            TokenResponse tokenResponse = authenticationService.login(command);
            return ResponseEntity.ok(ApiResponse.success(tokenResponse, "登录成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(401, "登录失败: " + e.getMessage()));
        }
    }

    /**
     * 刷新令牌
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(@Valid @RequestBody RefreshTokenCommand command,
                                                                  HttpServletRequest httpRequest) {
        try {
            command.setClientIp(getClientIp(httpRequest));
            command.setUserAgent(httpRequest.getHeader("User-Agent"));

            TokenResponse tokenResponse = authenticationService.refreshToken(command);
            return ResponseEntity.ok(ApiResponse.success(tokenResponse, "令牌刷新成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(401, "令牌刷新失败: " + e.getMessage()));
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String authorization,
                                                     @RequestParam String tenantId,
                                                     HttpServletRequest httpRequest) {
        try {
            String token = extractTokenFromAuthorization(authorization);
            
            // 从 token 中提取用户ID（在删除 token 之前）
            String userId = authenticationService.getUserIdFromToken(token, tenantId);
            
            // 执行登出操作
            authenticationService.logout(token, tenantId);
            
            // 记录登出审计日志
            auditService.logLogout(
                userId != null ? userId : "unknown",
                tenantId,
                getClientIp(httpRequest),
                httpRequest.getHeader("User-Agent")
            );
            
            return ResponseEntity.ok(ApiResponse.success(null, "登出成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(401, "登出失败: " + e.getMessage()));
        }
    }

    /**
     * 验证令牌
     */
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<Boolean>> validateToken(@RequestHeader("Authorization") String authorization,
                                                               @RequestParam String tenantId) {
        try {
            String token = extractTokenFromAuthorization(authorization);
            boolean isValid = authenticationService.validateToken(token, tenantId);
            return ResponseEntity.ok(ApiResponse.success(isValid, isValid ? "令牌有效" : "令牌无效"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(401, "令牌验证失败: " + e.getMessage()));
        }
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

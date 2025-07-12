package com.aixone.auth.controller;

import com.aixone.auth.common.ApiResponse;
import com.aixone.auth.common.ErrorCode;
import com.aixone.auth.service.AuthService;
import com.aixone.auth.service.VerificationCodeService;
import com.aixone.auth.service.TokenBlacklistService;
import com.aixone.auth.ratelimit.RateLimitService;
import com.aixone.auth.service.OAuth2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;
import org.springframework.http.HttpHeaders;
import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.aixone.auth.service.PermissionServiceAdapter;
import com.aixone.permission.model.Role;
import com.aixone.permission.model.Permission;
import java.util.HashMap;
import java.util.List;

/**
 * 认证相关API
 * 包含注册、登录、Token刷新、验证码、第三方登录等接口
 */
@Tag(name = "认证相关API", description = "注册、登录、Token、验证码、第三方登录等")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    private RateLimitService rateLimitService;

    @Autowired
    private OAuth2Service oAuth2Service;

    @Autowired
    private PermissionServiceAdapter permissionServiceAdapter;

    /** 用户名密码登录 */
    @Operation(summary = "用户名密码登录")
    @PostMapping("/login")
    public ApiResponse<?> login(@RequestParam String username, @RequestParam String password) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return ApiResponse.error(ErrorCode.INVALID_PARAM, "用户名和密码不能为空");
        }
        // 限流：每分钟最多5次
        if (!rateLimitService.tryAcquire("login:" + username, 5)) {
            return ApiResponse.error(ErrorCode.FORBIDDEN, "登录操作过于频繁，请稍后再试");
        }
        return ApiResponse.success(authService.login(username, password));
    }

    /** 用户注册 */
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public ApiResponse<?> register(@RequestBody Object registerRequest) {
        // 假设registerRequest为Map<String, Object>，实际可用DTO替换
        String key = null;
        if (registerRequest instanceof java.util.Map) {
            java.util.Map req = (java.util.Map) registerRequest;
            key = (String) req.get("username");
            if (key == null) key = (String) req.get("email");
            if (key == null) key = (String) req.get("phone");
        }
        if (key != null && !rateLimitService.tryAcquire("register:" + key, 3)) {
            return ApiResponse.error(ErrorCode.FORBIDDEN, "注册操作过于频繁，请稍后再试");
        }
        // TODO: 可加参数校验
        return ApiResponse.success(authService.register(registerRequest));
    }

    /** 刷新Token */
    @Operation(summary = "刷新Token")
    @PostMapping("/refresh")
    public ApiResponse<?> refresh(@RequestParam String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            return ApiResponse.error(ErrorCode.INVALID_PARAM, "refreshToken不能为空");
        }
        return ApiResponse.success(authService.refreshToken(refreshToken));
    }

    /** 发送短信验证码 */
    @Operation(summary = "发送短信验证码")
    @PostMapping("/sms/send")
    public ApiResponse<?> sendSmsCode(@RequestParam String phone) {
        if (!StringUtils.hasText(phone)) {
            return ApiResponse.error(ErrorCode.INVALID_PARAM, "手机号不能为空");
        }
        // 限流：每分钟最多3次
        if (!rateLimitService.tryAcquire(phone, 3)) {
            return ApiResponse.error(ErrorCode.FORBIDDEN, "操作过于频繁，请稍后再试");
        }
        String code = verificationCodeService.generateCode(phone);
        verificationCodeService.sendSmsCode(phone, code);
        return ApiResponse.success("短信验证码发送成功");
    }

    /** 短信验证码登录 */
    @Operation(summary = "短信验证码登录")
    @PostMapping("/sms/login")
    public ApiResponse<?> smsLogin(@RequestParam String phone, @RequestParam String smsCode) {
        if (!StringUtils.hasText(phone) || !StringUtils.hasText(smsCode)) {
            return ApiResponse.error(ErrorCode.INVALID_PARAM, "手机号和验证码不能为空");
        }
        boolean valid = verificationCodeService.validateCode(phone, smsCode);
        if (!valid) {
            return ApiResponse.error(ErrorCode.INVALID_PARAM, "验证码错误或已过期");
        }
        // 验证码通过后，调用登录逻辑（可根据实际业务扩展）
        return ApiResponse.success(authService.login(phone, null));
    }

    /** 发送邮箱验证码 */
    @Operation(summary = "发送邮箱验证码")
    @PostMapping("/email/send")
    public ApiResponse<?> sendEmailCode(@RequestParam String email) {
        if (!StringUtils.hasText(email)) {
            return ApiResponse.error(ErrorCode.INVALID_PARAM, "邮箱不能为空");
        }
        // 限流：每分钟最多3次
        if (!rateLimitService.tryAcquire(email, 3)) {
            return ApiResponse.error(ErrorCode.FORBIDDEN, "操作过于频繁，请稍后再试");
        }
        String code = verificationCodeService.generateCode(email);
        verificationCodeService.sendEmailCode(email, code);
        return ApiResponse.success("邮箱验证码发送成功");
    }

    /** 邮箱验证码登录 */
    @Operation(summary = "邮箱验证码登录")
    @PostMapping("/email/login")
    public ApiResponse<?> emailLogin(@RequestParam String email, @RequestParam String emailCode) {
        if (!StringUtils.hasText(email) || !StringUtils.hasText(emailCode)) {
            return ApiResponse.error(ErrorCode.INVALID_PARAM, "邮箱和验证码不能为空");
        }
        boolean valid = verificationCodeService.validateCode(email, emailCode);
        if (!valid) {
            return ApiResponse.error(ErrorCode.INVALID_PARAM, "验证码错误或已过期");
        }
        // 验证码通过后，调用登录逻辑（可根据实际业务扩展）
        return ApiResponse.success(authService.login(email, null));
    }

    /** 支付宝OAuth2授权 */
    @Operation(summary = "支付宝OAuth2授权")
    @GetMapping("/alipay/authorize")
    public ApiResponse<?> alipayAuthorize() {
        // TODO: 生成支付宝授权URL，重定向或返回给前端
        String authorizeUrl = "https://openauth.alipay.com/oauth2/publicAppAuthorize.htm?app_id=your_app_id&scope=auth_user&redirect_uri=your_callback_url";
        return ApiResponse.success(authorizeUrl);
    }

    /** 支付宝OAuth2回调 */
    @Operation(summary = "支付宝OAuth2回调")
    @GetMapping("/alipay/callback")
    public ApiResponse<?> alipayCallback(@RequestParam String code) {
        // 用code换取access_token，获取支付宝用户信息
        String alipayUserId = oAuth2Service.getAlipayUserId(code);
        return ApiResponse.success(authService.login(alipayUserId, null));
    }

    /** 微信OAuth2授权 */
    @Operation(summary = "微信OAuth2授权")
    @GetMapping("/wechat/authorize")
    public ApiResponse<?> wechatAuthorize() {
        // TODO: 生成微信授权URL，重定向或返回给前端
        String authorizeUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=your_app_id&redirect_uri=your_callback_url&response_type=code&scope=snsapi_userinfo";
        return ApiResponse.success(authorizeUrl);
    }

    /** 微信OAuth2回调 */
    @Operation(summary = "微信OAuth2回调")
    @GetMapping("/wechat/callback")
    public ApiResponse<?> wechatCallback(@RequestParam String code) {
        String wechatUserId = oAuth2Service.getWechatUserId(code);
        return ApiResponse.success(authService.login(wechatUserId, null));
    }

    /** 阿里云OAuth2授权 */
    @Operation(summary = "阿里云OAuth2授权")
    @GetMapping("/aliyun/authorize")
    public ApiResponse<?> aliyunAuthorize() {
        // TODO: 生成阿里云授权URL，重定向或返回给前端
        String authorizeUrl = "https://oauth.aliyun.com/authorize?app_id=your_app_id&redirect_uri=your_callback_url&response_type=code&scope=user_info";
        return ApiResponse.success(authorizeUrl);
    }

    /** 阿里云OAuth2回调 */
    @Operation(summary = "阿里云OAuth2回调")
    @GetMapping("/aliyun/callback")
    public ApiResponse<?> aliyunCallback(@RequestParam String code) {
        String aliyunUserId = oAuth2Service.getAliyunUserId(code);
        return ApiResponse.success(authService.login(aliyunUserId, null));
    }

    /** 用户登出 */
    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public ApiResponse<?> logout(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ApiResponse.error(ErrorCode.UNAUTHORIZED, "未携带Token");
        }
        String token = authHeader.substring(7);
        tokenBlacklistService.addToBlacklist(token);
        return ApiResponse.success("登出成功，Token已失效");
    }

    /** 权限校验示例接口 */
    @Operation(summary = "权限校验示例")
    @GetMapping("/check-access")
    public ApiResponse<?> checkAccess(@RequestParam String userId, @RequestParam String resource, @RequestParam String action) {
        boolean allowed = permissionServiceAdapter.checkAccess(userId, resource, action, new HashMap<>());
        return allowed ? ApiResponse.success("有权限") : ApiResponse.error(ErrorCode.FORBIDDEN, "无权限");
    }

    /** 获取用户所有角色 */
    @Operation(summary = "获取用户所有角色")
    @GetMapping("/user-roles")
    public ApiResponse<?> getUserRoles(@RequestParam String userId) {
        List<Role> roles = permissionServiceAdapter.getUserRoles(userId);
        return ApiResponse.success(roles);
    }

    /** 获取角色所有权限 */
    @Operation(summary = "获取角色所有权限")
    @GetMapping("/role-permissions")
    public ApiResponse<?> getRolePermissions(@RequestParam String roleId) {
        List<Permission> perms = permissionServiceAdapter.getRolePermissions(roleId);
        return ApiResponse.success(perms);
    }
} 
package com.aixone.tech.auth.authentication.interfaces.rest;

import com.aixone.tech.auth.authentication.application.command.LoginCommand;
import com.aixone.tech.auth.authentication.application.dto.auth.TokenResponse;
import com.aixone.tech.auth.authentication.application.service.AuthenticationApplicationService;
import com.aixone.tech.auth.authentication.domain.model.User;
import com.aixone.tech.auth.authentication.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 用户相关接口控制器
 * 提供前端需要的用户登录和配置接口
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private AuthenticationApplicationService authenticationService;

    @Autowired
    private UserRepository userRepository;

    /**
     * 用户登录接口 (checkIn POST)
     * 支持用户名密码登录
     */
    @PostMapping("/checkIn")
    public ResponseEntity<Map<String, Object>> checkIn(@RequestBody Map<String, Object> request) {
        try {
            // 提取请求参数
            String username = (String) request.get("username");
            String password = (String) request.get("password");
            String email = (String) request.get("email");
            String mobile = (String) request.get("mobile");
            String tenantId = (String) request.getOrDefault("tenantId", "default");
            String clientId = (String) request.getOrDefault("clientId", "default-client");
            String clientSecret = (String) request.getOrDefault("clientSecret", "default-secret");

            // 构建登录命令
            LoginCommand loginCommand = new LoginCommand();
            loginCommand.setTenantId(tenantId);
            loginCommand.setClientId(clientId);
            loginCommand.setClientSecret(clientSecret);

            // 根据提供的字段确定用户名
            if (username != null && !username.isEmpty()) {
                loginCommand.setUsername(username);
            } else if (email != null && !email.isEmpty()) {
                loginCommand.setUsername(email);
            } else if (mobile != null && !mobile.isEmpty()) {
                loginCommand.setUsername(mobile);
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "code", 0,
                    "msg", "用户名、邮箱或手机号必须提供其中一个"
                ));
            }

            loginCommand.setPassword(password);

            // 执行登录
            TokenResponse tokenResponse = authenticationService.login(loginCommand);

            // 获取用户信息
            Optional<User> userOpt = userRepository.findByUsernameAndTenantId(loginCommand.getUsername(), tenantId);
            User user = userOpt.orElse(null);

            // 构建用户信息
            Map<String, Object> userInfo = new HashMap<>();
            if (user != null) {
                userInfo.put("id", user.getId().hashCode()); // 前端期望数字类型
                userInfo.put("username", user.getUsername());
                userInfo.put("nickname", user.getUsername()); // 使用username作为nickname
                userInfo.put("email", user.getEmail() != null ? user.getEmail() : "");
                userInfo.put("mobile", ""); // 默认为空
                userInfo.put("avatar", user.getAvatarUrl() != null ? user.getAvatarUrl() : "");
                userInfo.put("gender", 0); // 默认为0
                userInfo.put("birthday", ""); // 默认为空
                userInfo.put("money", 0); // 默认为0
                userInfo.put("score", 0); // 默认为0
                userInfo.put("last_login_time", ""); // 默认为空
                userInfo.put("last_login_ip", ""); // 默认为空
                userInfo.put("join_time", ""); // 默认为空
                userInfo.put("motto", ""); // 默认为空
                userInfo.put("token", tokenResponse.getAccessToken()); // 添加token
                userInfo.put("refresh_token", tokenResponse.getRefreshToken()); // 添加refresh_token
            }

            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("code", 1);
            response.put("msg", "登录成功");
            response.put("data", Map.of(
                "accessToken", tokenResponse.getAccessToken(),
                "refreshToken", tokenResponse.getRefreshToken(),
                "expiresIn", tokenResponse.getExpiresIn(),
                "tokenType", tokenResponse.getTokenType(),
                "userInfo", userInfo,
                "routePath", "/admin"
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 0);
            response.put("msg", "登录失败: " + e.getMessage());
            response.put("data", Map.of());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 获取登录页面配置 (checkIn GET)
     * 返回登录页面需要的配置信息
     */
    @GetMapping("/checkIn")
    public ResponseEntity<Map<String, Object>> getLoginConfig() {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 1);
        response.put("msg", "获取配置成功");
        response.put("data", Map.of(
            "userLoginCaptchaSwitch", false,  // 验证码开关
            "accountVerificationType", new String[]{"email", "mobile"}  // 支持的验证类型
        ));
        return ResponseEntity.ok(response);
    }

    /**
     * 用户登出接口
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestBody Map<String, Object> request) {
        try {
            String refreshToken = (String) request.get("refreshToken");
            String tenantId = (String) request.getOrDefault("tenantId", "default");
            if (refreshToken != null && !refreshToken.isEmpty()) {
                authenticationService.logout(refreshToken, tenantId);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("code", 1);
            response.put("msg", "登出成功");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 0);
            response.put("msg", "登出失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}

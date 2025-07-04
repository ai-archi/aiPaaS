package com.aixone.auth.service;

import com.aixone.auth.user.UserRepository;
import com.aixone.auth.tenant.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.aixone.auth.user.User;
import com.aixone.auth.util.PasswordUtil;
import com.aixone.auth.util.JwtUtil;
import java.util.Optional;
import com.aixone.auth.tenant.Tenant;
import java.util.Map;
import com.aixone.auth.service.RefreshTokenService;
import java.util.HashMap;

/**
 * 认证业务服务实现
 */
@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TenantRepository tenantRepository;
    @Autowired
    private RefreshTokenService refreshTokenService;
    private static final long REFRESH_TOKEN_EXPIRE_SECONDS = 7 * 24 * 3600; // 7天
    // 可注入更多依赖，如验证码服务、Token工具类等

    @Override
    public Object register(Object registerRequest) {
        // 假设registerRequest为Map<String, Object>，实际可用DTO替换
        Map<String, Object> req = (Map<String, Object>) registerRequest;
        String tenantId = (String) req.get("tenantId");
        String username = (String) req.get("username");
        String password = (String) req.get("password");
        if (tenantId == null || username == null || password == null) {
            throw new RuntimeException("租户ID、用户名、密码不能为空");
        }
        // 租户注册开关校验
        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow(() -> new RuntimeException("租户不存在"));
        if (!Boolean.TRUE.equals(tenant.getAllowRegister())) {
            throw new RuntimeException("该租户不允许注册");
        }
        // 用户唯一性校验
        boolean exists = userRepository.findAll().stream().anyMatch(u -> username.equals(u.getUsername()));
        if (exists) {
            throw new RuntimeException("用户名已存在");
        }
        // 密码加密
        String encodedPwd = PasswordUtil.encode(password);
        // 创建用户
        User user = new User();
        user.setUserId(java.util.UUID.randomUUID().toString());
        user.setTenantId(tenantId);
        user.setUsername(username);
        user.setPassword(encodedPwd);
        user.setStatus("ENABLED");
        user.setEmail((String) req.get("email"));
        user.setPhone((String) req.get("phone"));
        userRepository.save(user);
        // 可扩展：分配默认角色、发送欢迎邮件等
        String accessToken = JwtUtil.generateToken(user.getUserId(), "default-client");
        String refreshToken = refreshTokenService.createAndStore(user.getUserId(), "default-client", REFRESH_TOKEN_EXPIRE_SECONDS);
        HashMap<String, String> result = new HashMap<>();
        result.put("accessToken", accessToken);
        result.put("refreshToken", refreshToken);
        return result;
    }

    @Override
    public Object login(String usernameOrPhoneOrEmail, String password) {
        // 支持用户名/手机号/邮箱登录
        Optional<User> userOpt = userRepository.findAll().stream()
                .filter(u -> usernameOrPhoneOrEmail.equals(u.getUsername())
                        || usernameOrPhoneOrEmail.equals(u.getPhone())
                        || usernameOrPhoneOrEmail.equals(u.getEmail()))
                .findFirst();
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        User user = userOpt.get();
        // 如果是密码登录
        if (password != null) {
            if (!PasswordUtil.matches(password, user.getPassword())) {
                throw new RuntimeException("密码错误");
            }
        }
        // 如果是验证码登录，已在Controller层校验验证码，这里无需校验密码
        // Token生成
        String accessToken = JwtUtil.generateToken(user.getUserId(), "default-client");
        String refreshToken = refreshTokenService.createAndStore(user.getUserId(), "default-client", REFRESH_TOKEN_EXPIRE_SECONDS);
        HashMap<String, String> result = new HashMap<>();
        result.put("accessToken", accessToken);
        result.put("refreshToken", refreshToken);
        return result;
    }

    @Override
    public Object refreshToken(String refreshToken) {
        // 校验refreshToken
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new RuntimeException("refreshToken不能为空");
        }
        if (!refreshTokenService.validate(refreshToken)) {
            throw new RuntimeException("refreshToken无效或已过期");
        }
        String userId = refreshTokenService.getUserId(refreshToken);
        String clientId = "default-client";
        String newAccessToken = JwtUtil.generateToken(userId, clientId);
        // 可选：续签新的refreshToken
        String newRefreshToken = refreshTokenService.createAndStore(userId, clientId, REFRESH_TOKEN_EXPIRE_SECONDS);
        // 旧refreshToken失效
        refreshTokenService.delete(refreshToken);
        HashMap<String, String> result = new HashMap<>();
        result.put("accessToken", newAccessToken);
        result.put("refreshToken", newRefreshToken);
        return result;
    }
} 
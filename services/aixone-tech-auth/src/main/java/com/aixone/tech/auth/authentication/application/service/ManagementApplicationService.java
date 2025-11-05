package com.aixone.tech.auth.authentication.application.service;

import com.aixone.tech.auth.authentication.application.dto.management.*;
import com.aixone.tech.auth.authentication.domain.model.Token;
import com.aixone.tech.auth.authentication.domain.model.User;
import com.aixone.tech.auth.authentication.domain.repository.TokenRepository;
import com.aixone.tech.auth.authentication.domain.repository.UserRepository;
import com.aixone.tech.auth.authentication.infrastructure.persistence.repository.JpaUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 管理接口应用服务
 */
@Service
@Transactional
public class ManagementApplicationService {
    
    private final UserRepository userRepository;
    private final JpaUserRepository jpaUserRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    
    public ManagementApplicationService(UserRepository userRepository,
                                       JpaUserRepository jpaUserRepository,
                                       TokenRepository tokenRepository,
                                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jpaUserRepository = jpaUserRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * 获取已登录用户列表
     */
    @Transactional(readOnly = true)
    public List<ActiveUserResponse> getActiveUsers(String tenantId) {
        // 获取所有有有效Token的用户
        List<Token> allTokens = tokenRepository.findByTenantId(tenantId);
        List<Token> activeTokens = allTokens.stream()
            .filter(token -> !token.isExpired() && token.isAccessToken())
            .collect(Collectors.toList());
        
        return activeTokens.stream()
            .map(token -> {
                Optional<User> userOpt = userRepository.findById(UUID.fromString(token.getUserId()));
                if (userOpt.isEmpty()) {
                    return null;
                }
                User user = userOpt.get();
                
                // 获取用户的所有设备
                List<Token> userTokens = tokenRepository.findByUserIdAndTenantId(token.getUserId(), tenantId);
                List<ActiveUserResponse.DeviceInfo> devices = userTokens.stream()
                    .filter(t -> !t.isExpired() && t.isAccessToken())
                    .map(t -> new ActiveUserResponse.DeviceInfo(
                        t.getToken().substring(0, Math.min(20, t.getToken().length())), // deviceId用token前20位
                        t.getClientId(),
                        null, // clientIp需要从其他地方获取
                        null, // userAgent需要从其他地方获取
                        t.getCreatedAt(),
                        t.getExpiresAt()
                    ))
                    .collect(Collectors.toList());
                
                return new ActiveUserResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getTenantId(),
                    devices.size(),
                    userTokens.stream()
                        .map(Token::getCreatedAt)
                        .max(LocalDateTime::compareTo)
                        .orElse(null),
                    devices
                );
            })
            .filter(response -> response != null)
            .collect(Collectors.toList());
    }
    
    /**
     * 获取用户的所有登录设备
     */
    @Transactional(readOnly = true)
    public List<ActiveUserResponse.DeviceInfo> getUserDevices(String userId, String tenantId) {
        List<Token> tokens = tokenRepository.findByUserIdAndTenantId(userId, tenantId);
        return tokens.stream()
            .filter(t -> !t.isExpired() && t.isAccessToken())
            .map(t -> new ActiveUserResponse.DeviceInfo(
                t.getToken().substring(0, Math.min(20, t.getToken().length())),
                t.getClientId(),
                null,
                null,
                t.getCreatedAt(),
                t.getExpiresAt()
            ))
            .collect(Collectors.toList());
    }
    
    /**
     * 创建认证用户
     */
    public UserResponse createUser(UserCreateRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsernameAndTenantId(request.getUsername(), request.getTenantId())) {
            throw new IllegalArgumentException("用户名已存在");
        }
        
        // 创建用户
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User user = new User(
            request.getUsername(),
            hashedPassword,
            request.getEmail(),
            request.getTenantId()
        );
        user.setPhone(request.getPhone());
        
        User savedUser = userRepository.save(user);
        return toUserResponse(savedUser);
    }
    
    /**
     * 获取认证用户
     */
    @Transactional(readOnly = true)
    public Optional<UserResponse> getUserById(UUID userId) {
        return userRepository.findById(userId)
            .map(this::toUserResponse);
    }
    
    /**
     * 获取认证用户列表
     */
    @Transactional(readOnly = true)
    public Page<UserResponse> getUsers(String tenantId, Pageable pageable) {
        // 这里需要实现分页查询，暂时返回所有用户
        List<User> users = jpaUserRepository.findByTenantId(tenantId);
        List<UserResponse> responses = users.stream()
            .map(this::toUserResponse)
            .collect(Collectors.toList());
        return new PageImpl<>(responses, pageable, responses.size());
    }
    
    /**
     * 更新认证用户
     */
    public UserResponse updateUser(UUID userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        user.setUpdatedAt(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        return toUserResponse(savedUser);
    }
    
    /**
     * 更新用户密码
     */
    public void updateUserPassword(UUID userId, String newPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        
        String hashedPassword = passwordEncoder.encode(newPassword);
        user.setHashedPassword(hashedPassword);
        user.setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(user);
    }
    
    /**
     * 重置用户密码
     */
    public void resetUserPassword(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        
        // 生成默认密码（实际应该通过邮件等方式发送）
        String defaultPassword = "TempPassword123!";
        String hashedPassword = passwordEncoder.encode(defaultPassword);
        user.setHashedPassword(hashedPassword);
        user.setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(user);
    }
    
    /**
     * 删除认证用户
     */
    public void deleteUser(UUID userId) {
        // 先查找用户，获取租户ID
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // 删除用户的所有Token
            tokenRepository.deleteByUserIdAndTenantId(userId.toString(), user.getTenantId());
        }
        // 删除用户
        userRepository.deleteById(userId);
    }
    
    /**
     * 获取Token列表
     */
    @Transactional(readOnly = true)
    public List<TokenInfoResponse> getTokens(String tenantId) {
        List<Token> tokens = tokenRepository.findByTenantId(tenantId);
        
        return tokens.stream()
            .map(this::toTokenInfoResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * 撤销Token
     */
    public void revokeToken(String token, String tenantId) {
        Optional<Token> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isEmpty()) {
            throw new IllegalArgumentException("Token不存在");
        }
        
        Token tokenEntity = tokenOpt.get();
        if (!tokenEntity.belongsToTenant(tenantId)) {
            throw new IllegalArgumentException("Token不属于指定租户");
        }
        
        tokenRepository.delete(token);
    }
    
    /**
     * 撤销用户的所有Token
     */
    public void revokeUserTokens(String userId, String tenantId) {
        tokenRepository.deleteByUserIdAndTenantId(userId, tenantId);
    }
    
    /**
     * 用户登出所有设备
     */
    public void logoutAllDevices(String userId, String tenantId) {
        revokeUserTokens(userId, tenantId);
    }
    
    /**
     * 用户登出指定设备
     */
    public void logoutDevice(String userId, String deviceId, String tenantId) {
        // deviceId是token的前20位，需要找到对应的token
        List<Token> tokens = tokenRepository.findByUserIdAndTenantId(userId, tenantId);
        Token targetToken = tokens.stream()
            .filter(t -> t.getToken().startsWith(deviceId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("设备不存在"));
        
        tokenRepository.delete(targetToken.getToken());
    }
    
    private UserResponse toUserResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getPhone(),
            user.getStatus(),
            user.getTenantId(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
    
    private TokenInfoResponse toTokenInfoResponse(Token token) {
        return new TokenInfoResponse(
            token.getToken(),
            token.getUserId(),
            token.getClientId(),
            token.getTenantId(),
            token.getType().toString(),
            token.getExpiresAt(),
            token.getCreatedAt()
        );
    }
}


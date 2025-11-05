package com.aixone.tech.auth.authentication.interfaces.rest;

import com.aixone.tech.auth.authentication.application.dto.management.*;
import com.aixone.tech.auth.authentication.application.service.ManagementApplicationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 管理接口Controller（内部接口，不直接对外暴露）
 */
@RestController
@RequestMapping("/auth/internal")
public class ManagementController {
    
    private final ManagementApplicationService managementService;
    
    public ManagementController(ManagementApplicationService managementService) {
        this.managementService = managementService;
    }
    
    // ========== 已登录用户管理 ==========
    
    /**
     * 获取已登录用户列表
     */
    @PreAuthorize("hasAuthority('auth:user:read')")
    @GetMapping("/users/active")
    public ResponseEntity<Map<String, Object>> getActiveUsers(@RequestParam String tenantId) {
        List<ActiveUserResponse> users = managementService.getActiveUsers(tenantId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "success");
        response.put("data", users);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取用户的所有登录设备
     */
    @PreAuthorize("hasAuthority('auth:device:read')")
    @GetMapping("/users/{userId}/devices")
    public ResponseEntity<Map<String, Object>> getUserDevices(
            @PathVariable String userId,
            @RequestParam String tenantId) {
        List<ActiveUserResponse.DeviceInfo> devices = managementService.getUserDevices(userId, tenantId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "success");
        response.put("data", devices);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 用户登出所有设备
     */
    @PreAuthorize("hasAuthority('auth:device:logout')")
    @PostMapping("/users/{userId}/logout-all")
    public ResponseEntity<Map<String, Object>> logoutAllDevices(
            @PathVariable String userId,
            @RequestParam String tenantId) {
        managementService.logoutAllDevices(userId, tenantId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "success");
        response.put("data", Map.of());
        return ResponseEntity.ok(response);
    }
    
    /**
     * 用户登出指定设备
     */
    @PreAuthorize("hasAuthority('auth:device:logout')")
    @PostMapping("/users/{userId}/devices/{deviceId}/logout")
    public ResponseEntity<Map<String, Object>> logoutDevice(
            @PathVariable String userId,
            @PathVariable String deviceId,
            @RequestParam String tenantId) {
        managementService.logoutDevice(userId, deviceId, tenantId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "success");
        response.put("data", Map.of());
        return ResponseEntity.ok(response);
    }
    
    // ========== 认证用户管理 ==========
    
    /**
     * 创建认证用户
     */
    @PreAuthorize("hasAuthority('auth:user:create')")
    @PostMapping("/users")
    public ResponseEntity<Map<String, Object>> createUser(
            @Valid @RequestBody UserCreateRequest request) {
        UserResponse user = managementService.createUser(request);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "success");
        response.put("data", user);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取认证用户
     */
    @PreAuthorize("hasAuthority('auth:user:read')")
    @GetMapping("/users/{userId}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable UUID userId) {
        UserResponse user = managementService.getUserById(userId)
            .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "success");
        response.put("data", user);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取认证用户列表
     */
    @PreAuthorize("hasAuthority('auth:user:read')")
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getUsers(
            @RequestParam String tenantId,
            Pageable pageable) {
        Page<UserResponse> users = managementService.getUsers(tenantId, pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "success");
        response.put("data", users);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 更新认证用户
     */
    @PreAuthorize("hasAuthority('auth:user:update')")
    @PutMapping("/users/{userId}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UserUpdateRequest request) {
        UserResponse user = managementService.updateUser(userId, request);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "success");
        response.put("data", user);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 更新用户密码
     */
    @PreAuthorize("hasAuthority('auth:user:update')")
    @PutMapping("/users/{userId}/password")
    public ResponseEntity<Map<String, Object>> updateUserPassword(
            @PathVariable UUID userId,
            @RequestBody Map<String, String> request) {
        String newPassword = request.get("newPassword");
        if (newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("新密码不能为空");
        }
        managementService.updateUserPassword(userId, newPassword);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "success");
        response.put("data", Map.of());
        return ResponseEntity.ok(response);
    }
    
    /**
     * 重置用户密码
     */
    @PreAuthorize("hasAuthority('auth:user:password:reset')")
    @PostMapping("/users/{userId}/password/reset")
    public ResponseEntity<Map<String, Object>> resetUserPassword(@PathVariable UUID userId) {
        managementService.resetUserPassword(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "success");
        response.put("data", Map.of());
        return ResponseEntity.ok(response);
    }
    
    /**
     * 删除认证用户
     */
    @PreAuthorize("hasAuthority('auth:user:delete')")
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable UUID userId) {
        managementService.deleteUser(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "success");
        response.put("data", Map.of());
        return ResponseEntity.ok(response);
    }
    
    // ========== Token管理 ==========
    
    /**
     * 获取Token列表
     */
    @PreAuthorize("hasAuthority('auth:token:read')")
    @GetMapping("/tokens")
    public ResponseEntity<Map<String, Object>> getTokens(@RequestParam String tenantId) {
        List<TokenInfoResponse> tokens = managementService.getTokens(tenantId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "success");
        response.put("data", tokens);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 撤销Token
     */
    @PreAuthorize("hasAuthority('auth:token:revoke')")
    @PostMapping("/tokens/{token}/revoke")
    public ResponseEntity<Map<String, Object>> revokeToken(
            @PathVariable String token,
            @RequestParam String tenantId) {
        managementService.revokeToken(token, tenantId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "success");
        response.put("data", Map.of());
        return ResponseEntity.ok(response);
    }
    
    /**
     * 撤销用户的所有Token
     */
    @PreAuthorize("hasAuthority('auth:token:revoke')")
    @PostMapping("/users/{userId}/tokens/revoke-all")
    public ResponseEntity<Map<String, Object>> revokeUserTokens(
            @PathVariable String userId,
            @RequestParam String tenantId) {
        managementService.revokeUserTokens(userId, tenantId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "success");
        response.put("data", Map.of());
        return ResponseEntity.ok(response);
    }
}


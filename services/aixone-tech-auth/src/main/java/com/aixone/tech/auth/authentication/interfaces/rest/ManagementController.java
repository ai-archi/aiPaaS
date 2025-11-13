package com.aixone.tech.auth.authentication.interfaces.rest;

import com.aixone.common.api.ApiResponse;
import com.aixone.tech.auth.authentication.application.dto.management.*;
import com.aixone.tech.auth.authentication.application.service.ManagementApplicationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
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
    public ResponseEntity<ApiResponse<List<ActiveUserResponse>>> getActiveUsers(@RequestParam String tenantId) {
        try {
            List<ActiveUserResponse> users = managementService.getActiveUsers(tenantId);
            return ResponseEntity.ok(ApiResponse.success(users, "获取已登录用户列表成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(500, "获取已登录用户列表失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取用户的所有登录设备
     */
    @PreAuthorize("hasAuthority('auth:device:read')")
    @GetMapping("/users/{userId}/devices")
    public ResponseEntity<ApiResponse<List<ActiveUserResponse.DeviceInfo>>> getUserDevices(
            @PathVariable String userId,
            @RequestParam String tenantId) {
        try {
            List<ActiveUserResponse.DeviceInfo> devices = managementService.getUserDevices(userId, tenantId);
            return ResponseEntity.ok(ApiResponse.success(devices, "获取用户设备列表成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(500, "获取用户设备列表失败: " + e.getMessage()));
        }
    }
    
    /**
     * 用户登出所有设备
     */
    @PreAuthorize("hasAuthority('auth:device:logout')")
    @PostMapping("/users/{userId}/logout-all")
    public ResponseEntity<ApiResponse<Void>> logoutAllDevices(
            @PathVariable String userId,
            @RequestParam String tenantId) {
        try {
            managementService.logoutAllDevices(userId, tenantId);
            return ResponseEntity.ok(ApiResponse.success(null, "登出所有设备成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(500, "登出所有设备失败: " + e.getMessage()));
        }
    }
    
    /**
     * 用户登出指定设备
     */
    @PreAuthorize("hasAuthority('auth:device:logout')")
    @PostMapping("/users/{userId}/devices/{deviceId}/logout")
    public ResponseEntity<ApiResponse<Void>> logoutDevice(
            @PathVariable String userId,
            @PathVariable String deviceId,
            @RequestParam String tenantId) {
        try {
            managementService.logoutDevice(userId, deviceId, tenantId);
            return ResponseEntity.ok(ApiResponse.success(null, "登出设备成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(500, "登出设备失败: " + e.getMessage()));
        }
    }
    
    // ========== 认证用户管理 ==========
    
    /**
     * 创建认证用户
     */
    @PreAuthorize("hasAuthority('auth:user:create')")
    @PostMapping("/users")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody UserCreateRequest request) {
        try {
            UserResponse user = managementService.createUser(request);
            return ResponseEntity.ok(ApiResponse.success(user, "创建用户成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(500, "创建用户失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取认证用户
     */
    @PreAuthorize("hasAuthority('auth:user:read')")
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable UUID userId) {
        try {
            UserResponse user = managementService.getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
            return ResponseEntity.ok(ApiResponse.success(user, "获取用户成功"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.notFound(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(500, "获取用户失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取认证用户列表
     */
    @PreAuthorize("hasAuthority('auth:user:read')")
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getUsers(
            @RequestParam String tenantId,
            Pageable pageable) {
        try {
            Page<UserResponse> users = managementService.getUsers(tenantId, pageable);
            return ResponseEntity.ok(ApiResponse.success(users, "获取用户列表成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(500, "获取用户列表失败: " + e.getMessage()));
        }
    }
    
    /**
     * 更新认证用户
     */
    @PreAuthorize("hasAuthority('auth:user:update')")
    @PutMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UserUpdateRequest request) {
        try {
            UserResponse user = managementService.updateUser(userId, request);
            return ResponseEntity.ok(ApiResponse.success(user, "更新用户成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(500, "更新用户失败: " + e.getMessage()));
        }
    }
    
    /**
     * 更新用户密码
     */
    @PreAuthorize("hasAuthority('auth:user:update')")
    @PutMapping("/users/{userId}/password")
    public ResponseEntity<ApiResponse<Void>> updateUserPassword(
            @PathVariable UUID userId,
            @RequestBody java.util.Map<String, String> request) {
        try {
            String newPassword = request.get("newPassword");
            if (newPassword == null || newPassword.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.badRequest("新密码不能为空"));
            }
            managementService.updateUserPassword(userId, newPassword);
            return ResponseEntity.ok(ApiResponse.success(null, "更新密码成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(500, "更新密码失败: " + e.getMessage()));
        }
    }
    
    /**
     * 重置用户密码
     */
    @PreAuthorize("hasAuthority('auth:user:password:reset')")
    @PostMapping("/users/{userId}/password/reset")
    public ResponseEntity<ApiResponse<Void>> resetUserPassword(@PathVariable UUID userId) {
        try {
            managementService.resetUserPassword(userId);
            return ResponseEntity.ok(ApiResponse.success(null, "重置密码成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(500, "重置密码失败: " + e.getMessage()));
        }
    }
    
    /**
     * 删除认证用户
     */
    @PreAuthorize("hasAuthority('auth:user:delete')")
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID userId) {
        try {
            managementService.deleteUser(userId);
            return ResponseEntity.ok(ApiResponse.success(null, "删除用户成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(500, "删除用户失败: " + e.getMessage()));
        }
    }
    
    // ========== Token管理 ==========
    
    /**
     * 获取Token列表
     */
    @PreAuthorize("hasAuthority('auth:token:read')")
    @GetMapping("/tokens")
    public ResponseEntity<ApiResponse<List<TokenInfoResponse>>> getTokens(@RequestParam String tenantId) {
        try {
            List<TokenInfoResponse> tokens = managementService.getTokens(tenantId);
            return ResponseEntity.ok(ApiResponse.success(tokens, "获取Token列表成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(500, "获取Token列表失败: " + e.getMessage()));
        }
    }
    
    /**
     * 撤销Token
     */
    @PreAuthorize("hasAuthority('auth:token:revoke')")
    @PostMapping("/tokens/{token}/revoke")
    public ResponseEntity<ApiResponse<Void>> revokeToken(
            @PathVariable String token,
            @RequestParam String tenantId) {
        try {
            managementService.revokeToken(token, tenantId);
            return ResponseEntity.ok(ApiResponse.success(null, "撤销Token成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(500, "撤销Token失败: " + e.getMessage()));
        }
    }
    
    /**
     * 撤销用户的所有Token
     */
    @PreAuthorize("hasAuthority('auth:token:revoke')")
    @PostMapping("/users/{userId}/tokens/revoke-all")
    public ResponseEntity<ApiResponse<Void>> revokeUserTokens(
            @PathVariable String userId,
            @RequestParam String tenantId) {
        try {
            managementService.revokeUserTokens(userId, tenantId);
            return ResponseEntity.ok(ApiResponse.success(null, "撤销用户所有Token成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(500, "撤销用户所有Token失败: " + e.getMessage()));
        }
    }
}


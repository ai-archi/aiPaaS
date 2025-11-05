package com.aixone.tech.auth.authentication.application.dto.management;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 已登录用户响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActiveUserResponse {
    private UUID userId;
    private String username;
    private String email;
    private String phone;
    private String tenantId;
    private Integer activeDeviceCount;
    private LocalDateTime lastLoginTime;
    private List<DeviceInfo> devices;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeviceInfo {
        private String deviceId;
        private String clientId;
        private String clientIp;
        private String userAgent;
        private LocalDateTime loginTime;
        private LocalDateTime expiresAt;
    }
}


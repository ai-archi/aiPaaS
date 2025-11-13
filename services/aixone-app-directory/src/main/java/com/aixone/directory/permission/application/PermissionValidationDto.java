package com.aixone.directory.permission.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 权限校验相关 DTO
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
public final class PermissionValidationDto {
    private PermissionValidationDto() {}

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckPermissionRequest {
        private String userId;
        private String resource;
        private String action;
        private Map<String, Object> userAttributes;  // 用户ABAC属性（可选）
        private Map<String, Object> resourceAttributes;  // 资源ABAC属性（可选）
        private Map<String, Object> environmentAttributes;  // 环境ABAC属性（可选）
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckPermissionResponse {
        private Boolean hasPermission;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckPermissionsRequest {
        private String userId;
        private List<String> permissions;  // 权限列表（格式：{resource}:{action}）
        private Map<String, Object> userAttributes;  // 用户ABAC属性（可选）
        private Map<String, Object> resourceAttributes;  // 资源ABAC属性（可选）
        private Map<String, Object> environmentAttributes;  // 环境ABAC属性（可选）
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PermissionCheckResult {
        private String permission;  // 权限标识
        private Boolean hasPermission;  // 是否有权限
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckPermissionsResponse {
        private List<PermissionCheckResult> results;
    }
}


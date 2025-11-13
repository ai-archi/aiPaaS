package com.aixone.directory.permission.application;

import com.aixone.directory.permission.domain.aggregate.Permission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 权限相关 DTO
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
public final class PermissionDto {
    private PermissionDto() {}

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatePermissionCommand {
        private String tenantId;
        private String name;
        private String code;  // 权限编码（唯一）
        private String resource;  // 资源标识
        private String action;  // 操作标识（read、write、delete等）
        private Permission.PermissionType type;  // 权限类型：FUNCTIONAL/DATA
        private String description;
        private Map<String, Object> abacConditions;  // ABAC条件（JSON格式）
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdatePermissionCommand {
        private String name;
        private String code;
        private String resource;
        private String action;
        private Permission.PermissionType type;
        private String description;
        private Map<String, Object> abacConditions;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PermissionView {
        private String permissionId;
        private String tenantId;
        private String name;
        private String code;
        private String resource;
        private String action;
        private Permission.PermissionType type;
        private String description;
        private Map<String, Object> abacConditions;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}


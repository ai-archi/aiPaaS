package com.aixone.directory.permission.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限规则相关 DTO
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
public final class PermissionRuleDto {
    private PermissionRuleDto() {}

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatePermissionRuleCommand {
        private String tenantId;
        private String pattern;  // 路径模式，支持Ant路径匹配（**、*）
        private List<String> methods;  // HTTP方法数组（GET、POST、PUT、DELETE等）
        private String permission;  // 权限标识，格式：{resource}:{action} 或 admin:{resource}:{action}
        private String description;
        private Boolean enabled;
        private Integer priority;  // 优先级，数字越大优先级越高
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdatePermissionRuleCommand {
        private String pattern;
        private List<String> methods;
        private String permission;
        private String description;
        private Boolean enabled;
        private Integer priority;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PermissionRuleView {
        private String id;
        private String tenantId;
        private String pattern;
        private List<String> methods;
        private String permission;
        private String description;
        private Boolean enabled;
        private Integer priority;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}


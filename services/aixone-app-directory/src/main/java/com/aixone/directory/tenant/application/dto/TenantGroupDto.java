package com.aixone.directory.tenant.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 租户组 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantGroupDto {

    private String id;
    private String name;
    private String description;
    private String parentId;
    private Integer sortOrder;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 创建租户组请求
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateTenantGroupRequest {
        private String name;
        private String description;
        private String parentId;
        private Integer sortOrder;
    }

    /**
     * 更新租户组请求
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTenantGroupRequest {
        private String name;
        private String description;
        private String parentId;
        private Integer sortOrder;
    }
}


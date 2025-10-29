package com.aixone.directory.menu.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单相关 DTO
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
public final class MenuDto {
    private MenuDto() {}

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateMenuCommand {
        private String tenantId;
        private String parentId;
        private String name;
        private String title;
        private String path;
        private String icon;
        private String type;
        private String renderType;
        private String component;
        private String url;
        private Boolean keepalive;
        private Integer displayOrder;
        private Boolean visible;
        private String config;
        private String extend;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateMenuCommand {
        private String name;
        private String title;
        private String path;
        private String icon;
        private String type;
        private String renderType;
        private String component;
        private String url;
        private Boolean keepalive;
        private Integer displayOrder;
        private Boolean visible;
        private String config;
        private String extend;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuView {
        private String id;
        private String tenantId;
        private String parentId;
        private String name;
        private String title;
        private String path;
        private String icon;
        private String type;
        private String renderType;
        private String component;
        private String url;
        private Boolean keepalive;
        private Integer displayOrder;
        private Boolean visible;
        private String config;
        private String extend;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<MenuView> children;
    }
}

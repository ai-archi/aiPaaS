package com.aixone.directory.menu.domain.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 菜单聚合根
 * 菜单是目录服务的核心聚合，负责管理菜单的结构、类型、顺序等
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Menu {
    
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
    
    /**
     * 创建菜单
     */
    public static Menu create(String tenantId, String name, String title, String path) {
        return Menu.builder()
                .tenantId(tenantId)
                .name(name)
                .title(title)
                .path(path)
                .type("menu")
                .renderType("tab")
                .keepalive(false)
                .displayOrder(0)
                .visible(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 更新菜单
     */
    public void update(String name, String title, String path) {
        this.name = name;
        this.title = title;
        this.path = path;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 设置父菜单
     * @param parentId 父菜单ID，如果为 null 表示设置为根菜单
     */
    public void setParent(String parentId) {
        this.parentId = parentId;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 设置显示顺序
     */
    public void setDisplayOrder(Integer order) {
        this.displayOrder = order;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 设置可见性
     */
    public void setVisible(Boolean visible) {
        this.visible = visible;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 设置图标
     */
    public void setIcon(String icon) {
        this.icon = icon;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 设置类型
     */
    public void setType(String type) {
        this.type = type;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 设置渲染类型
     */
    public void setRenderType(String renderType) {
        this.renderType = renderType;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 设置组件路径
     */
    public void setComponent(String component) {
        this.component = component;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 设置URL
     */
    public void setUrl(String url) {
        this.url = url;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 设置缓存
     */
    public void setKeepalive(Boolean keepalive) {
        this.keepalive = keepalive;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 设置配置
     */
    public void setConfig(String config) {
        this.config = config;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 设置扩展属性
     */
    public void setExtend(String extend) {
        this.extend = extend;
        this.updatedAt = LocalDateTime.now();
    }
}

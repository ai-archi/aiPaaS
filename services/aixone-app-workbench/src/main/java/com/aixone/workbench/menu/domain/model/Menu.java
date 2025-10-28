package com.aixone.workbench.menu.domain.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 菜单聚合根
 * 菜单是工作台的核心聚合，负责管理菜单的结构、类型、顺序等
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Entity
@Table(name = "wb_menu", indexes = {
    @Index(name = "idx_tenant_id", columnList = "tenant_id"),
    @Index(name = "idx_parent_id", columnList = "parent_id"),
    @Index(name = "idx_tenant_parent", columnList = "tenant_id, parent_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Menu implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;
    
    /**
     * 租户ID
     */
    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;
    
    /**
     * 父菜单ID（NULL表示根菜单）
     */
    @Column(name = "parent_id")
    private UUID parentId;
    
    /**
     * 菜单名称（路由name）
     */
    @Column(name = "name", length = 100)
    private String name;
    
    /**
     * 菜单标题
     */
    @Column(name = "title", length = 100)
    private String title;
    
    /**
     * 菜单路径（路由路径）
     */
    @Column(name = "path", length = 200)
    private String path;
    
    /**
     * 菜单图标
     */
    @Column(name = "icon", length = 50)
    private String icon;
    
    /**
     * 菜单类型：menu（菜单）、menu_dir（目录）、button（按钮）
     * 直接使用前端期望的字符串格式，避免枚举转换
     */
    @Column(name = "type", nullable = false, length = 20)
    private String type;
    
    /**
     * 显示顺序
     */
    @Column(name = "display_order")
    private Integer displayOrder;
    
    /**
     * 是否可见
     */
    @Column(name = "visible")
    private Boolean visible = true;
    
    /**
     * 渲染类型：tab（标签页）、iframe（内嵌）、link（外部链接）
     */
    @Column(name = "render_type", length = 50)
    private String renderType;
    
    /**
     * 组件路径
     */
    @Column(name = "component", length = 500)
    private String component;
    
    /**
     * 外部链接URL
     */
    @Column(name = "url", length = 500)
    private String url;
    
    /**
     * 是否缓存页面
     */
    @Column(name = "keepalive")
    private Boolean keepalive;
    
    /**
     * 菜单配置（JSON格式，可扩展配置）
     */
    @Column(name = "config", columnDefinition = "TEXT")
    private String config;
    
    /**
     * 扩展属性
     */
    @Column(name = "extend", length = 50)
    private String extend;
    
    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 移除了 MenuType 枚举，改用 String 类型直接存储前端期望的格式
    // type 值：menu, menu_dir, button
    
    /**
     * 业务规则：菜单必须是可见的才能被用户访问
     */
    public boolean isVisible() {
        return visible != null && visible;
    }
    
    /**
     * 业务规则：判断是否为根菜单
     */
    public boolean isRoot() {
        return parentId == null;
    }
}

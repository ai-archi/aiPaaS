package com.aixone.directory.menu.infrastructure.persistence.dbo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 菜单数据库对象
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Entity
@Table(name = "menus", indexes = {
    @Index(name = "idx_menus_tenant_id", columnList = "tenant_id"),
    @Index(name = "idx_menus_parent_id", columnList = "parent_id"),
    @Index(name = "idx_menus_tenant_parent", columnList = "tenant_id, parent_id"),
    @Index(name = "idx_menus_display_order", columnList = "display_order")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuDbo {
    
    @Id
    @Column(name = "id", length = 36)
    private String id;
    
    @Column(name = "tenant_id", length = 36, nullable = false)
    private String tenantId;
    
    @Column(name = "parent_id", length = 36)
    private String parentId;
    
    @Column(name = "name", length = 100, nullable = false)
    private String name;
    
    @Column(name = "title", length = 100, nullable = false)
    private String title;
    
    @Column(name = "path", length = 200, nullable = false)
    private String path;
    
    @Column(name = "icon", length = 50)
    private String icon;
    
    @Column(name = "type", length = 20, nullable = false)
    private String type;
    
    @Column(name = "render_type", length = 50)
    private String renderType;
    
    @Column(name = "component", length = 500)
    private String component;
    
    @Column(name = "url", length = 500)
    private String url;
    
    @Column(name = "keepalive")
    private Boolean keepalive;
    
    @Column(name = "display_order")
    private Integer displayOrder;
    
    @Column(name = "visible")
    private Boolean visible;
    
    @Column(name = "config", columnDefinition = "TEXT")
    private String config;
    
    @Column(name = "extend", length = 50)
    private String extend;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

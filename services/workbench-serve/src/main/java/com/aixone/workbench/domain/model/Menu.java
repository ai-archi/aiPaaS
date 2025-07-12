package com.aixone.workbench.domain.model;

import jakarta.persistence.*;
import java.util.*;

/**
 * 菜单聚合领域模型，支持多级结构、个性化配置、权限控制。
 */
@Entity
@Table(name = "menu")
public class Menu {
    /** 菜单ID */
    @Id
    @Column(name = "menu_id", nullable = false, updatable = false)
    private UUID menuId;

    /** 租户ID */
    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    /** 父菜单ID */
    @Column(name = "parent_id")
    private UUID parentId;

    /** 菜单名称 */
    @Column(name = "name", nullable = false)
    private String name;

    /** 路径 */
    @Column(name = "path")
    private String path;

    /** 图标 */
    @Column(name = "icon")
    private String icon;

    /** 排序 */
    @Column(name = "order_num")
    private Integer order;

    /** 类型（目录/菜单/按钮） */
    @Column(name = "type")
    private String type;

    /** 是否可见 */
    @Column(name = "visible")
    private Boolean visible;

    /** 个性化配置（JSON） */
    @Column(name = "config", columnDefinition = "TEXT")
    private String config;

    /** 可见角色ID列表 */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "menu_roles", joinColumns = @JoinColumn(name = "menu_id"))
    @Column(name = "role_id")
    private List<UUID> roles;

    /** 用户个性化配置（如顺序、快捷入口、隐藏等，JSON） */
    @Column(name = "user_custom", columnDefinition = "TEXT")
    private String userCustom;

    // getter/setter 省略
} 
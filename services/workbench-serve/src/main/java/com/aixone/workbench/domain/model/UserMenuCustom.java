package com.aixone.workbench.domain.model;

import jakarta.persistence.*;
import java.util.UUID;

/**
 * 用户菜单个性化配置实体。
 */
@Entity
@Table(name = "user_menu_custom")
public class UserMenuCustom {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /** 用户ID */
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    /** 菜单ID */
    @Column(name = "menu_id", nullable = false)
    private UUID menuId;

    /** 个性化配置（JSON） */
    @Column(name = "config", columnDefinition = "TEXT")
    private String config;

    // getter/setter 省略
} 
package com.aixone.workbench.domain.model;

import jakarta.persistence.*;
import java.util.UUID;

/**
 * 用户仪表盘配置实体。
 */
@Entity
@Table(name = "user_dashboard")
public class UserDashboard {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /** 用户ID */
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    /** 仪表盘配置（JSON） */
    @Column(name = "config", columnDefinition = "TEXT")
    private String config;

    // getter/setter 省略
} 
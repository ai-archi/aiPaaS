package com.aixone.workbench.domain.model;

import jakarta.persistence.*;
import java.util.UUID;

/**
 * 用户快捷入口配置实体。
 */
@Entity
@Table(name = "user_quick_entry")
public class UserQuickEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /** 用户ID */
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    /** 快捷入口ID */
    @Column(name = "entry_id", nullable = false)
    private UUID entryId;

    /** 配置（JSON） */
    @Column(name = "config", columnDefinition = "TEXT")
    private String config;

    // getter/setter 省略
} 
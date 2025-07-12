package com.aixone.workbench.domain.model;

import jakarta.persistence.*;
import java.util.*;

/**
 * 快捷入口/仪表盘组件领域模型。
 */
@Entity
@Table(name = "quick_entry")
public class QuickEntry {
    /** 快捷入口/仪表盘组件ID */
    @Id
    @Column(name = "entry_id", nullable = false, updatable = false)
    private UUID entryId;

    /** 用户ID */
    @Column(name = "user_id")
    private UUID userId;

    /** 租户ID */
    @Column(name = "tenant_id")
    private UUID tenantId;

    /** 类型（快捷入口/仪表盘组件） */
    @Column(name = "type")
    private String type;

    /** 配置（布局、样式等，JSON） */
    @Column(name = "config", columnDefinition = "TEXT")
    private String config;

    // getter/setter 省略
} 
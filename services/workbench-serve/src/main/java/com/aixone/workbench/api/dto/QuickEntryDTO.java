package com.aixone.workbench.api.dto;

import java.util.*;

/**
 * 快捷入口/仪表盘DTO。
 */
public class QuickEntryDTO {
    /** 快捷入口/仪表盘组件ID */
    private UUID entryId;
    /** 用户ID */
    private UUID userId;
    /** 租户ID */
    private UUID tenantId;
    /** 类型 */
    private String type;
    /** 配置（JSON） */
    private String config;
    // getter/setter 省略
} 
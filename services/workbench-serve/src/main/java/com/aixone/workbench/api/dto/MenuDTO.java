package com.aixone.workbench.api.dto;

import java.util.*;

/**
 * 菜单DTO，用于接口数据传输。
 */
public class MenuDTO {
    /** 菜单ID */
    private UUID menuId;
    /** 租户ID */
    private UUID tenantId;
    /** 父菜单ID */
    private UUID parentId;
    /** 菜单名称 */
    private String name;
    /** 路径 */
    private String path;
    /** 图标 */
    private String icon;
    /** 排序 */
    private Integer order;
    /** 类型（目录/菜单/按钮） */
    private String type;
    /** 是否可见 */
    private Boolean visible;
    /** 个性化配置（JSON） */
    private String config;
    /** 可见角色ID列表 */
    private List<UUID> roles;
    /** 用户个性化配置（JSON） */
    private String userCustom;
    // getter/setter 省略
} 
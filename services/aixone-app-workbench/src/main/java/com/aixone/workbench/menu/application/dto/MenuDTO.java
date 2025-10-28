package com.aixone.workbench.menu.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * 菜单DTO
 * 用于API数据传输，匹配前端期望的菜单结构
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 菜单ID
     */
    private String id;
    
    /**
     * 租户ID
     */
    private UUID tenantId;
    
    /**
     * 父菜单ID
     */
    private UUID parentId;
    
    /**
     * 菜单名称（用于路由name）
     */
    private String name;
    
    /**
     * 菜单标题
     */
    private String title;
    
    /**
     * 菜单路径
     */
    private String path;
    
    /**
     * 菜单图标
     */
    private String icon;
    
    /**
     * 菜单类型：menu, menu_dir, route等
     * 注意：前端期望 "menu", "menu_dir", "button" 等小写形式
     */
    @JsonProperty("type")
    private String type;
    
    /**
     * 渲染类型：tab（标签页）、iframe（内嵌）、link（外部链接）
     */
    @JsonProperty("renderType")
    private String renderType;
    
    /**
     * 组件路径（用于动态路由）
     */
    private String component;
    
    /**
     * 外部链接URL（用于link或iframe类型）
     */
    private String url;
    
    /**
     * 是否缓存页面
     */
    private Boolean keepalive;
    
    /**
     * 显示顺序
     */
    private Integer displayOrder;
    
    /**
     * 是否可见
     */
    private Boolean visible;
    
    /**
     * 配置信息（JSON格式）
     */
    private String config;
    
    /**
     * 扩展属性
     */
    private String extend;
    
    /**
     * 子菜单列表
     */
    private List<MenuDTO> children;
}

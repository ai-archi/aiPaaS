package com.aixone.workbench.menu.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

/**
 * 用户菜单个性化配置DTO
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMenuCustomDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 配置ID
     */
    private UUID id;
    
    /**
     * 用户ID
     */
    private UUID userId;
    
    /**
     * 租户ID
     */
    private UUID tenantId;
    
    /**
     * 菜单ID
     */
    private UUID menuId;
    
    /**
     * 配置内容
     */
    private String config;
    
    /**
     * 是否为快捷入口
     */
    private Boolean isQuickEntry;
    
    /**
     * 自定义顺序
     */
    private Integer customOrder;
    
    /**
     * 是否隐藏
     */
    private Boolean isHidden;
}

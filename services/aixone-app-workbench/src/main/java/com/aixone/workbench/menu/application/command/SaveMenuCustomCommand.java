package com.aixone.workbench.menu.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

/**
 * 保存菜单个性化配置命令
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveMenuCustomCommand implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private UUID userId;
    private UUID tenantId;
    private UUID menuId;
    private String config;
    private Boolean isQuickEntry;
    private Integer customOrder;
    private Boolean isHidden;
}


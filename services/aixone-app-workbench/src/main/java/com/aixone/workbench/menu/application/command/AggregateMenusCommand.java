package com.aixone.workbench.menu.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * 聚合菜单命令
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AggregateMenusCommand implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private UUID userId;
    private UUID tenantId;
    private List<UUID> userRoles;
}


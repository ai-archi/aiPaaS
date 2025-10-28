package com.aixone.workbench.menu.domain.service;

import com.aixone.workbench.menu.application.dto.MenuDTO;
import com.aixone.workbench.menu.application.dto.UserMenuCustomDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 菜单聚合领域服务
 * 负责菜单的聚合逻辑，包括主数据拉取、个性化配置应用、权限过滤等
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
public interface MenuAggregationService {
    
    /**
     * 聚合用户可见菜单
     * 
     * 流程：
     * 1. 从目录服务拉取菜单主数据
     * 2. 查询用户个性化配置
     * 3. 应用个性化配置（顺序、隐藏等）
     * 4. 权限过滤（基于用户角色）
     * 5. 构建菜单树
     * 
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @param userRoles 用户角色列表
     * @return 菜单树
     */
    List<MenuDTO> aggregateVisibleMenus(UUID userId, UUID tenantId, List<UUID> userRoles);
    
    /**
     * 获取用户菜单个性化配置
     * 
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @return 个性化配置映射（key=menuId, value=配置）
     */
    Map<String, UserMenuCustomDTO> getUserMenuCustomConfig(UUID userId, UUID tenantId);
    
    /**
     * 保存用户菜单个性化配置
     * 
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @param menuId 菜单ID
     * @param config 配置内容（JSON格式）
     */
    void saveUserMenuCustom(UUID userId, UUID tenantId, UUID menuId, String config);
    
    /**
     * 获取菜单树（不包含个性化配置）
     * 
     * @param tenantId 租户ID
     * @param userRoles 用户角色列表
     * @return 菜单树
     */
    List<MenuDTO> buildMenuTree(UUID tenantId, List<UUID> userRoles);
}

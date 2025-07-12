package com.aixone.workbench.service;

import com.aixone.workbench.api.dto.MenuDTO;
import java.util.List;
import java.util.UUID;

/**
 * 菜单聚合与个性化服务接口。
 */
public interface MenuService {
    /**
     * 获取当前用户可见菜单（聚合主数据+个性化配置）
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @return 可见菜单列表
     */
    List<MenuDTO> getVisibleMenus(UUID userId, UUID tenantId);

    /**
     * 保存用户菜单个性化配置（如顺序、快捷入口、隐藏等）
     * @param userId 用户ID
     * @param menuCustomJson 个性化配置JSON
     */
    void saveUserMenuCustom(UUID userId, String menuCustomJson);
} 
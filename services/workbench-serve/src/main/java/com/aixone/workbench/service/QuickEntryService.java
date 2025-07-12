package com.aixone.workbench.service;

import com.aixone.workbench.api.dto.QuickEntryDTO;
import java.util.List;
import java.util.UUID;

/**
 * 快捷入口/仪表盘服务接口。
 */
public interface QuickEntryService {
    /**
     * 获取用户快捷入口
     * @param userId 用户ID
     * @return 快捷入口列表
     */
    List<QuickEntryDTO> getQuickEntries(UUID userId);

    /**
     * 保存用户快捷入口配置
     * @param userId 用户ID
     * @param quickEntryJson 快捷入口配置JSON
     */
    void saveQuickEntries(UUID userId, String quickEntryJson);

    /**
     * 获取用户仪表盘配置
     * @param userId 用户ID
     * @return 仪表盘配置JSON
     */
    String getDashboard(UUID userId);

    /**
     * 保存用户仪表盘配置
     * @param userId 用户ID
     * @param dashboardJson 仪表盘配置JSON
     */
    void saveDashboard(UUID userId, String dashboardJson);
} 
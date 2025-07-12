package com.aixone.workbench.service.impl;

import com.aixone.workbench.api.dto.QuickEntryDTO;
import com.aixone.workbench.service.QuickEntryService;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * 快捷入口/仪表盘服务实现。
 */
@Service
public class QuickEntryServiceImpl implements QuickEntryService {
    @Override
    public List<QuickEntryDTO> getQuickEntries(UUID userId) {
        // TODO: 查询并返回用户快捷入口
        return Collections.emptyList();
    }

    @Override
    public void saveQuickEntries(UUID userId, String quickEntryJson) {
        // TODO: 持久化用户快捷入口配置
    }

    @Override
    public String getDashboard(UUID userId) {
        // TODO: 查询并返回用户仪表盘配置
        return "";
    }

    @Override
    public void saveDashboard(UUID userId, String dashboardJson) {
        // TODO: 持久化用户仪表盘配置
    }
} 
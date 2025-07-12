package com.aixone.workbench.controller;

import com.aixone.workbench.api.dto.QuickEntryDTO;
import com.aixone.workbench.service.QuickEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

/**
 * 快捷入口与仪表盘API。
 */
@RestController
@RequestMapping("/api/v1")
public class QuickEntryController {
    @Autowired
    private QuickEntryService quickEntryService;

    /**
     * 获取用户快捷入口
     */
    @GetMapping("/quick-entries")
    public List<QuickEntryDTO> getQuickEntries(@RequestParam UUID userId) {
        return quickEntryService.getQuickEntries(userId);
    }

    /**
     * 保存用户快捷入口配置
     */
    @PutMapping("/quick-entries")
    public void saveQuickEntries(@RequestParam UUID userId, @RequestBody String quickEntryJson) {
        quickEntryService.saveQuickEntries(userId, quickEntryJson);
    }

    /**
     * 获取用户仪表盘配置
     */
    @GetMapping("/dashboard")
    public String getDashboard(@RequestParam UUID userId) {
        return quickEntryService.getDashboard(userId);
    }

    /**
     * 保存用户仪表盘配置
     */
    @PutMapping("/dashboard")
    public void saveDashboard(@RequestParam UUID userId, @RequestBody String dashboardJson) {
        quickEntryService.saveDashboard(userId, dashboardJson);
    }
} 
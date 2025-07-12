package com.aixone.workbench.controller;

import com.aixone.workbench.api.dto.MenuDTO;
import com.aixone.workbench.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

/**
 * 菜单聚合与个性化API。
 */
@RestController
@RequestMapping("/api/v1/menus")
public class MenuController {
    @Autowired
    private MenuService menuService;

    /**
     * 获取当前用户可见菜单
     */
    @GetMapping
    public List<MenuDTO> getVisibleMenus(@RequestParam UUID userId, @RequestParam UUID tenantId) {
        return menuService.getVisibleMenus(userId, tenantId);
    }

    /**
     * 保存用户菜单个性化配置
     */
    @PutMapping("/custom")
    public void saveUserMenuCustom(@RequestParam UUID userId, @RequestBody String menuCustomJson) {
        menuService.saveUserMenuCustom(userId, menuCustomJson);
    }
} 
package com.aixone.tech.auth.authentication.interfaces.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 站点配置接口控制器
 * 提供前端初始化需要的配置信息
 */
@RestController
@RequestMapping("/api/index")
public class IndexController {

    /**
     * 站点初始化接口
     * 返回站点配置、路由信息、用户信息等
     */
    @GetMapping("/index")
    public ResponseEntity<Map<String, Object>> getIndexConfig(@RequestParam(required = false) Integer requiredLogin) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 1);
            response.put("msg", "获取配置成功");
            
            // 构建站点配置
            Map<String, Object> site = new HashMap<>();
            site.put("siteName", "AI Xone");
            site.put("version", "1.0.0");
            site.put("cdnUrl", "");
            site.put("apiUrl", "http://localhost:8080/api/v1");
            site.put("upload", Map.of("mode", "local"));
            site.put("headNav", new Object[0]);
            site.put("recordNumber", "");
            site.put("cdnUrlParams", "");
            site.put("initialize", true);
            site.put("userInitialize", false);

            // 构建路由规则（空数组，由前端管理）
            Object[] rules = new Object[0];
            
            // 构建会员中心基础菜单（工作台）
            Map<String, Object> dashboardMenu = new HashMap<>();
            dashboardMenu.put("id", 1);
            dashboardMenu.put("name", "user-overview");
            dashboardMenu.put("title", "工作台");
            dashboardMenu.put("path", "overview");
            dashboardMenu.put("component", "/@/views/frontend/user/account/overview.vue");
            dashboardMenu.put("menu_type", "route");
            dashboardMenu.put("type", "menu");
            dashboardMenu.put("icon", "fa fa-dashboard");
            dashboardMenu.put("keepalive", true);
            dashboardMenu.put("extend", "");
            
            Object[] menus = new Object[]{dashboardMenu};

            // 构建用户信息（如果已登录）
            Map<String, Object> userInfo = new HashMap<>();
            if (requiredLogin != null && requiredLogin == 1) {
                // 这里可以根据token获取用户信息
                // 暂时返回空用户信息
            }

            response.put("data", Map.of(
                "site", site,
                "rules", rules,
                "menus", menus,
                "userInfo", userInfo,
                "openMemberCenter", true
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 0);
            response.put("msg", "获取配置失败: " + e.getMessage());
            response.put("data", Map.of(
                "site", Map.of(),
                "rules", new Object[0],
                "menus", new Object[0],
                "userInfo", Map.of(),
                "openMemberCenter", true
            ));
            return ResponseEntity.ok(response);
        }
    }
}

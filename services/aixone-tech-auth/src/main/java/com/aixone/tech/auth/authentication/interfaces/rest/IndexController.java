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
     * 注意：此接口不在认证服务中实现
     * 用户信息、菜单等应由专门的业务微服务处理
     * 认证服务仅负责认证相关功能
     */
    @GetMapping("/index")
    public ResponseEntity<Map<String, Object>> getIndexConfig(@RequestParam(required = false) Integer requiredLogin) {
        // 返回一个简单的配置，表明认证服务正在运行
        Map<String, Object> response = new HashMap<>();
        response.put("code", 1);
        response.put("msg", "认证服务运行正常，请配置业务服务获取用户信息和菜单");
        
        // 返回空的站点配置，由业务服务填充
        Map<String, Object> site = new HashMap<>();
        site.put("siteName", "AI Xone Auth Service");
        site.put("version", "1.0.0");
        site.put("apiUrl", "http://localhost:8080/api/v1");
        
        response.put("data", Map.of(
            "site", site,
            "rules", new Object[0],
            "menus", new Object[0],
            "userInfo", Map.of(),
            "adminInfo", Map.of(),
            "openMemberCenter", true
        ));
        
        return ResponseEntity.ok(response);
    }
}

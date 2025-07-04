package com.aixone.auth.controller;

import org.springframework.web.bind.annotation.*;

/**
 * 租户注册开关相关API
 */
@RestController
@RequestMapping("/api/v1/tenants")
public class TenantRegisterController {
    /** 查询租户注册开关 */
    @GetMapping("/{tenantId}/register-enabled")
    public Object getRegisterEnabled(@PathVariable String tenantId) {
        // TODO: 查询租户是否允许注册
        return null;
    }

    /** 配置租户注册开关 */
    @PutMapping("/{tenantId}/register-enabled")
    public Object setRegisterEnabled(@PathVariable String tenantId, @RequestParam boolean enabled) {
        // TODO: 配置租户注册开关
        return null;
    }
} 
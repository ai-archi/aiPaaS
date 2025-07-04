package com.aixone.auth.tenant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 租户管理API
 */
@RestController
@RequestMapping("/api/v1/tenants")
public class TenantController {
    @Autowired
    private TenantService tenantService;

    /** 查询所有租户 */
    @GetMapping
    public List<Tenant> listTenants() {
        return tenantService.findAll();
    }

    /** 根据ID查询租户 */
    @GetMapping("/{id}")
    public Optional<Tenant> getTenant(@PathVariable("id") String tenantId) {
        return tenantService.findById(tenantId);
    }

    /** 新增租户 */
    @PostMapping
    public Tenant createTenant(@RequestBody Tenant tenant) {
        return tenantService.save(tenant);
    }

    /** 删除租户 */
    @DeleteMapping("/{id}")
    public void deleteTenant(@PathVariable("id") String tenantId) {
        tenantService.deleteById(tenantId);
    }
} 
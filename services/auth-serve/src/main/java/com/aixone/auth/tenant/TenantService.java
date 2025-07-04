package com.aixone.auth.tenant;

import java.util.List;
import java.util.Optional;

/**
 * 租户服务接口
 * 定义Tenant相关的业务操作
 */
public interface TenantService {
    Optional<Tenant> findById(String tenantId);
    List<Tenant> findAll();
    Tenant save(Tenant tenant);
    void deleteById(String tenantId);
} 
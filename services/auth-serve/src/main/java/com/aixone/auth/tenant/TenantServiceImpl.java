package com.aixone.auth.tenant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 租户服务实现
 */
@Service
public class TenantServiceImpl implements TenantService {
    @Autowired
    private TenantRepository tenantRepository;

    @Override
    public Optional<Tenant> findById(String tenantId) {
        return tenantRepository.findById(tenantId);
    }

    @Override
    public List<Tenant> findAll() {
        return tenantRepository.findAll();
    }

    @Override
    public Tenant save(Tenant tenant) {
        return tenantRepository.save(tenant);
    }

    @Override
    public void deleteById(String tenantId) {
        tenantRepository.deleteById(tenantId);
    }
} 
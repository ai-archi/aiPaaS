package com.aixone.metacenter.integrationorchestration.infrastructure;

import com.aixone.metacenter.integrationorchestration.domain.Integration;
import com.aixone.metacenter.integrationorchestration.domain.IntegrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 集成编排仓储实现
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Repository
public class IntegrationRepositoryImpl implements IntegrationRepository {

    @Autowired
    private IntegrationJpaRepository integrationJpaRepository;

    @Override
    public Optional<Integration> findByTenantIdAndName(String tenantId, String name) {
        return integrationJpaRepository.findByTenantIdAndName(tenantId, name);
    }

    @Override
    public List<Integration> findByTenantIdAndType(String tenantId, String type) {
        return integrationJpaRepository.findByTenantIdAndType(tenantId, type);
    }

    @Override
    public List<Integration> findByTenantIdAndStatus(String tenantId, String status) {
        return integrationJpaRepository.findByTenantIdAndStatus(tenantId, status);
    }

    @Override
    public Page<Integration> findByTenantId(String tenantId, Pageable pageable) {
        return integrationJpaRepository.findByTenantId(tenantId, pageable);
    }

    @Override
    public Page<Integration> findByTenantIdAndNameContainingIgnoreCase(String tenantId, String name, Pageable pageable) {
        return integrationJpaRepository.findByTenantIdAndNameContainingIgnoreCase(tenantId, name, pageable);
    }

    @Override
    public boolean existsByTenantIdAndName(String tenantId, String name) {
        return integrationJpaRepository.existsByTenantIdAndName(tenantId, name);
    }

    @Override
    public long countByTenantId(String tenantId) {
        return integrationJpaRepository.countByTenantId(tenantId);
    }

    @Override
    public long countByTenantIdAndType(String tenantId, String type) {
        return integrationJpaRepository.countByTenantIdAndType(tenantId, type);
    }

    // JpaRepository 基本方法实现
    public Integration save(Integration integration) {
        return integrationJpaRepository.save(integration);
    }

    public List<Integration> saveAll(List<Integration> integrations) {
        return integrationJpaRepository.saveAll(integrations);
    }

    public List<Integration> findAll() {
        return integrationJpaRepository.findAll();
    }

    public Optional<Integration> findById(Long id) {
        return integrationJpaRepository.findById(id);
    }

    public void deleteById(Long id) {
        integrationJpaRepository.deleteById(id);
    }

    public void deleteAll(List<Integration> integrations) {
        integrationJpaRepository.deleteAll(integrations);
    }

    public boolean existsById(Long id) {
        return integrationJpaRepository.existsById(id);
    }

    public long count() {
        return integrationJpaRepository.count();
    }
}

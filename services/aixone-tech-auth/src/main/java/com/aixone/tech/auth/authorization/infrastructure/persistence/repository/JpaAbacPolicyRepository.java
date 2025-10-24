package com.aixone.tech.auth.authorization.infrastructure.persistence.repository;

import com.aixone.tech.auth.authorization.domain.model.AbacPolicy;
import com.aixone.tech.auth.authorization.domain.repository.AbacPolicyRepository;
import com.aixone.tech.auth.authorization.infrastructure.persistence.entity.AbacPolicyEntity;
import com.aixone.tech.auth.authorization.infrastructure.persistence.mapper.AbacPolicyMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AbacPolicy JPA 仓储实现
 */
@Repository
public class JpaAbacPolicyRepository implements AbacPolicyRepository {

    private final AbacPolicyJpaRepository jpaRepository;
    private final AbacPolicyMapper mapper;

    public JpaAbacPolicyRepository(AbacPolicyJpaRepository jpaRepository, AbacPolicyMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<AbacPolicy> findByTenantIdAndResourceAndAction(String tenantId, String resource, String action) {
        return jpaRepository.findByTenantIdAndResourceAndAction(tenantId, resource, action)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public AbacPolicy save(AbacPolicy policy) {
        AbacPolicyEntity entity = mapper.toEntity(policy);
        AbacPolicyEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public AbacPolicy findByTenantIdAndPolicyId(String tenantId, String policyId) {
        return jpaRepository.findByTenantIdAndPolicyId(tenantId, policyId)
                .map(mapper::toDomain)
                .orElse(null);
    }

    @Override
    public List<AbacPolicy> findByTenantId(String tenantId) {
        return jpaRepository.findByTenantId(tenantId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteByTenantIdAndPolicyId(String tenantId, String policyId) {
        jpaRepository.deleteByTenantIdAndPolicyId(tenantId, policyId);
    }

    @Override
    public boolean existsByTenantIdAndName(String tenantId, String name) {
        return jpaRepository.existsByTenantIdAndName(tenantId, name);
    }
}

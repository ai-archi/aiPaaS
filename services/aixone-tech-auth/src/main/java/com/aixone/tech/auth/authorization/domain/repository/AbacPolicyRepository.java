package com.aixone.tech.auth.authorization.domain.repository;

import com.aixone.tech.auth.authorization.domain.model.AbacPolicy;
import java.util.List;

/**
 * ABAC策略仓储接口
 */
public interface AbacPolicyRepository {
    AbacPolicy save(AbacPolicy policy);
    AbacPolicy findByTenantIdAndPolicyId(String tenantId, String policyId);
    List<AbacPolicy> findByTenantIdAndResourceAndAction(String tenantId, String resource, String action);
    List<AbacPolicy> findByTenantId(String tenantId);
    void deleteByTenantIdAndPolicyId(String tenantId, String policyId);
    boolean existsByTenantIdAndName(String tenantId, String name);
}

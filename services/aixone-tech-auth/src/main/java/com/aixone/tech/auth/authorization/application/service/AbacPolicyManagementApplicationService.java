package com.aixone.tech.auth.authorization.application.service;

import com.aixone.tech.auth.authorization.application.dto.AbacPolicyResponse;
import com.aixone.tech.auth.authorization.application.dto.CreateAbacPolicyRequest;
import com.aixone.tech.auth.authorization.application.dto.UpdateAbacPolicyRequest;
import com.aixone.tech.auth.authorization.domain.model.AbacPolicy;
import com.aixone.tech.auth.authorization.domain.repository.AbacPolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class AbacPolicyManagementApplicationService {

    private final AbacPolicyRepository abacPolicyRepository;

    public AbacPolicyManagementApplicationService(AbacPolicyRepository abacPolicyRepository) {
        this.abacPolicyRepository = abacPolicyRepository;
    }

    public AbacPolicyResponse createAbacPolicy(CreateAbacPolicyRequest request) {
        if (abacPolicyRepository.existsByTenantIdAndName(request.getTenantId(), request.getName())) {
            throw new IllegalArgumentException("策略名称已存在");
        }

        AbacPolicy policy = new AbacPolicy(
            UUID.randomUUID().toString(),
            request.getTenantId(),
            request.getName(),
            request.getDescription(),
            request.getResource(),
            request.getAction(),
            request.getCondition(),
            request.getAttributes()
        );
        AbacPolicy savedPolicy = abacPolicyRepository.save(policy);
        return toAbacPolicyResponse(savedPolicy);
    }

    public AbacPolicyResponse updateAbacPolicy(String policyId, UpdateAbacPolicyRequest request) {
        AbacPolicy existingPolicy = abacPolicyRepository.findByTenantIdAndPolicyId(request.getTenantId(), policyId);
        if (existingPolicy == null) {
            throw new IllegalArgumentException("策略不存在");
        }

        if (!existingPolicy.getName().equals(request.getName()) && abacPolicyRepository.existsByTenantIdAndName(request.getTenantId(), request.getName())) {
            throw new IllegalArgumentException("策略名称已存在");
        }

        existingPolicy.setName(request.getName());
        existingPolicy.setDescription(request.getDescription());
        existingPolicy.setResource(request.getResource());
        existingPolicy.setAction(request.getAction());
        existingPolicy.setCondition(request.getCondition());
        existingPolicy.setAttributes(request.getAttributes());
        existingPolicy.setUpdatedAt(LocalDateTime.now());

        AbacPolicy updatedPolicy = abacPolicyRepository.save(existingPolicy);
        return toAbacPolicyResponse(updatedPolicy);
    }

    public void deleteAbacPolicy(String tenantId, String policyId) {
        abacPolicyRepository.deleteByTenantIdAndPolicyId(tenantId, policyId);
    }

    public AbacPolicyResponse getAbacPolicyById(String tenantId, String policyId) {
        AbacPolicy policy = abacPolicyRepository.findByTenantIdAndPolicyId(tenantId, policyId);
        if (policy == null) {
            throw new IllegalArgumentException("策略不存在");
        }
        return toAbacPolicyResponse(policy);
    }

    public List<AbacPolicyResponse> getAllAbacPolicies(String tenantId) {
        return abacPolicyRepository.findByTenantId(tenantId)
            .stream()
            .map(this::toAbacPolicyResponse)
            .collect(Collectors.toList());
    }

    public List<AbacPolicyResponse> getAbacPoliciesByResourceAndAction(String tenantId, String resource, String action) {
        return abacPolicyRepository.findByTenantIdAndResourceAndAction(tenantId, resource, action)
            .stream()
            .map(this::toAbacPolicyResponse)
            .collect(Collectors.toList());
    }

    private AbacPolicyResponse toAbacPolicyResponse(AbacPolicy policy) {
        return new AbacPolicyResponse(
            policy.getPolicyId(),
            policy.getTenantId(),
            policy.getName(),
            policy.getDescription(),
            policy.getResource(),
            policy.getAction(),
            policy.getCondition(),
            policy.getAttributes(),
            policy.getCreatedAt(),
            policy.getUpdatedAt()
        );
    }
}

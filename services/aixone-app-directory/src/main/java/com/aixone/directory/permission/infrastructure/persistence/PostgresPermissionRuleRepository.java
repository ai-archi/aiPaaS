package com.aixone.directory.permission.infrastructure.persistence;

import com.aixone.directory.permission.domain.aggregate.PermissionRule;
import com.aixone.directory.permission.domain.repository.PermissionRuleRepository;
import com.aixone.directory.permission.infrastructure.persistence.dbo.PermissionRuleDbo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 权限规则仓储实现
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Repository
@RequiredArgsConstructor
public class PostgresPermissionRuleRepository implements PermissionRuleRepository {

    private final PermissionRuleJpaRepository permissionRuleJpaRepository;

    @Override
    public PermissionRule save(PermissionRule permissionRule) {
        PermissionRuleDbo dbo = convertToDbo(permissionRule);
        PermissionRuleDbo savedDbo = permissionRuleJpaRepository.save(dbo);
        return convertToDomain(savedDbo);
    }

    @Override
    public Optional<PermissionRule> findById(String id) {
        return permissionRuleJpaRepository.findById(id)
                .map(this::convertToDomain);
    }

    @Override
    public List<PermissionRule> findByTenantId(String tenantId) {
        List<PermissionRuleDbo> dbos = permissionRuleJpaRepository.findByTenantId(tenantId);
        return dbos.stream()
                .map(this::convertToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<PermissionRule> findByPathAndMethod(String tenantId, String path, String method) {
        // 获取该租户下所有启用的权限规则
        List<PermissionRuleDbo> dbos = permissionRuleJpaRepository.findByTenantIdAndEnabledOrderByPriorityDesc(tenantId);
        
        // 过滤匹配的权限规则
        return dbos.stream()
                .map(this::convertToDomain)
                .filter(rule -> rule.matches(path, method))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String id) {
        permissionRuleJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return permissionRuleJpaRepository.existsById(id);
    }

    /**
     * 转换为数据库对象
     */
    private PermissionRuleDbo convertToDbo(PermissionRule rule) {
        PermissionRuleDbo.PermissionRuleDboBuilder builder = PermissionRuleDbo.builder()
                .tenantId(rule.getTenantId())
                .pattern(rule.getPattern())
                .permission(rule.getPermission())
                .description(rule.getDescription())
                .enabled(rule.getEnabled())
                .priority(rule.getPriority())
                .createdAt(rule.getCreatedAt())
                .updatedAt(rule.getUpdatedAt());
        
        // 如果ID为空，生成新的UUID
        if (rule.getId() == null || rule.getId().isEmpty()) {
            builder.id(UUID.randomUUID().toString());
        } else {
            builder.id(rule.getId());
        }
        
        PermissionRuleDbo dbo = builder.build();
        // 设置HTTP方法列表（使用便捷方法）
        dbo.setMethods(rule.getMethods());
        
        return dbo;
    }

    /**
     * 转换为领域对象
     */
    private PermissionRule convertToDomain(PermissionRuleDbo dbo) {
        return PermissionRule.builder()
                .id(dbo.getId())
                .tenantId(dbo.getTenantId())
                .pattern(dbo.getPattern())
                .methods(dbo.getMethods())
                .permission(dbo.getPermission())
                .description(dbo.getDescription())
                .enabled(dbo.getEnabled())
                .priority(dbo.getPriority())
                .createdAt(dbo.getCreatedAt())
                .updatedAt(dbo.getUpdatedAt())
                .build();
    }
}


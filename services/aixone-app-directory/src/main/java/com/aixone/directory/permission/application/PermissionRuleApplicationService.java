package com.aixone.directory.permission.application;

import com.aixone.common.api.PageRequest;
import com.aixone.common.api.PageResult;
import com.aixone.directory.permission.domain.aggregate.PermissionRule;
import com.aixone.directory.permission.domain.repository.PermissionRuleRepository;
import com.aixone.directory.permission.infrastructure.persistence.dbo.PermissionRuleDbo;
import com.aixone.directory.permission.infrastructure.persistence.PermissionRuleJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 权限规则应用服务
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionRuleApplicationService {

    private final PermissionRuleRepository permissionRuleRepository;
    private final PermissionRuleJpaRepository permissionRuleJpaRepository;

    /**
     * 创建权限规则
     */
    @Transactional
    public PermissionRuleDto.PermissionRuleView createPermissionRule(PermissionRuleDto.CreatePermissionRuleCommand command) {
        log.info("创建权限规则: pattern={}, tenantId={}", command.getPattern(), command.getTenantId());

        Assert.hasText(command.getPattern(), "路径模式不能为空");
        Assert.notEmpty(command.getMethods(), "HTTP方法不能为空");
        Assert.hasText(command.getPermission(), "权限标识不能为空");
        Assert.hasText(command.getTenantId(), "租户ID不能为空");

        // 创建权限规则
        PermissionRule permissionRule = PermissionRule.create(
                command.getTenantId(),
                command.getPattern(),
                command.getMethods(),
                command.getPermission()
        );
        
        // 设置可选属性
        if (command.getDescription() != null) {
            permissionRule.setDescription(command.getDescription());
        }
        if (command.getEnabled() != null) {
            permissionRule.setEnabled(command.getEnabled());
        }
        if (command.getPriority() != null) {
            permissionRule.setPriority(command.getPriority());
        }
        
        // 生成ID
        permissionRule.setId(UUID.randomUUID().toString());

        PermissionRule savedRule = permissionRuleRepository.save(permissionRule);
        return convertToView(savedRule);
    }

    /**
     * 更新权限规则
     */
    @Transactional
    public PermissionRuleDto.PermissionRuleView updatePermissionRule(String ruleId, PermissionRuleDto.UpdatePermissionRuleCommand command) {
        log.info("更新权限规则: id={}", ruleId);

        PermissionRule permissionRule = permissionRuleRepository.findById(ruleId)
                .orElseThrow(() -> new IllegalArgumentException("权限规则不存在: " + ruleId));

        permissionRule.update(
                command.getPattern(),
                command.getMethods(),
                command.getPermission(),
                command.getDescription(),
                command.getEnabled(),
                command.getPriority()
        );

        PermissionRule updatedRule = permissionRuleRepository.save(permissionRule);
        return convertToView(updatedRule);
    }

    /**
     * 删除权限规则
     */
    @Transactional
    public void deletePermissionRule(String ruleId) {
        log.info("删除权限规则: id={}", ruleId);

        if (!permissionRuleRepository.existsById(ruleId)) {
            throw new IllegalArgumentException("权限规则不存在: " + ruleId);
        }

        permissionRuleRepository.delete(ruleId);
    }

    /**
     * 根据ID查找权限规则
     */
    @Transactional(readOnly = true)
    public Optional<PermissionRuleDto.PermissionRuleView> findPermissionRuleById(String ruleId, String tenantId) {
        log.info("查找权限规则: id={}, tenantId={}", ruleId, tenantId);

        return permissionRuleRepository.findById(ruleId)
                .filter(rule -> rule.belongsToTenant(tenantId))
                .map(this::convertToView);
    }

    /**
     * 分页查询权限规则
     */
    @Transactional(readOnly = true)
    public PageResult<PermissionRuleDto.PermissionRuleView> findPermissionRules(
            PageRequest pageRequest, String tenantId, String pattern, String method) {
        log.info("分页查询权限规则: pageNum={}, pageSize={}, tenantId={}, pattern={}, method={}", 
                pageRequest.getPageNum(), pageRequest.getPageSize(), tenantId, pattern, method);

        Assert.hasText(tenantId, "租户ID不能为空");

        // 构建查询规格
        Specification<PermissionRuleDbo> spec = (root, query, cb) -> {
            List<Predicate> predicates = new java.util.ArrayList<>();
            
            // 必须按租户ID过滤
            predicates.add(cb.equal(root.get("tenantId"), tenantId));
            
            if (StringUtils.hasText(pattern)) {
                predicates.add(cb.like(cb.lower(root.get("pattern")), "%" + pattern.toLowerCase() + "%"));
            }
            
            // 注意：method过滤需要在关联表中查询，这里先简化处理
            // 实际实现中可以通过JOIN查询或子查询来实现
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        // 构建排序：按优先级降序，创建时间升序
        org.springframework.data.domain.Sort sort = org.springframework.data.domain.Sort.by("priority").descending()
                .and(org.springframework.data.domain.Sort.by("createdAt").ascending());
        
        Pageable pageable = org.springframework.data.domain.PageRequest.of(
                pageRequest.getPageNum() - 1, // JPA 页码从 0 开始
                pageRequest.getPageSize(),
                sort
        );

        Page<PermissionRuleDbo> page = permissionRuleJpaRepository.findAll(spec, pageable);
        
        List<PermissionRuleDto.PermissionRuleView> views = page.getContent().stream()
                .map(this::convertDboToView)
                .collect(Collectors.toList());

        return PageResult.of(page.getTotalElements(), pageRequest, views);
    }

    /**
     * 根据路径和方法查找匹配的权限规则
     */
    @Transactional(readOnly = true)
    public List<PermissionRuleDto.PermissionRuleView> findPermissionRulesByPathAndMethod(String tenantId, String path, String method) {
        log.info("查找匹配的权限规则: tenantId={}, path={}, method={}", tenantId, path, method);

        List<PermissionRule> rules = permissionRuleRepository.findByPathAndMethod(tenantId, path, method);
        return rules.stream()
                .map(this::convertToView)
                .collect(Collectors.toList());
    }

    /**
     * 转换为视图对象
     */
    private PermissionRuleDto.PermissionRuleView convertToView(PermissionRule rule) {
        return PermissionRuleDto.PermissionRuleView.builder()
                .id(rule.getId())
                .tenantId(rule.getTenantId())
                .pattern(rule.getPattern())
                .methods(rule.getMethods())
                .permission(rule.getPermission())
                .description(rule.getDescription())
                .enabled(rule.getEnabled())
                .priority(rule.getPriority())
                .createdAt(rule.getCreatedAt())
                .updatedAt(rule.getUpdatedAt())
                .build();
    }

    /**
     * 从Dbo转换为视图对象
     */
    private PermissionRuleDto.PermissionRuleView convertDboToView(PermissionRuleDbo dbo) {
        return PermissionRuleDto.PermissionRuleView.builder()
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


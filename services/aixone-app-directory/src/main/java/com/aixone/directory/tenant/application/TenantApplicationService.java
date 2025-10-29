package com.aixone.directory.tenant.application;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.aixone.common.api.PageResult;
import com.aixone.common.exception.BizException;
import com.aixone.directory.tenant.domain.aggregate.Tenant;
import com.aixone.directory.tenant.domain.repository.TenantRepository;
import com.aixone.directory.tenant.infrastructure.persistence.TenantJpaRepository;
import com.aixone.directory.tenant.infrastructure.persistence.dbo.TenantDbo;

import jakarta.persistence.criteria.Predicate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 租户应用服务
 * 提供租户管理的业务逻辑，包括创建、查询、更新、删除等操作
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenantApplicationService {

    private final TenantRepository tenantRepository;
    private final TenantJpaRepository tenantJpaRepository;

    /**
     * 创建租户
     */
    @Transactional
    public TenantDto.TenantView createTenant(TenantDto.CreateTenantCommand command) {
        Assert.notNull(command, "Command must not be null");
        Assert.hasText(command.getName(), "Tenant name must not be empty");

        log.info("创建租户: name={}", command.getName());

        // 检查名称是否已存在
        if (tenantJpaRepository.existsByName(command.getName())) {
            throw new BizException("TENANT_NAME_EXISTS", "租户名称已存在");
        }

        Tenant tenant = Tenant.create(command.getName(), command.getGroupId());
        tenantRepository.save(tenant);
        return convertToView(tenant);
    }

    /**
     * 根据ID查询租户
     */
    public Optional<TenantDto.TenantView> findTenantById(String tenantId) {
        return tenantRepository.findById(tenantId).map(this::convertToView);
    }

    /**
     * 分页查询租户列表
     */
    public PageResult<TenantDto.TenantView> findTenants(com.aixone.common.api.PageRequest pageRequest, String name, String status) {
        // 构建查询规格
        Specification<TenantDbo> spec = (root, query, cb) -> {
            List<Predicate> predicates = new java.util.ArrayList<>();
            
            if (StringUtils.hasText(name)) {
                predicates.add(cb.like(root.get("name"), "%" + name + "%"));
            }
            
            if (StringUtils.hasText(status)) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        Pageable pageable = org.springframework.data.domain.PageRequest.of(
            pageRequest.getPageNum() - 1, // JPA 页码从 0 开始
            pageRequest.getPageSize()
        );
        
        Page<TenantDbo> page = tenantJpaRepository.findAll(spec, pageable);
        List<TenantDto.TenantView> content = page.getContent().stream()
                .map(this::convertDboToView)
                .collect(Collectors.toList());
        
        return PageResult.of(page.getTotalElements(), pageRequest, content);
    }

    /**
     * 更新租户
     */
    @Transactional
    public TenantDto.TenantView updateTenant(String tenantId, TenantDto.UpdateTenantCommand command) {
        log.info("更新租户: id={}", tenantId);
        
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new BizException("TENANT_NOT_FOUND", "租户不存在"));

        if (command.getName() != null && !command.getName().equals(tenant.getName())) {
            // 检查新名称是否已存在
            if (tenantJpaRepository.existsByName(command.getName())) {
                throw new BizException("TENANT_NAME_EXISTS", "租户名称已存在");
            }
            tenant.updateName(command.getName());
        }

        if (command.getGroupId() != null) {
            tenant.updateGroupId(command.getGroupId());
        }

        if (command.getStatus() != null) {
            if ("SUSPENDED".equals(command.getStatus())) {
                tenant.suspend();
            } else if ("ACTIVE".equals(command.getStatus())) {
                tenant.activate();
            }
        }

        tenantRepository.save(tenant);
        return convertToView(tenant);
    }

    /**
     * 删除租户
     */
    @Transactional
    public void deleteTenant(String tenantId) {
        log.info("删除租户: id={}", tenantId);
        
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new BizException("TENANT_NOT_FOUND", "租户不存在"));
        
        // 可以通过业务规则判断是否可以删除
        tenantRepository.delete(tenantId);
    }

    /**
     * 批量删除租户
     */
    @Transactional
    public void deleteTenants(List<String> tenantIds) {
        log.info("批量删除租户: ids={}", tenantIds);
        for (String tenantId : tenantIds) {
            deleteTenant(tenantId);
        }
    }

    /**
     * 转换领域模型为视图
     */
    private TenantDto.TenantView convertToView(Tenant tenant) {
        return TenantDto.TenantView.builder()
                .id(tenant.getId())
                .name(tenant.getName())
                .groupId(tenant.getGroupId())
                .groupName(null) // 需要时可以通过关联查询获取
                .status(tenant.getStatus().name())
                .createdAt(tenant.getCreatedAt())
                .updatedAt(tenant.getUpdatedAt())
                .build();
    }

    /**
     * 转换DBO为视图
     */
    private TenantDto.TenantView convertDboToView(TenantDbo dbo) {
        return TenantDto.TenantView.builder()
                .id(dbo.getId())
                .name(dbo.getName())
                .groupId(dbo.getGroupId())
                .groupName(null) // 需要时可以通过关联查询获取
                .status(dbo.getStatus().name())
                .createdAt(dbo.getCreatedAt())
                .updatedAt(dbo.getUpdatedAt())
                .build();
    }
} 
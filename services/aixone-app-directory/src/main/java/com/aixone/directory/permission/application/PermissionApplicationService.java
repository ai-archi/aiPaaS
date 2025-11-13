package com.aixone.directory.permission.application;

import com.aixone.common.api.PageRequest;
import com.aixone.common.api.PageResult;
import com.aixone.directory.permission.domain.aggregate.Permission;
import com.aixone.directory.permission.domain.repository.PermissionRepository;
import com.aixone.directory.permission.domain.repository.RolePermissionRepository;
import com.aixone.directory.permission.infrastructure.persistence.dbo.PermissionDbo;
import com.aixone.directory.permission.infrastructure.persistence.PermissionJpaRepository;
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
 * 权限应用服务
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionApplicationService {

    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionJpaRepository permissionJpaRepository;

    /**
     * 创建权限
     */
    @Transactional
    public PermissionDto.PermissionView createPermission(PermissionDto.CreatePermissionCommand command) {
        log.info("创建权限: name={}, tenantId={}", command.getName(), command.getTenantId());

        Assert.hasText(command.getName(), "权限名称不能为空");
        Assert.hasText(command.getCode(), "权限编码不能为空");
        Assert.hasText(command.getResource(), "资源标识不能为空");
        Assert.hasText(command.getAction(), "操作标识不能为空");
        Assert.hasText(command.getTenantId(), "租户ID不能为空");

        // 检查权限编码是否已存在
        if (permissionRepository.existsByTenantIdAndCode(command.getTenantId(), command.getCode())) {
            throw new IllegalArgumentException("权限编码已存在: " + command.getCode());
        }

        // 创建权限
        Permission permission = Permission.create(
                command.getTenantId(),
                command.getName(),
                command.getCode(),
                command.getResource(),
                command.getAction()
        );
        
        // 设置可选属性
        if (command.getType() != null) {
            permission.setType(command.getType());
        }
        if (command.getDescription() != null) {
            permission.setDescription(command.getDescription());
        }
        if (command.getAbacConditions() != null) {
            permission.setAbacConditions(command.getAbacConditions());
        }
        
        // 生成ID
        permission.setPermissionId(UUID.randomUUID().toString());

        Permission savedPermission = permissionRepository.save(permission);
        return convertToView(savedPermission);
    }

    /**
     * 更新权限
     */
    @Transactional
    public PermissionDto.PermissionView updatePermission(String permissionId, PermissionDto.UpdatePermissionCommand command) {
        log.info("更新权限: id={}", permissionId);

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new IllegalArgumentException("权限不存在: " + permissionId));

        permission.update(
                command.getName(),
                command.getCode(),
                command.getResource(),
                command.getAction(),
                command.getType(),
                command.getDescription(),
                command.getAbacConditions()
        );

        Permission updatedPermission = permissionRepository.save(permission);
        return convertToView(updatedPermission);
    }

    /**
     * 删除权限
     */
    @Transactional
    public void deletePermission(String permissionId) {
        log.info("删除权限: id={}", permissionId);

        if (!permissionRepository.existsById(permissionId)) {
            throw new IllegalArgumentException("权限不存在: " + permissionId);
        }

        permissionRepository.delete(permissionId);
    }

    /**
     * 根据ID查找权限
     */
    @Transactional(readOnly = true)
    public Optional<PermissionDto.PermissionView> findPermissionById(String permissionId, String tenantId) {
        log.info("查找权限: id={}, tenantId={}", permissionId, tenantId);

        return permissionRepository.findById(permissionId)
                .filter(permission -> permission.belongsToTenant(tenantId))
                .map(this::convertToView);
    }

    /**
     * 分页查询权限
     */
    @Transactional(readOnly = true)
    public PageResult<PermissionDto.PermissionView> findPermissions(
            PageRequest pageRequest, String tenantId, String resource, String action) {
        log.info("分页查询权限: pageNum={}, pageSize={}, tenantId={}, resource={}, action={}", 
                pageRequest.getPageNum(), pageRequest.getPageSize(), tenantId, resource, action);

        Assert.hasText(tenantId, "租户ID不能为空");

        // 构建查询规格
        Specification<PermissionDbo> spec = (root, query, cb) -> {
            List<Predicate> predicates = new java.util.ArrayList<>();
            
            // 必须按租户ID过滤
            predicates.add(cb.equal(root.get("tenantId"), tenantId));
            
            if (StringUtils.hasText(resource)) {
                predicates.add(cb.like(cb.lower(root.get("resource")), "%" + resource.toLowerCase() + "%"));
            }
            
            if (StringUtils.hasText(action)) {
                predicates.add(cb.equal(root.get("action"), action));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        // 构建排序：按创建时间升序
        org.springframework.data.domain.Sort sort = org.springframework.data.domain.Sort.by("createdAt").ascending();
        
        Pageable pageable = org.springframework.data.domain.PageRequest.of(
                pageRequest.getPageNum() - 1, // JPA 页码从 0 开始
                pageRequest.getPageSize(),
                sort
        );

        Page<PermissionDbo> page = permissionJpaRepository.findAll(spec, pageable);
        
        List<PermissionDto.PermissionView> views = page.getContent().stream()
                .map(this::convertDboToView)
                .collect(Collectors.toList());

        return PageResult.of(page.getTotalElements(), pageRequest, views);
    }

    /**
     * 分配权限给角色
     */
    @Transactional
    public void assignRolePermission(String roleId, String permissionId, String tenantId) {
        log.info("分配权限给角色: roleId={}, permissionId={}, tenantId={}", roleId, permissionId, tenantId);

        // 验证权限是否存在且属于当前租户
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new IllegalArgumentException("权限不存在: " + permissionId));
        
        if (!permission.belongsToTenant(tenantId)) {
            throw new IllegalArgumentException("权限不属于当前租户");
        }

        rolePermissionRepository.assignPermission(roleId, permissionId, tenantId);
    }

    /**
     * 移除角色权限
     */
    @Transactional
    public void removeRolePermission(String roleId, String permissionId) {
        log.info("移除角色权限: roleId={}, permissionId={}", roleId, permissionId);

        rolePermissionRepository.removePermission(roleId, permissionId);
    }

    /**
     * 获取角色的权限列表
     */
    @Transactional(readOnly = true)
    public List<PermissionDto.PermissionView> getRolePermissions(String roleId, String tenantId) {
        log.info("获取角色权限列表: roleId={}, tenantId={}", roleId, tenantId);

        List<Permission> permissions = rolePermissionRepository.findPermissionsByRoleId(roleId, tenantId);
        return permissions.stream()
                .map(this::convertToView)
                .collect(Collectors.toList());
    }

    /**
     * 转换为视图对象
     */
    private PermissionDto.PermissionView convertToView(Permission permission) {
        return PermissionDto.PermissionView.builder()
                .permissionId(permission.getPermissionId())
                .tenantId(permission.getTenantId())
                .name(permission.getName())
                .code(permission.getCode())
                .resource(permission.getResource())
                .action(permission.getAction())
                .type(permission.getType())
                .description(permission.getDescription())
                .abacConditions(permission.getAbacConditions())
                .createdAt(permission.getCreatedAt())
                .updatedAt(permission.getUpdatedAt())
                .build();
    }

    /**
     * 从Dbo转换为视图对象
     */
    private PermissionDto.PermissionView convertDboToView(PermissionDbo dbo) {
        return PermissionDto.PermissionView.builder()
                .permissionId(dbo.getPermissionId())
                .tenantId(dbo.getTenantId())
                .name(dbo.getName())
                .code(dbo.getCode())
                .resource(dbo.getResource())
                .action(dbo.getAction())
                .type(dbo.getType() != null ? Permission.PermissionType.valueOf(dbo.getType()) : Permission.PermissionType.FUNCTIONAL)
                .description(dbo.getDescription())
                .abacConditions(dbo.getAbacConditions())
                .createdAt(dbo.getCreatedAt())
                .updatedAt(dbo.getUpdatedAt())
                .build();
    }
}


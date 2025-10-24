package com.aixone.tech.auth.authorization.application.service;

import com.aixone.tech.auth.authorization.application.dto.CreateRoleRequest;
import com.aixone.tech.auth.authorization.application.dto.RoleResponse;
import com.aixone.tech.auth.authorization.application.dto.UpdateRoleRequest;
import com.aixone.tech.auth.authorization.domain.model.Permission;
import com.aixone.tech.auth.authorization.domain.model.Role;
import com.aixone.tech.auth.authorization.domain.repository.PermissionRepository;
import com.aixone.tech.auth.authorization.domain.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 角色管理应用服务
 */
@Service
@Transactional
public class RoleManagementApplicationService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleManagementApplicationService(RoleRepository roleRepository, 
                                          PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    /**
     * 创建角色
     */
    public RoleResponse createRole(CreateRoleRequest request) {
        // 1. 验证角色名称在租户内是否唯一
        if (roleRepository.existsByTenantIdAndName(request.getTenantId(), request.getName())) {
            throw new IllegalArgumentException("角色名称在租户内已存在");
        }

        // 2. 验证权限是否存在
        List<Permission> permissions = permissionRepository.findByTenantIdAndPermissionIdIn(
                request.getTenantId(), request.getPermissionIds());
        if (permissions.size() != request.getPermissionIds().size()) {
            throw new IllegalArgumentException("部分权限不存在");
        }

        // 3. 创建角色
        String roleId = UUID.randomUUID().toString();
        Role role = new Role(
            roleId,
            request.getTenantId(),
            request.getName(),
            request.getDescription(),
            request.getPermissionIds()
        );

        Role savedRole = roleRepository.save(role);

        // 4. 返回响应
        return new RoleResponse(
            savedRole.getRoleId(),
            savedRole.getTenantId(),
            savedRole.getName(),
            savedRole.getDescription(),
            savedRole.getPermissionIds(),
            savedRole.getCreatedAt(),
            savedRole.getUpdatedAt()
        );
    }

    /**
     * 更新角色
     */
    public RoleResponse updateRole(String tenantId, String roleId, UpdateRoleRequest request) {
        // 1. 查找角色
        Role role = roleRepository.findByTenantIdAndRoleId(tenantId, roleId);
        if (role == null) {
            throw new IllegalArgumentException("角色不存在");
        }

        // 2. 验证角色名称在租户内是否唯一（排除自己）
        if (!role.getName().equals(request.getName()) && 
            roleRepository.existsByTenantIdAndName(tenantId, request.getName())) {
            throw new IllegalArgumentException("角色名称在租户内已存在");
        }

        // 3. 验证权限是否存在
        List<Permission> permissions = permissionRepository.findByTenantIdAndPermissionIdIn(
                tenantId, request.getPermissionIds());
        if (permissions.size() != request.getPermissionIds().size()) {
            throw new IllegalArgumentException("部分权限不存在");
        }

        // 4. 更新角色
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        role.setPermissionIds(request.getPermissionIds());
        role.setUpdatedAt(LocalDateTime.now());

        Role savedRole = roleRepository.save(role);

        // 5. 返回响应
        return new RoleResponse(
            savedRole.getRoleId(),
            savedRole.getTenantId(),
            savedRole.getName(),
            savedRole.getDescription(),
            savedRole.getPermissionIds(),
            savedRole.getCreatedAt(),
            savedRole.getUpdatedAt()
        );
    }

    /**
     * 删除角色
     */
    public void deleteRole(String tenantId, String roleId) {
        // 1. 查找角色
        Role role = roleRepository.findByTenantIdAndRoleId(tenantId, roleId);
        if (role == null) {
            throw new IllegalArgumentException("角色不存在");
        }

        // 2. 检查是否有用户使用该角色
        // TODO: 这里应该检查用户角色关联表，暂时跳过

        // 3. 删除角色
        roleRepository.deleteByTenantIdAndRoleId(tenantId, roleId);
    }

    /**
     * 查询角色详情
     */
    @Transactional(readOnly = true)
    public RoleResponse getRole(String tenantId, String roleId) {
        Role role = roleRepository.findByTenantIdAndRoleId(tenantId, roleId);
        if (role == null) {
            throw new IllegalArgumentException("角色不存在");
        }

        return new RoleResponse(
            role.getRoleId(),
            role.getTenantId(),
            role.getName(),
            role.getDescription(),
            role.getPermissionIds(),
            role.getCreatedAt(),
            role.getUpdatedAt()
        );
    }

    /**
     * 查询租户下的所有角色
     */
    @Transactional(readOnly = true)
    public List<RoleResponse> getRoles(String tenantId) {
        List<Role> roles = roleRepository.findByTenantId(tenantId);
        
        return roles.stream()
                .map(role -> new RoleResponse(
                    role.getRoleId(),
                    role.getTenantId(),
                    role.getName(),
                    role.getDescription(),
                    role.getPermissionIds(),
                    role.getCreatedAt(),
                    role.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }
}

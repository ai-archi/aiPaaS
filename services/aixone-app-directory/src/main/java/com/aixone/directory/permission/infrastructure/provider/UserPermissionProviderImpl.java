package com.aixone.directory.permission.infrastructure.provider;

import com.aixone.directory.permission.domain.aggregate.Permission;
import com.aixone.directory.permission.domain.repository.PermissionRepository;
import com.aixone.directory.permission.domain.repository.RolePermissionRepository;
import com.aixone.directory.user.domain.repository.UserRepository;
import com.aixone.directory.user.domain.aggregate.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户权限提供者实现
 * 为权限决策引擎提供用户角色和权限信息
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserPermissionProviderImpl {

    private final UserRepository userRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;

    /**
     * 获取用户角色ID列表
     * 
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @return 角色ID列表
     */
    public List<String> getUserRoles(String userId, String tenantId) {
        log.debug("获取用户角色: userId={}, tenantId={}", userId, tenantId);

        User user = userRepository.findById(userId)
                .orElse(null);

        if (user == null || !user.getTenantId().equals(tenantId)) {
            log.warn("用户不存在或不属于当前租户: userId={}, tenantId={}", userId, tenantId);
            return List.of();
        }

        // 从User实体获取角色ID列表
        // 注意：User实体中的roleIds是Set<String>，存储的是角色ID
        Set<String> roleIds = user.getRoleIds();
        List<String> roleList = roleIds != null ? new java.util.ArrayList<>(roleIds) : List.of();

        log.debug("用户角色数量: {}", roleList.size());
        return roleList;
    }

    /**
     * 获取用户权限列表
     * 
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @return 权限列表
     */
    public List<Permission> getUserPermissions(String userId, String tenantId) {
        log.debug("获取用户权限: userId={}, tenantId={}", userId, tenantId);

        List<String> roleIds = getUserRoles(userId, tenantId);
        if (roleIds.isEmpty()) {
            return List.of();
        }

        // 获取所有角色的权限
        Set<String> permissionIds = roleIds.stream()
                .flatMap(roleId -> rolePermissionRepository.findPermissionIdsByRoleId(roleId, tenantId).stream())
                .collect(Collectors.toSet());

        // 转换为Permission对象
        List<Permission> permissions = permissionIds.stream()
                .map(permissionId -> permissionRepository.findById(permissionId))
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .collect(Collectors.toList());

        log.debug("用户权限数量: {}", permissions.size());
        return permissions;
    }

    /**
     * 获取角色权限列表
     * 
     * @param roleId 角色ID
     * @param tenantId 租户ID
     * @return 权限列表
     */
    public List<Permission> getRolePermissions(String roleId, String tenantId) {
        log.debug("获取角色权限: roleId={}, tenantId={}", roleId, tenantId);

        return rolePermissionRepository.findPermissionsByRoleId(roleId, tenantId);
    }
}


package com.aixone.directory.permission.domain.service;

import com.aixone.directory.permission.domain.aggregate.Permission;
import com.aixone.directory.permission.domain.repository.PermissionRepository;
import com.aixone.directory.permission.domain.repository.RolePermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * RBAC权限决策引擎
 * 基于角色的访问控制（Role-Based Access Control）
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RbacDecisionEngine {

    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;

    /**
     * 检查用户是否有指定权限（通过角色）
     * 
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @param roleIds 用户角色ID列表
     * @param resource 资源标识
     * @param action 操作标识
     * @return 是否有权限
     */
    public boolean checkPermission(String userId, String tenantId, List<String> roleIds, String resource, String action) {
        log.debug("RBAC权限检查: userId={}, tenantId={}, roleIds={}, resource={}, action={}", 
                userId, tenantId, roleIds, resource, action);

        if (roleIds == null || roleIds.isEmpty()) {
            log.debug("用户没有角色，权限检查失败");
            return false;
        }

        // 查找权限
        Permission permission = permissionRepository.findByTenantIdAndResourceAndAction(tenantId, resource, action)
                .orElse(null);

        if (permission == null) {
            log.debug("权限不存在: resource={}, action={}", resource, action);
            return false;
        }

        // 检查用户角色是否拥有该权限
        for (String roleId : roleIds) {
            if (rolePermissionRepository.hasPermission(roleId, permission.getPermissionId())) {
                log.debug("用户通过角色 {} 拥有权限: resource={}, action={}", roleId, resource, action);
                return true;
            }
        }

        log.debug("用户角色都不拥有该权限: resource={}, action={}", resource, action);
        return false;
    }

    /**
     * 检查用户是否有指定权限标识
     * 
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @param roleIds 用户角色ID列表
     * @param permissionIdentifier 权限标识（格式：{resource}:{action}）
     * @return 是否有权限
     */
    public boolean checkPermissionByIdentifier(String userId, String tenantId, List<String> roleIds, String permissionIdentifier) {
        log.debug("RBAC权限检查（通过权限标识）: userId={}, tenantId={}, roleIds={}, permissionIdentifier={}", 
                userId, tenantId, roleIds, permissionIdentifier);

        if (permissionIdentifier == null || !permissionIdentifier.contains(":")) {
            log.warn("无效的权限标识格式: {}", permissionIdentifier);
            return false;
        }

        String[] parts = permissionIdentifier.split(":", 2);
        if (parts.length != 2) {
            log.warn("权限标识格式错误: {}", permissionIdentifier);
            return false;
        }

        return checkPermission(userId, tenantId, roleIds, parts[0], parts[1]);
    }

    /**
     * 获取用户的所有权限（通过角色）
     * 
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @param roleIds 用户角色ID列表
     * @return 权限标识列表（格式：{resource}:{action}）
     */
    public Set<String> getUserPermissions(String userId, String tenantId, List<String> roleIds) {
        log.debug("获取用户权限: userId={}, tenantId={}, roleIds={}", userId, tenantId, roleIds);

        if (roleIds == null || roleIds.isEmpty()) {
            return Set.of();
        }

        // 获取所有角色的权限
        Set<String> permissions = roleIds.stream()
                .flatMap(roleId -> rolePermissionRepository.findPermissionIdsByRoleId(roleId, tenantId).stream())
                .distinct()
                .map(permissionId -> permissionRepository.findById(permissionId))
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .map(Permission::getPermissionIdentifier)
                .collect(Collectors.toSet());

        log.debug("用户权限数量: {}", permissions.size());
        return permissions;
    }

    /**
     * 批量检查权限
     * 
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @param roleIds 用户角色ID列表
     * @param permissions 权限列表（格式：{resource}:{action}）
     * @return 权限检查结果映射（权限标识 -> 是否有权限）
     */
    public java.util.Map<String, Boolean> checkPermissions(String userId, String tenantId, List<String> roleIds, List<String> permissions) {
        log.debug("批量RBAC权限检查: userId={}, tenantId={}, roleIds={}, permissions={}", 
                userId, tenantId, roleIds, permissions);

        return permissions.stream()
                .collect(Collectors.toMap(
                        permission -> permission,
                        permission -> checkPermissionByIdentifier(userId, tenantId, roleIds, permission)
                ));
    }
}


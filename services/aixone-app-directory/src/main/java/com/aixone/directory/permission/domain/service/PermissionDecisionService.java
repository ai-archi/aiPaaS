package com.aixone.directory.permission.domain.service;

import com.aixone.directory.permission.domain.aggregate.Permission;
import com.aixone.directory.permission.domain.repository.PermissionRepository;
import com.aixone.directory.permission.infrastructure.provider.UserPermissionProviderImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限决策服务
 * 提供RBAC+ABAC混合权限决策能力
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionDecisionService {

    private final RbacDecisionEngine rbacDecisionEngine;
    private final AbacDecisionEngine abacDecisionEngine;
    private final PermissionRepository permissionRepository;
    private final UserPermissionProviderImpl userPermissionProvider;

    /**
     * 检查用户是否有指定权限（RBAC+ABAC混合决策）
     * 
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @param resource 资源标识
     * @param action 操作标识
     * @param context 上下文信息（可选，用于ABAC决策）
     * @return 是否有权限
     */
    public boolean checkPermission(
            String userId,
            String tenantId,
            String resource,
            String action,
            PermissionContext context) {
        
        log.debug("权限决策: userId={}, tenantId={}, resource={}, action={}", 
                userId, tenantId, resource, action);

        // 1. 获取用户角色
        List<String> roleIds = userPermissionProvider.getUserRoles(userId, tenantId);
        if (roleIds.isEmpty()) {
            log.debug("用户没有角色，权限检查失败");
            return false;
        }

        // 2. 查找权限
        Permission permission = permissionRepository.findByTenantIdAndResourceAndAction(tenantId, resource, action)
                .orElse(null);

        if (permission == null) {
            log.debug("权限不存在: resource={}, action={}", resource, action);
            return false;
        }

        // 3. 执行RBAC权限决策
        boolean rbacResult = rbacDecisionEngine.checkPermission(userId, tenantId, roleIds, resource, action);
        if (!rbacResult) {
            log.debug("RBAC权限检查失败");
            return false;
        }

        // 4. 如果权限有ABAC条件，执行ABAC权限决策
        if (permission.hasAbacConditions()) {
            Map<String, Object> userAttributes = context != null ? context.getUserAttributes() : null;
            Map<String, Object> resourceAttributes = context != null ? context.getResourceAttributes() : null;
            Map<String, Object> environmentAttributes = context != null ? context.getEnvironmentAttributes() : null;

            boolean abacResult = abacDecisionEngine.checkPermission(
                    userId, tenantId, permission, userAttributes, resourceAttributes, environmentAttributes);

            if (!abacResult) {
                log.debug("ABAC权限检查失败");
                return false;
            }
        }

        log.debug("权限检查通过: resource={}, action={}", resource, action);
        return true;
    }

    /**
     * 检查用户是否有指定权限标识
     * 
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @param permissionIdentifier 权限标识（格式：{resource}:{action}）
     * @param context 上下文信息（可选）
     * @return 是否有权限
     */
    public boolean checkPermissionByIdentifier(
            String userId,
            String tenantId,
            String permissionIdentifier,
            PermissionContext context) {
        
        log.debug("权限决策（通过权限标识）: userId={}, tenantId={}, permissionIdentifier={}", 
                userId, tenantId, permissionIdentifier);

        if (permissionIdentifier == null || !permissionIdentifier.contains(":")) {
            log.warn("无效的权限标识格式: {}", permissionIdentifier);
            return false;
        }

        String[] parts = permissionIdentifier.split(":", 2);
        if (parts.length != 2) {
            log.warn("权限标识格式错误: {}", permissionIdentifier);
            return false;
        }

        return checkPermission(userId, tenantId, parts[0], parts[1], context);
    }

    /**
     * 批量检查权限
     * 
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @param permissions 权限列表（格式：{resource}:{action}）
     * @param context 上下文信息（可选）
     * @return 权限检查结果映射（权限标识 -> 是否有权限）
     */
    public Map<String, Boolean> checkPermissions(
            String userId,
            String tenantId,
            List<String> permissions,
            PermissionContext context) {
        
        log.debug("批量权限决策: userId={}, tenantId={}, permissions={}", userId, tenantId, permissions);

        return permissions.stream()
                .collect(Collectors.toMap(
                        permission -> permission,
                        permission -> checkPermissionByIdentifier(userId, tenantId, permission, context)
                ));
    }

    /**
     * 获取用户有效权限列表
     * 
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @return 权限标识列表（格式：{resource}:{action}）
     */
    public Set<String> getUserEffectivePermissions(String userId, String tenantId) {
        log.debug("获取用户有效权限: userId={}, tenantId={}", userId, tenantId);

        List<String> roleIds = userPermissionProvider.getUserRoles(userId, tenantId);
        return rbacDecisionEngine.getUserPermissions(userId, tenantId, roleIds);
    }

    /**
     * 权限决策上下文
     * 用于传递ABAC决策所需的属性信息
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class PermissionContext {
        /**
         * 用户ABAC属性
         * 例如：{"department": "IT", "level": 5, "roles": ["admin"]}
         */
        private Map<String, Object> userAttributes;

        /**
         * 资源ABAC属性
         * 例如：{"owner": "user123", "category": "sensitive"}
         */
        private Map<String, Object> resourceAttributes;

        /**
         * 环境ABAC属性
         * 例如：{"time": "09:00", "ip": "192.168.1.1", "location": "office"}
         */
        private Map<String, Object> environmentAttributes;
    }
}


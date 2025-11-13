package com.aixone.directory.permission.application;

import com.aixone.directory.permission.domain.service.PermissionDecisionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 权限校验服务
 * 提供权限校验接口，供业务服务调用
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionValidationService {

    private final PermissionDecisionService permissionDecisionService;

    /**
     * 校验权限
     * 
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @param resource 资源标识
     * @param action 操作标识
     * @param context 上下文信息（可选，用于ABAC决策）
     * @return 是否有权限
     */
    @Transactional(readOnly = true)
    public boolean validatePermission(
            String userId,
            String tenantId,
            String resource,
            String action,
            PermissionDecisionService.PermissionContext context) {
        
        log.info("权限校验: userId={}, tenantId={}, resource={}, action={}", userId, tenantId, resource, action);
        
        return permissionDecisionService.checkPermission(userId, tenantId, resource, action, context);
    }

    /**
     * 批量校验权限
     * 
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @param permissions 权限列表（格式：{resource}:{action}）
     * @param context 上下文信息（可选）
     * @return 权限校验结果映射（权限标识 -> 是否有权限）
     */
    @Transactional(readOnly = true)
    public Map<String, Boolean> validatePermissions(
            String userId,
            String tenantId,
            List<String> permissions,
            PermissionDecisionService.PermissionContext context) {
        
        log.info("批量权限校验: userId={}, tenantId={}, permissions={}", userId, tenantId, permissions);
        
        return permissionDecisionService.checkPermissions(userId, tenantId, permissions, context);
    }

    /**
     * 获取用户有效权限列表
     * 
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @return 权限标识列表（格式：{resource}:{action}）
     */
    @Transactional(readOnly = true)
    public Set<String> getUserEffectivePermissions(String userId, String tenantId) {
        log.info("获取用户有效权限: userId={}, tenantId={}", userId, tenantId);
        
        return permissionDecisionService.getUserEffectivePermissions(userId, tenantId);
    }
}


package com.aixone.tech.auth.authorization.application.service;

import com.aixone.tech.auth.authorization.domain.service.PermissionDomainService;
import com.aixone.tech.auth.authorization.application.dto.CheckPermissionRequest;
import com.aixone.tech.auth.authorization.application.dto.CheckPermissionResponse;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 授权应用服务
 */
@Service
public class AuthorizationApplicationService {
    private final PermissionDomainService permissionDomainService;

    public AuthorizationApplicationService(PermissionDomainService permissionDomainService) {
        this.permissionDomainService = permissionDomainService;
    }

    /**
     * 检查用户权限
     */
    public CheckPermissionResponse checkPermission(CheckPermissionRequest request) {
        String tenantId = request.getTenantId();
        String userId = request.getUserId();
        String resource = request.getResource();
        String action = request.getAction();
        Map<String, Object> context = request.getContext();

        // 1. RBAC校验
        boolean hasRbacPermission = permissionDomainService.hasPermission(tenantId, userId, resource, action);
        if (!hasRbacPermission) {
            return new CheckPermissionResponse(false, "用户没有访问该资源的权限");
        }

        // 2. ABAC校验（仅当有上下文时进行）
        if (context != null && !context.isEmpty()) {
            boolean hasAbacPermission = permissionDomainService.checkAbacPolicy(tenantId, userId, resource, action, context);
            if (!hasAbacPermission) {
                return new CheckPermissionResponse(false, "用户不满足访问该资源的条件");
            }
        }

        return new CheckPermissionResponse(true, "权限校验通过");
    }
}

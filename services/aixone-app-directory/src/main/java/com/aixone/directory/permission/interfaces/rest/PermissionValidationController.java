package com.aixone.directory.permission.interfaces.rest;

import com.aixone.common.api.ApiResponse;
import com.aixone.common.session.SessionContext;
import com.aixone.directory.permission.application.PermissionValidationDto;
import com.aixone.directory.permission.application.PermissionValidationService;
import com.aixone.directory.permission.domain.service.PermissionDecisionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限校验控制器
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionValidationController {

    private final PermissionValidationService permissionValidationService;

    /**
     * 单权限校验
     */
    @PostMapping("/check")
    public ResponseEntity<ApiResponse<PermissionValidationDto.CheckPermissionResponse>> checkPermission(
            @Valid @RequestBody PermissionValidationDto.CheckPermissionRequest request) {
        log.info("权限校验请求: userId={}, resource={}, action={}", 
                request.getUserId(), request.getResource(), request.getAction());

        // 从token获取租户ID（如果请求中没有提供）
        String tenantId = SessionContext.getTenantId();
        if (!StringUtils.hasText(tenantId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供有效的租户信息"));
        }

        // 如果请求中没有提供userId，从token获取
        String userId = request.getUserId();
        if (!StringUtils.hasText(userId)) {
            userId = SessionContext.getUserId();
            if (!StringUtils.hasText(userId)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "未提供有效的用户信息"));
            }
        }

        try {
            // 构建权限决策上下文
            PermissionDecisionService.PermissionContext context = PermissionDecisionService.PermissionContext.builder()
                    .userAttributes(request.getUserAttributes())
                    .resourceAttributes(request.getResourceAttributes())
                    .environmentAttributes(request.getEnvironmentAttributes())
                    .build();

            boolean hasPermission = permissionValidationService.validatePermission(
                    userId, tenantId, request.getResource(), request.getAction(), context);

            PermissionValidationDto.CheckPermissionResponse response = PermissionValidationDto.CheckPermissionResponse.builder()
                    .hasPermission(hasPermission)
                    .build();

            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("权限校验失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "权限校验失败: " + e.getMessage()));
        }
    }

    /**
     * 批量权限校验
     */
    @PostMapping("/check-batch")
    public ResponseEntity<ApiResponse<PermissionValidationDto.CheckPermissionsResponse>> checkPermissions(
            @Valid @RequestBody PermissionValidationDto.CheckPermissionsRequest request) {
        log.info("批量权限校验请求: userId={}, permissions={}", request.getUserId(), request.getPermissions());

        // 从token获取租户ID（如果请求中没有提供）
        String tenantId = SessionContext.getTenantId();
        if (!StringUtils.hasText(tenantId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供有效的租户信息"));
        }

        // 如果请求中没有提供userId，从token获取
        String userId = request.getUserId();
        if (!StringUtils.hasText(userId)) {
            userId = SessionContext.getUserId();
            if (!StringUtils.hasText(userId)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "未提供有效的用户信息"));
            }
        }

        try {
            // 构建权限决策上下文
            PermissionDecisionService.PermissionContext context = PermissionDecisionService.PermissionContext.builder()
                    .userAttributes(request.getUserAttributes())
                    .resourceAttributes(request.getResourceAttributes())
                    .environmentAttributes(request.getEnvironmentAttributes())
                    .build();

            Map<String, Boolean> results = permissionValidationService.validatePermissions(
                    userId, tenantId, request.getPermissions(), context);

            // 转换为响应格式
            List<PermissionValidationDto.PermissionCheckResult> resultList = results.entrySet().stream()
                    .map(entry -> PermissionValidationDto.PermissionCheckResult.builder()
                            .permission(entry.getKey())
                            .hasPermission(entry.getValue())
                            .build())
                    .collect(Collectors.toList());

            PermissionValidationDto.CheckPermissionsResponse response = PermissionValidationDto.CheckPermissionsResponse.builder()
                    .results(resultList)
                    .build();

            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("批量权限校验失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "批量权限校验失败: " + e.getMessage()));
        }
    }

    /**
     * 获取用户有效权限列表
     */
    @GetMapping("/users/{userId}/permissions")
    public ResponseEntity<ApiResponse<Set<String>>> getUserPermissions(@PathVariable String userId) {
        log.info("获取用户权限列表: userId={}", userId);

        // 从token获取租户ID
        String tenantId = SessionContext.getTenantId();
        if (!StringUtils.hasText(tenantId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供有效的租户信息"));
        }

        try {
            Set<String> permissions = permissionValidationService.getUserEffectivePermissions(userId, tenantId);
            return ResponseEntity.ok(ApiResponse.success(permissions));
        } catch (Exception e) {
            log.error("获取用户权限列表失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "获取用户权限列表失败: " + e.getMessage()));
        }
    }
}


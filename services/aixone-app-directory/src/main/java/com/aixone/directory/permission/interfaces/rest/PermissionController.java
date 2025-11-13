package com.aixone.directory.permission.interfaces.rest;

import com.aixone.common.api.ApiResponse;
import com.aixone.common.api.PageRequest;
import com.aixone.common.api.PageResult;
import com.aixone.common.session.SessionContext;
import com.aixone.directory.permission.application.PermissionApplicationService;
import com.aixone.directory.permission.application.PermissionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 权限管理控制器
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/permissions/data")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionApplicationService permissionApplicationService;

    /**
     * 获取权限数据列表（分页）
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResult<PermissionDto.PermissionView>>> getPermissions(
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String resource,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String order) {
        
        // 从token获取租户ID
        String tenantId = SessionContext.getTenantId();
        if (!StringUtils.hasText(tenantId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供有效的租户信息"));
        }
        
        // 兼容两种参数名：优先使用 pageNum/pageSize，如果未提供则使用 page/limit
        int actualPageNum = (pageNum != null) ? pageNum : ((page != null) ? page : 1);
        int actualPageSize = (pageSize != null) ? pageSize : ((limit != null) ? limit : 20);
        
        log.info("查询权限数据列表: pageNum={}, pageSize={}, tenantId={}, resource={}, action={}, order={}", 
                actualPageNum, actualPageSize, tenantId, resource, action, order);
        
        // 解析排序参数（格式：field,direction，例如：createdAt,asc）
        String sortBy = null;
        String sortDirection = "asc";
        if (order != null && !order.isEmpty()) {
            String[] orderParts = order.split(",");
            if (orderParts.length >= 1) {
                sortBy = orderParts[0].trim();
            }
            if (orderParts.length >= 2) {
                sortDirection = orderParts[1].trim().toLowerCase();
                if ("ascending".equals(sortDirection)) {
                    sortDirection = "asc";
                } else if ("descending".equals(sortDirection)) {
                    sortDirection = "desc";
                }
            }
        }
        
        try {
            PageRequest pageRequest = new PageRequest(actualPageNum, actualPageSize, sortBy, sortDirection);
            PageResult<PermissionDto.PermissionView> result = permissionApplicationService.findPermissions(
                    pageRequest, tenantId, resource, action);
            
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            log.error("查询权限数据列表失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "查询权限数据列表失败: " + e.getMessage()));
        }
    }

    /**
     * 获取权限数据详情
     */
    @GetMapping("/{permissionId}")
    public ResponseEntity<ApiResponse<PermissionDto.PermissionView>> getPermissionById(
            @PathVariable String permissionId) {
        log.info("获取权限数据详情: id={}", permissionId);

        // 从token获取租户ID
        String tenantId = SessionContext.getTenantId();
        if (!StringUtils.hasText(tenantId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供有效的租户信息"));
        }

        return permissionApplicationService.findPermissionById(permissionId, tenantId)
                .map(permission -> ResponseEntity.ok(ApiResponse.success(permission)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.notFound("权限不存在或不属于当前租户")));
    }

    /**
     * 创建权限数据
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PermissionDto.PermissionView>> createPermission(
            @Valid @RequestBody PermissionDto.CreatePermissionCommand command) {
        log.info("创建权限数据: name={}", command.getName());

        // 从token获取租户ID并设置到command中
        String tenantId = SessionContext.getTenantId();
        if (!StringUtils.hasText(tenantId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供有效的租户信息"));
        }
        command.setTenantId(tenantId);

        try {
            PermissionDto.PermissionView result = permissionApplicationService.createPermission(command);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(result, "权限创建成功"));
        } catch (IllegalArgumentException e) {
            log.warn("创建权限数据失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(e.getMessage()));
        } catch (Exception e) {
            log.error("创建权限数据失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "创建权限数据失败: " + e.getMessage()));
        }
    }

    /**
     * 更新权限数据
     */
    @PutMapping("/{permissionId}")
    public ResponseEntity<ApiResponse<PermissionDto.PermissionView>> updatePermission(
            @PathVariable String permissionId,
            @Valid @RequestBody PermissionDto.UpdatePermissionCommand command) {
        log.info("更新权限数据: id={}", permissionId);

        try {
            PermissionDto.PermissionView result = permissionApplicationService.updatePermission(permissionId, command);
            return ResponseEntity.ok(ApiResponse.success(result, "权限更新成功"));
        } catch (IllegalArgumentException e) {
            log.warn("更新权限数据失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(e.getMessage()));
        } catch (Exception e) {
            log.error("更新权限数据失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "更新权限数据失败: " + e.getMessage()));
        }
    }

    /**
     * 删除权限数据
     */
    @DeleteMapping("/{permissionId}")
    public ResponseEntity<ApiResponse<Void>> deletePermission(@PathVariable String permissionId) {
        log.info("删除权限数据: id={}", permissionId);

        try {
            permissionApplicationService.deletePermission(permissionId);
            return ResponseEntity.ok(ApiResponse.success(null, "权限删除成功"));
        } catch (IllegalArgumentException e) {
            log.warn("删除权限数据失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(e.getMessage()));
        } catch (Exception e) {
            log.error("删除权限数据失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "删除权限数据失败: " + e.getMessage()));
        }
    }

    /**
     * 分配权限给角色
     */
    @PostMapping("/roles/{roleId}/permissions")
    public ResponseEntity<ApiResponse<Void>> assignRolePermission(
            @PathVariable String roleId,
            @RequestParam String permissionId) {
        log.info("分配权限给角色: roleId={}, permissionId={}", roleId, permissionId);

        // 从token获取租户ID
        String tenantId = SessionContext.getTenantId();
        if (!StringUtils.hasText(tenantId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供有效的租户信息"));
        }

        try {
            permissionApplicationService.assignRolePermission(roleId, permissionId, tenantId);
            return ResponseEntity.ok(ApiResponse.success(null, "权限分配成功"));
        } catch (IllegalArgumentException e) {
            log.warn("分配权限给角色失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(e.getMessage()));
        } catch (Exception e) {
            log.error("分配权限给角色失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "分配权限给角色失败: " + e.getMessage()));
        }
    }

    /**
     * 移除角色权限
     */
    @DeleteMapping("/roles/{roleId}/permissions/{permissionId}")
    public ResponseEntity<ApiResponse<Void>> removeRolePermission(
            @PathVariable String roleId,
            @PathVariable String permissionId) {
        log.info("移除角色权限: roleId={}, permissionId={}", roleId, permissionId);

        try {
            permissionApplicationService.removeRolePermission(roleId, permissionId);
            return ResponseEntity.ok(ApiResponse.success(null, "权限移除成功"));
        } catch (IllegalArgumentException e) {
            log.warn("移除角色权限失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(e.getMessage()));
        } catch (Exception e) {
            log.error("移除角色权限失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "移除角色权限失败: " + e.getMessage()));
        }
    }

    /**
     * 获取角色的权限列表
     */
    @GetMapping("/roles/{roleId}/permissions")
    public ResponseEntity<ApiResponse<List<PermissionDto.PermissionView>>> getRolePermissions(
            @PathVariable String roleId) {
        log.info("获取角色权限列表: roleId={}", roleId);

        // 从token获取租户ID
        String tenantId = SessionContext.getTenantId();
        if (!StringUtils.hasText(tenantId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供有效的租户信息"));
        }

        try {
            List<PermissionDto.PermissionView> result = permissionApplicationService.getRolePermissions(roleId, tenantId);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            log.error("获取角色权限列表失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "获取角色权限列表失败: " + e.getMessage()));
        }
    }
}


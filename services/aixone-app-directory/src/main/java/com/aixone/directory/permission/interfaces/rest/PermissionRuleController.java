package com.aixone.directory.permission.interfaces.rest;

import com.aixone.common.api.ApiResponse;
import com.aixone.common.api.PageRequest;
import com.aixone.common.api.PageResult;
import com.aixone.common.session.SessionContext;
import com.aixone.directory.permission.application.PermissionRuleApplicationService;
import com.aixone.directory.permission.application.PermissionRuleDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 权限规则管理控制器
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionRuleController {

    private final PermissionRuleApplicationService permissionRuleApplicationService;

    /**
     * 获取权限规则列表（分页）
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResult<PermissionRuleDto.PermissionRuleView>>> getPermissionRules(
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String pattern,
            @RequestParam(required = false) String method,
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
        
        log.info("查询权限规则列表: pageNum={}, pageSize={}, tenantId={}, pattern={}, method={}, order={}", 
                actualPageNum, actualPageSize, tenantId, pattern, method, order);
        
        // 解析排序参数（格式：field,direction，例如：priority,desc）
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
            PageResult<PermissionRuleDto.PermissionRuleView> result = permissionRuleApplicationService.findPermissionRules(
                    pageRequest, tenantId, pattern, method);
            
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            log.error("查询权限规则列表失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "查询权限规则列表失败: " + e.getMessage()));
        }
    }

    /**
     * 获取权限规则详情
     */
    @GetMapping("/{permissionId}")
    public ResponseEntity<ApiResponse<PermissionRuleDto.PermissionRuleView>> getPermissionRuleById(
            @PathVariable String permissionId) {
        log.info("获取权限规则详情: id={}", permissionId);

        // 从token获取租户ID
        String tenantId = SessionContext.getTenantId();
        if (!StringUtils.hasText(tenantId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供有效的租户信息"));
        }

        return permissionRuleApplicationService.findPermissionRuleById(permissionId, tenantId)
                .map(rule -> ResponseEntity.ok(ApiResponse.success(rule)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.notFound("权限规则不存在或不属于当前租户")));
    }

    /**
     * 创建权限规则
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PermissionRuleDto.PermissionRuleView>> createPermissionRule(
            @Valid @RequestBody PermissionRuleDto.CreatePermissionRuleCommand command) {
        log.info("创建权限规则: pattern={}", command.getPattern());

        // 从token获取租户ID并设置到command中
        String tenantId = SessionContext.getTenantId();
        if (!StringUtils.hasText(tenantId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供有效的租户信息"));
        }
        command.setTenantId(tenantId);

        try {
            PermissionRuleDto.PermissionRuleView result = permissionRuleApplicationService.createPermissionRule(command);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(result, "权限规则创建成功"));
        } catch (IllegalArgumentException e) {
            log.warn("创建权限规则失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(e.getMessage()));
        } catch (Exception e) {
            log.error("创建权限规则失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "创建权限规则失败: " + e.getMessage()));
        }
    }

    /**
     * 更新权限规则
     */
    @PutMapping("/{permissionId}")
    public ResponseEntity<ApiResponse<PermissionRuleDto.PermissionRuleView>> updatePermissionRule(
            @PathVariable String permissionId,
            @Valid @RequestBody PermissionRuleDto.UpdatePermissionRuleCommand command) {
        log.info("更新权限规则: id={}", permissionId);

        try {
            PermissionRuleDto.PermissionRuleView result = permissionRuleApplicationService.updatePermissionRule(permissionId, command);
            return ResponseEntity.ok(ApiResponse.success(result, "权限规则更新成功"));
        } catch (IllegalArgumentException e) {
            log.warn("更新权限规则失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(e.getMessage()));
        } catch (Exception e) {
            log.error("更新权限规则失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "更新权限规则失败: " + e.getMessage()));
        }
    }

    /**
     * 删除权限规则
     */
    @DeleteMapping("/{permissionId}")
    public ResponseEntity<ApiResponse<Void>> deletePermissionRule(@PathVariable String permissionId) {
        log.info("删除权限规则: id={}", permissionId);

        try {
            permissionRuleApplicationService.deletePermissionRule(permissionId);
            return ResponseEntity.ok(ApiResponse.success(null, "权限规则删除成功"));
        } catch (IllegalArgumentException e) {
            log.warn("删除权限规则失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(e.getMessage()));
        } catch (Exception e) {
            log.error("删除权限规则失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "删除权限规则失败: " + e.getMessage()));
        }
    }
}


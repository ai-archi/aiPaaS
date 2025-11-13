package com.aixone.directory.organization.interfaces.rest;

import com.aixone.common.api.ApiResponse;
import com.aixone.common.api.PageRequest;
import com.aixone.common.api.PageResult;
import com.aixone.common.session.SessionContext;
import com.aixone.directory.organization.application.DepartmentApplicationService;
import com.aixone.directory.organization.application.dto.DepartmentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 部门查询 REST 控制器
 * 提供部门查询的 HTTP API
 */
@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
@Slf4j
public class DepartmentQueryController {

    private final DepartmentApplicationService departmentApplicationService;

    /**
     * 获取部门列表（分页，支持过滤）
     * 支持两种分页参数名：pageNum/pageSize 和 page/limit（兼容前端baTable）
     * 租户ID从token自动获取
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResult<DepartmentDto>>> getDepartments(
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String parentId) {
        
        // 从token获取租户ID
        String tenantId = SessionContext.getTenantId();
        if (!StringUtils.hasText(tenantId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供有效的租户信息"));
        }
        
        // 兼容两种参数名：优先使用 pageNum/pageSize，如果未提供则使用 page/limit
        int actualPageNum = (pageNum != null) ? pageNum : ((page != null) ? page : 1);
        int actualPageSize = (pageSize != null) ? pageSize : ((limit != null) ? limit : 20);
        
        log.info("查询部门列表: pageNum={}, pageSize={}, tenantId={}, name={}, orgId={}, parentId={}", 
                actualPageNum, actualPageSize, tenantId, name, orgId, parentId);
        
        try {
            PageRequest pageRequest = new PageRequest(actualPageNum, actualPageSize);
            PageResult<DepartmentDto> result = departmentApplicationService.findDepartments(
                    pageRequest, tenantId, name, orgId, parentId);
            
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            log.error("查询部门列表失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "查询部门列表失败: " + e.getMessage()));
        }
    }
}


package com.aixone.directory.organization.interfaces.rest;

import com.aixone.common.api.ApiResponse;
import com.aixone.common.api.PageRequest;
import com.aixone.common.api.PageResult;
import com.aixone.common.api.RowData;
import com.aixone.common.session.SessionContext;
import com.aixone.directory.organization.application.OrganizationApplicationService;
import com.aixone.directory.organization.application.dto.CreateOrganizationRequest;
import com.aixone.directory.organization.application.dto.OrganizationDto;
import com.aixone.directory.organization.application.dto.UpdateOrganizationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 组织 REST 控制器
 * 提供组织管理的 HTTP API
 */
@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
@Slf4j
public class OrganizationController {

    private final OrganizationApplicationService organizationApplicationService;

    /**
     * 获取组织列表（分页，支持过滤）
     * 支持两种分页参数名：pageNum/pageSize 和 page/limit（兼容前端baTable）
     * 租户ID从token自动获取
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResult<OrganizationDto>>> getOrganizations(
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String name) {
        
        // 从token获取租户ID
        String tenantId = SessionContext.getTenantId();
        if (!StringUtils.hasText(tenantId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供有效的租户信息"));
        }
        
        // 兼容两种参数名：优先使用 pageNum/pageSize，如果未提供则使用 page/limit
        int actualPageNum = (pageNum != null) ? pageNum : ((page != null) ? page : 1);
        int actualPageSize = (pageSize != null) ? pageSize : ((limit != null) ? limit : 20);
        
        log.info("查询组织列表: pageNum={}, pageSize={}, tenantId={}, name={}", 
                actualPageNum, actualPageSize, tenantId, name);
        
        try {
            PageRequest pageRequest = new PageRequest(actualPageNum, actualPageSize);
            PageResult<OrganizationDto> result = organizationApplicationService.findOrganizations(
                    pageRequest, tenantId, name);
            
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            log.error("查询组织列表失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "查询组织列表失败: " + e.getMessage()));
        }
    }

    /**
     * 获取组织详情
     * 租户ID从token自动获取
     */
    @GetMapping("/{orgId}")
    public ResponseEntity<ApiResponse<RowData<OrganizationDto>>> getOrganizationById(@PathVariable String orgId) {
        // 从token获取租户ID
        String tenantId = SessionContext.getTenantId();
        if (!StringUtils.hasText(tenantId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供有效的租户信息"));
        }
        
        log.info("获取组织详情: orgId={}, tenantId={}", orgId, tenantId);
        
        try {
            OrganizationDto organization = organizationApplicationService.getOrganizationById(orgId);
            // 验证组织属于当前租户
            if (!organization.getTenantId().equals(tenantId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(403, "组织不属于当前租户"));
            }
            // baTable期望的格式：{code: 200, data: {row: {...}}}
            RowData<OrganizationDto> rowData = new RowData<>();
            rowData.setRow(organization);
            return ResponseEntity.ok(ApiResponse.success(rowData));
        } catch (Exception e) {
            log.error("获取组织详情失败", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "组织不存在: " + e.getMessage()));
        }
    }

    /**
     * 创建组织
     * 租户ID从token自动获取并设置
     */
    @PostMapping
    public ResponseEntity<ApiResponse<OrganizationDto>> createOrganization(@RequestBody CreateOrganizationRequest request) {
        // 从token获取租户ID
        String tenantId = SessionContext.getTenantId();
        if (!StringUtils.hasText(tenantId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供有效的租户信息"));
        }
        
        // 设置租户ID（覆盖请求体中的tenantId，确保安全）
        request.setTenantId(tenantId);
        
        log.info("创建组织: name={}, tenantId={}", request.getName(), tenantId);
        
        try {
            OrganizationDto organization = organizationApplicationService.createOrganization(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(organization, "组织创建成功"));
        } catch (Exception e) {
            log.error("创建组织失败", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, "创建组织失败: " + e.getMessage()));
        }
    }

    /**
     * 更新组织
     * 租户ID从token自动获取，自动验证组织是否属于当前租户
     */
    @PutMapping("/{orgId}")
    public ResponseEntity<ApiResponse<OrganizationDto>> updateOrganization(
            @PathVariable String orgId,
            @RequestBody UpdateOrganizationRequest request) {
        // 从token获取租户ID
        String tenantId = SessionContext.getTenantId();
        if (!StringUtils.hasText(tenantId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供有效的租户信息"));
        }
        
        log.info("更新组织: orgId={}, tenantId={}, name={}", orgId, tenantId, request.getName());
        
        try {
            OrganizationDto organization = organizationApplicationService.updateOrganization(orgId, tenantId, request);
            return ResponseEntity.ok(ApiResponse.success(organization, "组织更新成功"));
        } catch (Exception e) {
            log.error("更新组织失败", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, "更新组织失败: " + e.getMessage()));
        }
    }

    /**
     * 删除组织
     * 租户ID从token自动获取，自动验证组织是否属于当前租户
     */
    @DeleteMapping("/{orgId}")
    public ResponseEntity<ApiResponse<Void>> deleteOrganization(@PathVariable String orgId) {
        // 从token获取租户ID
        String tenantId = SessionContext.getTenantId();
        if (!StringUtils.hasText(tenantId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供有效的租户信息"));
        }
        
        log.info("删除组织: orgId={}, tenantId={}", orgId, tenantId);
        
        try {
            organizationApplicationService.deleteOrganization(orgId, tenantId);
            return ResponseEntity.ok(ApiResponse.success(null, "组织删除成功"));
        } catch (Exception e) {
            log.error("删除组织失败", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, "删除组织失败: " + e.getMessage()));
        }
    }
}


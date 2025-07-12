package com.aixone.eventcenter.audit;

import com.aixone.eventcenter.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 审计日志接口
 * /api/audit/logs
 */
@RestController
@RequestMapping("/api/audit/logs")
public class AuditLogController {
    @Autowired
    private AuditLogService auditLogService;

    private static final String TENANT_HEADER = "X-Tenant-Id";

    /**
     * 查询所有审计日志
     */
    @GetMapping
    public ApiResponse<List<AuditLog>> getAllLogs(@RequestHeader(value = TENANT_HEADER, required = false) String tenantId) {
        if (tenantId == null || tenantId.isEmpty()) {
            return ApiResponse.error(40001, "缺少租户ID");
        }
        return ApiResponse.success(auditLogService.getAllLogs(tenantId));
    }

    /**
     * 按ID查询日志
     */
    @GetMapping("/{id}")
    public ApiResponse<AuditLog> getLogById(@RequestHeader(value = TENANT_HEADER, required = false) String tenantId, @PathVariable Long id) {
        if (tenantId == null || tenantId.isEmpty()) {
            return ApiResponse.error(40001, "缺少租户ID");
        }
        return auditLogService.getLogById(id, tenantId)
                .map(ApiResponse::success)
                .orElseGet(() -> ApiResponse.error(40401, "日志不存在"));
    }
} 
package com.aixone.event.api;

import com.aixone.event.dto.AuditLogDTO;
import com.aixone.common.api.ApiResponse;
import com.aixone.common.api.PageRequest;
import com.aixone.common.api.PageResult;

/**
 * 审计日志相关接口协议
 */
public interface AuditApi {
    /** 分页/条件查询审计日志 */
    ApiResponse<PageResult<AuditLogDTO>> listAuditLogs(PageRequest pageRequest, String eventType, String tenantId);

    /** 导出审计日志 */
    ApiResponse<String> exportAuditLogs(String eventType, String tenantId);
} 
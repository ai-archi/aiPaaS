package com.aixone.audit.interfaces;

import com.aixone.audit.application.AuditQueryService;
import com.aixone.audit.interfaces.dto.AuditLogDTO;
import com.aixone.audit.interfaces.dto.AuditLogQueryDTO;
import com.aixone.common.api.ApiResponse;
import com.aixone.common.api.PageRequest;
import com.aixone.common.api.PageResult;
import com.aixone.common.exception.ValidationException;
import com.aixone.common.util.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审计REST API控制器
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@RestController
@RequestMapping("/api/audit")
public class AuditController {
    
    private final AuditQueryService auditQueryService;
    
    @Autowired
    public AuditController(AuditQueryService auditQueryService) {
        this.auditQueryService = auditQueryService;
    }
    
    /**
     * 根据ID获取审计日志
     * 
     * @param id 审计日志ID
     * @return 审计日志
     */
    @GetMapping("/{id}")
    public ApiResponse<AuditLogDTO> getAuditLog(@PathVariable Long id) {
        try {
            AuditLogDTO auditLog = auditQueryService.getById(id);
            return ApiResponse.success(auditLog);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 分页查询审计日志
     * 
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param sortBy 排序字段
     * @param sortDirection 排序方向
     * @return 分页结果
     */
    @GetMapping
    public ApiResponse<PageResult<AuditLogDTO>> getAuditLogs(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        try {
            PageRequest pageRequest = new PageRequest(pageNum, pageSize, sortBy, sortDirection);
            PageResult<AuditLogDTO> result = auditQueryService.getAuditLogs(pageRequest);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 根据用户ID分页查询审计日志
     * 
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    @GetMapping("/user/{userId}")
    public ApiResponse<PageResult<AuditLogDTO>> getAuditLogsByUserId(
            @PathVariable String userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        try {
            ValidationUtils.notBlank(userId, "用户ID不能为空");
            PageRequest pageRequest = new PageRequest(pageNum, pageSize);
            PageResult<AuditLogDTO> result = auditQueryService.getAuditLogsByUserId(userId, pageRequest);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 根据操作类型分页查询审计日志
     * 
     * @param action 操作类型
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    @GetMapping("/action/{action}")
    public ApiResponse<PageResult<AuditLogDTO>> getAuditLogsByAction(
            @PathVariable String action,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        try {
            ValidationUtils.notBlank(action, "操作类型不能为空");
            PageRequest pageRequest = new PageRequest(pageNum, pageSize);
            PageResult<AuditLogDTO> result = auditQueryService.getAuditLogsByAction(action, pageRequest);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 根据时间范围分页查询审计日志
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    @GetMapping("/time-range")
    public ApiResponse<PageResult<AuditLogDTO>> getAuditLogsByTimeRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        try {
            ValidationUtils.notNull(startTime, "开始时间不能为空");
            ValidationUtils.notNull(endTime, "结束时间不能为空");
            if (startTime.isAfter(endTime)) {
                throw new ValidationException("开始时间不能晚于结束时间");
            }
            
            PageRequest pageRequest = new PageRequest(pageNum, pageSize);
            PageResult<AuditLogDTO> result = auditQueryService.getAuditLogsByTimeRange(startTime, endTime, pageRequest);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 根据多个条件分页查询审计日志
     * 
     * @param query 查询条件
     * @return 分页结果
     */
    @PostMapping("/search")
    public ApiResponse<PageResult<AuditLogDTO>> searchAuditLogs(@RequestBody AuditLogQueryDTO query) {
        try {
            ValidationUtils.notNull(query, "查询条件不能为空");
            PageResult<AuditLogDTO> result = auditQueryService.getAuditLogsByConditions(query);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户最近的操作日志
     * 
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 审计日志列表
     */
    @GetMapping("/user/{userId}/recent")
    public ApiResponse<List<AuditLogDTO>> getRecentAuditLogsByUserId(
            @PathVariable String userId,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            ValidationUtils.notBlank(userId, "用户ID不能为空");
            ValidationUtils.isTrue(limit > 0, "限制数量必须大于0");
            List<AuditLogDTO> result = auditQueryService.getRecentAuditLogsByUserId(userId, limit);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 统计审计日志数量
     * 
     * @return 审计日志总数
     */
    @GetMapping("/count")
    public ApiResponse<Long> countAuditLogs() {
        try {
            long count = auditQueryService.countAuditLogs();
            return ApiResponse.success(count);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 统计用户审计日志数量
     * 
     * @param userId 用户ID
     * @return 审计日志数量
     */
    @GetMapping("/user/{userId}/count")
    public ApiResponse<Long> countAuditLogsByUserId(@PathVariable String userId) {
        try {
            ValidationUtils.notBlank(userId, "用户ID不能为空");
            long count = auditQueryService.countAuditLogsByUserId(userId);
            return ApiResponse.success(count);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}

package com.aixone.audit.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审计日志查询DTO
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Data
public class AuditLogQueryDTO {
    
    /** 用户ID */
    private String userId;
    
    /** 操作类型 */
    private String action;
    
    /** 操作结果 */
    private String result;
    
    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    
    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    
    /** 页码 */
    private int pageNum = 1;
    
    /** 每页大小 */
    private int pageSize = 20;
    
    /** 排序字段 */
    private String sortBy = "timestamp";
    
    /** 排序方向 */
    private String sortDirection = "desc";
}

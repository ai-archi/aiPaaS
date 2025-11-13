package com.aixone.eventcenter.schedule.application;

import lombok.Data;

/**
 * 任务执行结果
 * 执行微服务返回的结果
 */
@Data
public class TaskExecutionResult {
    
    /**
     * 任务ID
     */
    private Long taskId;
    
    /**
     * 日志ID
     */
    private Long logId;
    
    /**
     * 是否执行成功
     */
    private boolean success;
    
    /**
     * 执行结果
     */
    private String result;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 错误堆栈
     */
    private String errorStack;
    
    /**
     * 执行耗时（毫秒）
     */
    private Long durationMs;
    
    /**
     * 租户ID
     */
    private String tenantId;
    
    /**
     * 执行ID（用于幂等性）
     */
    private String executionId;
}

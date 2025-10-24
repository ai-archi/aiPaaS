package com.aixone.eventcenter.schedule.application;

import com.aixone.eventcenter.schedule.domain.TaskType;
import lombok.Data;

/**
 * 任务执行消息
 * 发送给执行微服务的消息
 */
@Data
public class TaskExecutionMessage {
    
    /**
     * 任务ID
     */
    private Long taskId;
    
    /**
     * 日志ID
     */
    private Long logId;
    
    /**
     * 任务名称
     */
    private String taskName;
    
    /**
     * 任务类型
     */
    private TaskType taskType;
    
    /**
     * 任务参数（JSON格式）
     */
    private String taskParams;
    
    /**
     * 执行微服务名称
     */
    private String executorService;
    
    /**
     * 超时时间（秒）
     */
    private Integer timeoutSeconds;
    
    /**
     * 重试次数
     */
    private Integer retryCount;
    
    /**
     * 租户ID
     */
    private String tenantId;
    
    /**
     * 执行ID（用于幂等性）
     */
    private String executionId;
}

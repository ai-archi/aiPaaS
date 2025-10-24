package com.aixone.eventcenter.schedule.application;

import lombok.Data;

/**
 * 更新任务命令
 */
@Data
public class UpdateTaskCommand {
    
    /**
     * 任务描述
     */
    private String description;
    
    /**
     * 调度表达式
     */
    private String scheduleExpression;
    
    /**
     * 任务参数（JSON格式）
     */
    private String taskParams;
    
    /**
     * 最大重试次数
     */
    private Integer maxRetryCount;
    
    /**
     * 超时时间（秒）
     */
    private Integer timeoutSeconds;
}

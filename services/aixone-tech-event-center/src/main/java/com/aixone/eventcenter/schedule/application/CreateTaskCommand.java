package com.aixone.eventcenter.schedule.application;

import com.aixone.eventcenter.schedule.domain.TaskType;
import lombok.Data;

/**
 * 创建任务命令
 */
@Data
public class CreateTaskCommand {
    
    /**
     * 任务名称
     */
    private String taskName;
    
    /**
     * 任务描述
     */
    private String description;
    
    /**
     * 任务类型
     */
    private TaskType taskType;
    
    /**
     * 调度表达式
     */
    private String scheduleExpression;
    
    /**
     * 执行微服务名称
     */
    private String executorService;
    
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

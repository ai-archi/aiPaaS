package com.aixone.eventcenter.schedule.interfaces;

import com.aixone.eventcenter.schedule.application.TaskExecutionResult;
import com.aixone.eventcenter.schedule.application.TaskSchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 任务执行结果消息处理器
 * 处理来自执行微服务的任务执行结果
 */
@Component
public class TaskExecutionResultHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskExecutionResultHandler.class);
    
    @Autowired
    private TaskSchedulerService taskSchedulerService;
    
    /**
     * 处理任务执行结果
     */
    @KafkaListener(topics = "task-execution-result", groupId = "event-center-scheduler")
    public void handleTaskExecutionResult(TaskExecutionResult result) {
        try {
            logger.info("收到任务执行结果 - TaskId: {}, LogId: {}, Success: {}", 
                result.getTaskId(), result.getLogId(), result.isSuccess());
            
            // 处理执行结果
            taskSchedulerService.handleTaskExecutionResult(result);
            
            logger.info("任务执行结果处理完成 - TaskId: {}", result.getTaskId());
            
        } catch (Exception e) {
            logger.error("处理任务执行结果失败 - TaskId: {}, Error: {}", 
                result.getTaskId(), e.getMessage(), e);
        }
    }
}

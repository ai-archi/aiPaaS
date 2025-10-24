package com.aixone.eventcenter.schedule.application;

import com.aixone.eventcenter.schedule.domain.*;
import com.aixone.session.SessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * 任务调度服务
 * 负责任务的调度和执行
 */
@Service
public class TaskSchedulerService {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskSchedulerService.class);
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private TaskLogRepository taskLogRepository;
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    /**
     * 调度任务
     */
    public void scheduleTask(Task task) {
        try {
            // 根据任务类型计算下次执行时间
            Instant nextExecuteTime = calculateNextExecuteTime(task);
            task.updateExecuteTime(null, nextExecuteTime);
            taskRepository.save(task);
            
            logger.info("任务已调度 - TaskId: {}, TaskName: {}, NextExecuteTime: {}", 
                task.getTaskId(), task.getTaskName(), nextExecuteTime);
        } catch (Exception e) {
            logger.error("任务调度失败 - TaskId: {}, Error: {}", task.getTaskId(), e.getMessage(), e);
            throw new RuntimeException("任务调度失败", e);
        }
    }
    
    /**
     * 重新调度任务
     */
    public void rescheduleTask(Task task) {
        // 先取消现有调度
        unscheduleTask(task.getTaskId());
        // 重新调度
        scheduleTask(task);
    }
    
    /**
     * 取消任务调度
     */
    public void unscheduleTask(Long taskId) {
        try {
            // 这里可以添加取消调度的逻辑
            logger.info("任务调度已取消 - TaskId: {}", taskId);
        } catch (Exception e) {
            logger.error("取消任务调度失败 - TaskId: {}, Error: {}", taskId, e.getMessage(), e);
        }
    }
    
    /**
     * 立即执行任务
     */
    @Transactional
    public void executeTaskImmediately(Task task) {
        String executorNode = getExecutorNode();
        
        // 创建执行记录
        TaskLog taskLog = new TaskLog(task.getTaskId(), executorNode, Instant.now());
        taskLogRepository.save(taskLog);
        
        // 更新任务状态
        task.updateStatus(TaskStatus.RUNNING);
        taskRepository.save(task);
        
        try {
            // 发送任务到执行微服务
            sendTaskToExecutor(task, taskLog);
            
            logger.info("任务已发送执行 - TaskId: {}, TaskName: {}, ExecutorNode: {}", 
                task.getTaskId(), task.getTaskName(), executorNode);
        } catch (Exception e) {
            logger.error("任务执行失败 - TaskId: {}, Error: {}", task.getTaskId(), e.getMessage(), e);
            
            // 更新执行记录
            taskLog.fail(e.getMessage(), getStackTrace(e), 0L);
            taskLogRepository.save(taskLog);
            
            // 更新任务状态
            task.updateStatus(TaskStatus.FAILED);
            task.incrementRetryCount();
            taskRepository.save(task);
        }
    }
    
    /**
     * 处理任务执行结果
     */
    @Transactional
    public void handleTaskExecutionResult(TaskExecutionResult result) {
        // 查找任务
        Task task = taskRepository.findById(result.getTaskId())
            .orElseThrow(() -> new RuntimeException("任务不存在: " + result.getTaskId()));
        
        // 查找执行记录
        TaskLog taskLog = taskLogRepository.findById(result.getLogId())
            .orElseThrow(() -> new RuntimeException("任务执行记录不存在: " + result.getLogId()));
        
        if (result.isSuccess()) {
            // 执行成功
            taskLog.complete(TaskStatus.SUCCESS, result.getResult(), result.getDurationMs());
            taskLogRepository.save(taskLog);
            
            task.updateStatus(TaskStatus.SUCCESS);
            task.resetRetryCount();
            
            // 如果不是一次性任务，计算下次执行时间
            if (!task.getTaskType().equals(TaskType.ONCE)) {
                Instant nextExecuteTime = calculateNextExecuteTime(task);
                task.updateExecuteTime(Instant.now(), nextExecuteTime);
            } else {
                // 一次性任务执行完成后禁用
                task.setEnabled(false);
            }
            
            logger.info("任务执行成功 - TaskId: {}, Duration: {}ms", 
                task.getTaskId(), taskLog.getDurationMs());
        } else {
            // 执行失败
            taskLog.fail(result.getErrorMessage(), result.getErrorStack(), result.getDurationMs());
            taskLogRepository.save(taskLog);
            
            if (task.canRetry()) {
                // 可以重试
                task.updateStatus(TaskStatus.PENDING);
                task.incrementRetryCount();
                
                // 延迟重试（指数退避）
                long delaySeconds = calculateRetryDelay(task.getCurrentRetryCount());
                Instant retryTime = Instant.now().plusSeconds(delaySeconds);
                task.updateExecuteTime(null, retryTime);
                
                logger.info("任务执行失败，将重试 - TaskId: {}, RetryCount: {}, RetryTime: {}", 
                    task.getTaskId(), task.getCurrentRetryCount(), retryTime);
            } else {
                // 重试次数用完，标记为失败
                task.updateStatus(TaskStatus.FAILED);
                logger.error("任务执行失败，重试次数已用完 - TaskId: {}", task.getTaskId());
            }
        }
        
        taskRepository.save(task);
    }
    
    /**
     * 获取需要执行的任务
     */
    @Transactional(readOnly = true)
    public List<Task> getPendingTasks() {
        String tenantId = SessionContext.getTenantId();
        return taskRepository.findPendingTasksForExecution(Instant.now(), tenantId);
    }
    
    /**
     * 计算下次执行时间
     */
    private Instant calculateNextExecuteTime(Task task) {
        ScheduleStrategy strategy = new ScheduleStrategy(task.getScheduleExpression(), task.getTaskType());
        
        if (strategy.isCron()) {
            // Cron表达式任务，这里简化处理，实际应该使用Cron解析器
            return Instant.now().plusSeconds(60); // 默认1分钟后执行
        } else if (strategy.isOnce()) {
            // 一次性任务
            return strategy.getExecuteTime();
        } else if (strategy.isInterval()) {
            // 间隔任务
            return Instant.now().plusSeconds(strategy.getIntervalSeconds());
        }
        
        return Instant.now().plusSeconds(60);
    }
    
    /**
     * 发送任务到执行微服务
     */
    private void sendTaskToExecutor(Task task, TaskLog taskLog) {
        TaskExecutionMessage message = new TaskExecutionMessage();
        message.setTaskId(task.getTaskId());
        message.setLogId(taskLog.getLogId());
        message.setTaskName(task.getTaskName());
        message.setTaskType(task.getTaskType());
        message.setTaskParams(task.getTaskParams());
        message.setExecutorService(task.getExecutorService());
        message.setTimeoutSeconds(task.getTimeoutSeconds());
        message.setRetryCount(taskLog.getRetryCount());
        message.setTenantId(SessionContext.getTenantId());
        message.setExecutionId(UUID.randomUUID().toString());
        
        // 发送到Kafka
        String topic = "task-execution-" + task.getExecutorService();
        kafkaTemplate.send(topic, message);
    }
    
    /**
     * 获取执行节点标识
     */
    private String getExecutorNode() {
        return "scheduler-" + System.getProperty("user.name") + "-" + System.currentTimeMillis();
    }
    
    /**
     * 计算重试延迟（指数退避）
     */
    private long calculateRetryDelay(int retryCount) {
        return Math.min(300, (long) Math.pow(2, retryCount) * 30); // 最大5分钟
    }
    
    /**
     * 获取异常堆栈信息
     */
    private String getStackTrace(Throwable throwable) {
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
}

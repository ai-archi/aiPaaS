package com.aixone.eventcenter.schedule.infrastructure;

import com.aixone.eventcenter.schedule.domain.Task;
import com.aixone.eventcenter.schedule.domain.TaskType;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

/**
 * Quartz 任务调度器实现
 */
@Component
public class QuartzTaskScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(QuartzTaskScheduler.class);
    
    @Autowired
    private Scheduler scheduler;
    
    /**
     * 调度任务
     */
    public void scheduleTask(Task task) {
        try {
            JobDetail jobDetail = createJobDetail(task);
            Trigger trigger = createTrigger(task);
            
            scheduler.scheduleJob(jobDetail, trigger);
            
            logger.info("任务已调度到Quartz - TaskId: {}, JobKey: {}", 
                task.getTaskId(), jobDetail.getKey());
        } catch (SchedulerException e) {
            logger.error("调度任务到Quartz失败 - TaskId: {}, Error: {}", 
                task.getTaskId(), e.getMessage(), e);
            throw new RuntimeException("调度任务失败", e);
        }
    }
    
    /**
     * 重新调度任务
     */
    public void rescheduleTask(Task task) {
        try {
            JobKey jobKey = JobKey.jobKey("task-" + task.getTaskId());
            
            // 删除现有任务
            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
            }
            
            // 重新调度
            scheduleTask(task);
            
            logger.info("任务已重新调度 - TaskId: {}", task.getTaskId());
        } catch (SchedulerException e) {
            logger.error("重新调度任务失败 - TaskId: {}, Error: {}", 
                task.getTaskId(), e.getMessage(), e);
            throw new RuntimeException("重新调度任务失败", e);
        }
    }
    
    /**
     * 取消任务调度
     */
    public void unscheduleTask(Long taskId) {
        try {
            JobKey jobKey = JobKey.jobKey("task-" + taskId);
            
            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
                logger.info("任务已从Quartz中移除 - TaskId: {}", taskId);
            }
        } catch (SchedulerException e) {
            logger.error("取消任务调度失败 - TaskId: {}, Error: {}", 
                taskId, e.getMessage(), e);
        }
    }
    
    /**
     * 立即执行任务
     */
    public void executeTaskImmediately(Task task) {
        try {
            JobDetail jobDetail = createJobDetail(task);
            Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("immediate-trigger-" + task.getTaskId())
                .startNow()
                .build();
            
            scheduler.scheduleJob(jobDetail, trigger);
            
            logger.info("任务已立即执行 - TaskId: {}", task.getTaskId());
        } catch (SchedulerException e) {
            logger.error("立即执行任务失败 - TaskId: {}, Error: {}", 
                task.getTaskId(), e.getMessage(), e);
            throw new RuntimeException("立即执行任务失败", e);
        }
    }
    
    /**
     * 创建JobDetail
     */
    private JobDetail createJobDetail(Task task) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("taskId", task.getTaskId());
        jobDataMap.put("taskName", task.getTaskName());
        jobDataMap.put("taskType", task.getTaskType());
        jobDataMap.put("executorService", task.getExecutorService());
        jobDataMap.put("taskParams", task.getTaskParams());
        jobDataMap.put("timeoutSeconds", task.getTimeoutSeconds());
        
        return JobBuilder.newJob(TaskExecutionJob.class)
            .withIdentity("task-" + task.getTaskId())
            .withDescription(task.getDescription())
            .setJobData(jobDataMap)
            .storeDurably()
            .build();
    }
    
    /**
     * 创建Trigger
     */
    private Trigger createTrigger(Task task) {
        TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger()
            .withIdentity("trigger-" + task.getTaskId())
            .withDescription("Trigger for task: " + task.getTaskName());
        
        if (task.getTaskType() == TaskType.ONCE) {
            // 一次性任务
            Instant executeTime = Instant.parse(task.getScheduleExpression());
            triggerBuilder.startAt(Date.from(executeTime));
        } else if (task.getTaskType() == TaskType.CRON) {
            // Cron任务
            CronScheduleBuilder cronSchedule = CronScheduleBuilder.cronSchedule(task.getScheduleExpression());
            triggerBuilder.withSchedule(cronSchedule);
        } else if (task.getTaskType() == TaskType.INTERVAL) {
            // 间隔任务
            long intervalSeconds = Long.parseLong(task.getScheduleExpression());
            SimpleScheduleBuilder simpleSchedule = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds((int) intervalSeconds)
                .repeatForever();
            triggerBuilder.withSchedule(simpleSchedule);
        }
        
        return triggerBuilder.build();
    }
    
    /**
     * 检查任务是否存在
     */
    public boolean isTaskScheduled(Long taskId) {
        try {
            JobKey jobKey = JobKey.jobKey("task-" + taskId);
            return scheduler.checkExists(jobKey);
        } catch (SchedulerException e) {
            logger.error("检查任务调度状态失败 - TaskId: {}, Error: {}", 
                taskId, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 暂停任务
     */
    public void pauseTask(Long taskId) {
        try {
            JobKey jobKey = JobKey.jobKey("task-" + taskId);
            if (scheduler.checkExists(jobKey)) {
                scheduler.pauseJob(jobKey);
                logger.info("任务已暂停 - TaskId: {}", taskId);
            }
        } catch (SchedulerException e) {
            logger.error("暂停任务失败 - TaskId: {}, Error: {}", 
                taskId, e.getMessage(), e);
        }
    }
    
    /**
     * 恢复任务
     */
    public void resumeTask(Long taskId) {
        try {
            JobKey jobKey = JobKey.jobKey("task-" + taskId);
            if (scheduler.checkExists(jobKey)) {
                scheduler.resumeJob(jobKey);
                logger.info("任务已恢复 - TaskId: {}", taskId);
            }
        } catch (SchedulerException e) {
            logger.error("恢复任务失败 - TaskId: {}, Error: {}", 
                taskId, e.getMessage(), e);
        }
    }
}

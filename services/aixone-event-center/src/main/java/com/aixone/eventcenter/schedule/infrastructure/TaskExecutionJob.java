package com.aixone.eventcenter.schedule.infrastructure;

import com.aixone.eventcenter.schedule.application.TaskSchedulerService;
import com.aixone.eventcenter.schedule.domain.Task;
import com.aixone.eventcenter.schedule.domain.TaskRepository;
import com.aixone.common.session.SessionContext;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 任务执行Job
 * Quartz调度的任务执行器
 */
@Component
public class TaskExecutionJob implements Job {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskExecutionJob.class);
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private TaskSchedulerService taskSchedulerService;
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            // 获取任务信息
            Long taskId = context.getJobDetail().getJobDataMap().getLong("taskId");
            String taskName = context.getJobDetail().getJobDataMap().getString("taskName");
            
            logger.info("开始执行任务 - TaskId: {}, TaskName: {}", taskId, taskName);
            
            // 查找任务
            String tenantId = SessionContext.getTenantId();
            Task task = taskRepository.findByIdAndTenantId(taskId, tenantId)
                .orElseThrow(() -> new JobExecutionException("任务不存在: " + taskId));
            
            // 检查任务是否启用
            if (!task.getEnabled()) {
                logger.warn("任务已禁用，跳过执行 - TaskId: {}", taskId);
                return;
            }
            
            // 执行任务
            taskSchedulerService.executeTaskImmediately(task);
            
            logger.info("任务执行完成 - TaskId: {}, TaskName: {}", taskId, taskName);
            
        } catch (Exception e) {
            logger.error("任务执行异常 - TaskId: {}, Error: {}", 
                context.getJobDetail().getJobDataMap().getLong("taskId"), e.getMessage(), e);
            throw new JobExecutionException("任务执行失败", e);
        }
    }
}

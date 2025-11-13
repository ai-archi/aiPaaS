package com.aixone.eventcenter.schedule.application;

import com.aixone.common.exception.BizException;
import com.aixone.common.util.ValidationUtils;
import com.aixone.eventcenter.schedule.domain.*;
import com.aixone.common.session.SessionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * 任务管理应用服务
 * 提供任务管理的业务逻辑
 */
@Service
@Transactional
public class TaskApplicationService {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private TaskLogRepository taskLogRepository;
    
    @Autowired
    private TaskSchedulerService taskSchedulerService;
    
    /**
     * 创建任务
     */
    public Task createTask(CreateTaskCommand command) {
        ValidationUtils.notNull(command, "创建任务命令不能为空");
        ValidationUtils.notBlank(command.getTaskName(), "任务名称不能为空");
        ValidationUtils.notBlank(command.getExecutorService(), "执行微服务不能为空");
        ValidationUtils.notNull(command.getTaskType(), "任务类型不能为空");
        ValidationUtils.notBlank(command.getScheduleExpression(), "调度表达式不能为空");
        
        // 检查任务名称是否已存在
        String tenantId = SessionContext.getTenantId();
        if (taskRepository.findByTaskNameAndTenantId(command.getTaskName(), tenantId).isPresent()) {
            throw new BizException("TASK_NAME_EXISTS", "任务名称已存在: " + command.getTaskName());
        }
        
        // 创建任务实体
        Task task = new Task(
            command.getTaskName(),
            command.getDescription(),
            command.getTaskType(),
            command.getScheduleExpression(),
            command.getExecutorService(),
            command.getTaskParams()
        );
        
        // 设置其他属性
        if (command.getMaxRetryCount() != null) {
            task.setMaxRetryCount(command.getMaxRetryCount());
        }
        if (command.getTimeoutSeconds() != null) {
            task.setTimeoutSeconds(command.getTimeoutSeconds());
        }
        task.setCreator(SessionContext.getUserId());
        
        // 保存任务
        Task savedTask = taskRepository.save(task);
        
        // 如果任务启用，则添加到调度器
        if (savedTask.getEnabled()) {
            taskSchedulerService.scheduleTask(savedTask);
        }
        
        return savedTask;
    }
    
    /**
     * 更新任务
     */
    public Task updateTask(Long taskId, UpdateTaskCommand command) {
        ValidationUtils.notNull(taskId, "任务ID不能为空");
        ValidationUtils.notNull(command, "更新任务命令不能为空");
        
        String tenantId = SessionContext.getTenantId();
        Task task = taskRepository.findByIdAndTenantId(taskId, tenantId)
            .orElseThrow(() -> new BizException("TASK_NOT_FOUND", "任务不存在: " + taskId));
        
        // 更新任务属性
        if (command.getDescription() != null) {
            task.setDescription(command.getDescription());
        }
        if (command.getScheduleExpression() != null) {
            task.setScheduleExpression(command.getScheduleExpression());
        }
        if (command.getTaskParams() != null) {
            task.setTaskParams(command.getTaskParams());
        }
        if (command.getMaxRetryCount() != null) {
            task.setMaxRetryCount(command.getMaxRetryCount());
        }
        if (command.getTimeoutSeconds() != null) {
            task.setTimeoutSeconds(command.getTimeoutSeconds());
        }
        
        task.setUpdateTime(Instant.now());
        
        // 保存任务
        Task savedTask = taskRepository.save(task);
        
        // 重新调度任务
        taskSchedulerService.rescheduleTask(savedTask);
        
        return savedTask;
    }
    
    /**
     * 删除任务
     */
    public void deleteTask(Long taskId) {
        ValidationUtils.notNull(taskId, "任务ID不能为空");
        
        String tenantId = SessionContext.getTenantId();
        taskRepository.findByIdAndTenantId(taskId, tenantId)
            .orElseThrow(() -> new BizException("TASK_NOT_FOUND", "任务不存在: " + taskId));
        
        // 从调度器中移除任务
        taskSchedulerService.unscheduleTask(taskId);
        
        // 删除任务日志
        taskLogRepository.deleteByTaskIdAndTenantId(taskId, tenantId);
        
        // 删除任务
        taskRepository.deleteById(taskId);
    }
    
    /**
     * 暂停任务
     */
    public void pauseTask(Long taskId) {
        ValidationUtils.notNull(taskId, "任务ID不能为空");
        
        String tenantId = SessionContext.getTenantId();
        Task task = taskRepository.findByIdAndTenantId(taskId, tenantId)
            .orElseThrow(() -> new BizException("TASK_NOT_FOUND", "任务不存在: " + taskId));
        
        task.pause();
        taskRepository.save(task);
        
        // 从调度器中移除任务
        taskSchedulerService.unscheduleTask(taskId);
    }
    
    /**
     * 恢复任务
     */
    public void resumeTask(Long taskId) {
        ValidationUtils.notNull(taskId, "任务ID不能为空");
        
        String tenantId = SessionContext.getTenantId();
        Task task = taskRepository.findByIdAndTenantId(taskId, tenantId)
            .orElseThrow(() -> new BizException("TASK_NOT_FOUND", "任务不存在: " + taskId));
        
        task.resume();
        taskRepository.save(task);
        
        // 重新调度任务
        taskSchedulerService.scheduleTask(task);
    }
    
    /**
     * 取消任务
     */
    public void cancelTask(Long taskId) {
        ValidationUtils.notNull(taskId, "任务ID不能为空");
        
        String tenantId = SessionContext.getTenantId();
        Task task = taskRepository.findByIdAndTenantId(taskId, tenantId)
            .orElseThrow(() -> new BizException("TASK_NOT_FOUND", "任务不存在: " + taskId));
        
        task.cancel();
        taskRepository.save(task);
        
        // 从调度器中移除任务
        taskSchedulerService.unscheduleTask(taskId);
    }
    
    /**
     * 立即执行任务
     */
    public void executeTask(Long taskId) {
        ValidationUtils.notNull(taskId, "任务ID不能为空");
        
        String tenantId = SessionContext.getTenantId();
        Task task = taskRepository.findByIdAndTenantId(taskId, tenantId)
            .orElseThrow(() -> new BizException("TASK_NOT_FOUND", "任务不存在: " + taskId));
        
        if (!task.getEnabled()) {
            throw new BizException("TASK_DISABLED", "任务已禁用，无法执行");
        }
        
        // 立即执行任务
        taskSchedulerService.executeTaskImmediately(task);
    }
    
    /**
     * 根据ID查询任务
     */
    @Transactional(readOnly = true)
    public Optional<Task> getTaskById(Long taskId) {
        ValidationUtils.notNull(taskId, "任务ID不能为空");
        return taskRepository.findByIdAndTenantId(taskId, SessionContext.getTenantId());
    }
    
    /**
     * 分页查询任务
     */
    @Transactional(readOnly = true)
    public Page<Task> getTasks(Pageable pageable) {
        return taskRepository.findByTenantId(SessionContext.getTenantId(), pageable);
    }
    
    /**
     * 根据状态查询任务
     */
    @Transactional(readOnly = true)
    public List<Task> getTasksByStatus(TaskStatus status) {
        ValidationUtils.notNull(status, "任务状态不能为空");
        return taskRepository.findByStatusAndTenantId(status, SessionContext.getTenantId());
    }
    
    /**
     * 根据执行微服务查询任务
     */
    @Transactional(readOnly = true)
    public List<Task> getTasksByExecutorService(String executorService) {
        ValidationUtils.notBlank(executorService, "执行微服务不能为空");
        return taskRepository.findByExecutorServiceAndTenantId(executorService, SessionContext.getTenantId());
    }
    
    /**
     * 获取任务执行记录
     */
    @Transactional(readOnly = true)
    public Page<TaskLog> getTaskLogs(Long taskId, Pageable pageable) {
        ValidationUtils.notNull(taskId, "任务ID不能为空");
        return taskLogRepository.findByTaskIdAndTenantId(taskId, SessionContext.getTenantId(), pageable);
    }
    
    /**
     * 获取任务统计信息
     */
    @Transactional(readOnly = true)
    public TaskStatistics getTaskStatistics(Long taskId) {
        ValidationUtils.notNull(taskId, "任务ID不能为空");
        
        String tenantId = SessionContext.getTenantId();
        long totalCount = taskLogRepository.countByTaskIdAndTenantId(taskId, tenantId);
        long successCount = taskLogRepository.countByTaskIdAndStatusAndTenantId(taskId, TaskStatus.SUCCESS, tenantId);
        long failCount = taskLogRepository.countByTaskIdAndStatusAndTenantId(taskId, TaskStatus.FAILED, tenantId);
        
        return new TaskStatistics(taskId, totalCount, successCount, failCount);
    }
    
    /**
     * 启用任务
     */
    public Task enableTask(Long taskId) {
        ValidationUtils.notNull(taskId, "任务ID不能为空");
        
        String tenantId = SessionContext.getTenantId();
        Task task = taskRepository.findByIdAndTenantId(taskId, tenantId)
            .orElseThrow(() -> new BizException("TASK_NOT_FOUND", "任务不存在: " + taskId));
        
        if (task.getEnabled()) {
            throw new BizException("TASK_ALREADY_ENABLED", "任务已启用");
        }
        
        task.setEnabled(true);
        Task savedTask = taskRepository.save(task);
        
        // 添加到调度器
        taskSchedulerService.scheduleTask(savedTask);
        
        return savedTask;
    }
    
    /**
     * 禁用任务
     */
    public Task disableTask(Long taskId) {
        ValidationUtils.notNull(taskId, "任务ID不能为空");
        
        String tenantId = SessionContext.getTenantId();
        Task task = taskRepository.findByIdAndTenantId(taskId, tenantId)
            .orElseThrow(() -> new BizException("TASK_NOT_FOUND", "任务不存在: " + taskId));
        
        if (!task.getEnabled()) {
            throw new BizException("TASK_ALREADY_DISABLED", "任务已禁用");
        }
        
        task.setEnabled(false);
        Task savedTask = taskRepository.save(task);
        
        // 从调度器中移除任务
        taskSchedulerService.unscheduleTask(taskId);
        
        return savedTask;
    }
    
    /**
     * 获取所有任务
     */
    @Transactional(readOnly = true)
    public List<Task> getAllTasks() {
        return taskRepository.findByTenantId(SessionContext.getTenantId());
    }
    
    /**
     * 获取启用的任务
     */
    @Transactional(readOnly = true)
    public List<Task> getEnabledTasks() {
        return taskRepository.findByEnabledAndTenantId(true, SessionContext.getTenantId());
    }
}

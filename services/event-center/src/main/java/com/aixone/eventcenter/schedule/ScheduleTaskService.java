package com.aixone.eventcenter.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import com.aixone.eventcenter.monitor.MonitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 调度任务服务
 */
@Service
public class ScheduleTaskService {
    private static final Logger logger = LoggerFactory.getLogger(ScheduleTaskService.class);
    @Autowired
    private ScheduleTaskRepository scheduleTaskRepository;
    @Autowired
    private MonitorService monitorService;

    /**
     * 注册新任务
     */
    public ScheduleTask registerTask(ScheduleTask task, String tenantId) {
        try {
            task.setCreatedAt(java.time.Instant.now());
            task.setTenantId(tenantId);
            ScheduleTask saved = scheduleTaskRepository.save(task);
            monitorService.incTask();
            return saved;
        } catch (Exception ex) {
            monitorService.incTaskError();
            logger.error("[ALERT] 任务注册失败: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * 查询所有任务
     */
    public List<ScheduleTask> getAllTasks(String tenantId) {
        return scheduleTaskRepository.findByTenantId(tenantId);
    }

    /**
     * 按ID查询任务
     */
    public Optional<ScheduleTask> getTaskById(Long id, String tenantId) {
        return scheduleTaskRepository.findByTaskIdAndTenantId(id, tenantId);
    }

    /**
     * 修改任务
     */
    public ScheduleTask updateTask(Long id, ScheduleTask updated, String tenantId) {
        return scheduleTaskRepository.findByTaskIdAndTenantId(id, tenantId).map(task -> {
            task.setName(updated.getName());
            task.setCron(updated.getCron());
            task.setType(updated.getType());
            task.setStatus(updated.getStatus());
            task.setPayload(updated.getPayload());
            return scheduleTaskRepository.save(task);
        }).orElseThrow(() -> new RuntimeException("任务不存在"));
    }

    /**
     * 删除任务
     */
    public void deleteTask(Long id, String tenantId) {
        scheduleTaskRepository.findByTaskIdAndTenantId(id, tenantId).ifPresent(scheduleTaskRepository::delete);
    }
} 
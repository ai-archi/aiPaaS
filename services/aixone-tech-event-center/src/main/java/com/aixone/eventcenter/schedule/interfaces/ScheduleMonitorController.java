package com.aixone.eventcenter.schedule.interfaces;

import com.aixone.common.api.ApiResponse;
import com.aixone.eventcenter.schedule.application.TaskApplicationService;
import com.aixone.eventcenter.schedule.application.TaskSchedulerService;
import com.aixone.eventcenter.schedule.domain.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务调度监控控制器
 * 提供调度中心的监控和管理接口
 */
@RestController
@RequestMapping("/api/v1/schedule/monitor")
public class ScheduleMonitorController {
    
    @Autowired
    private TaskApplicationService taskApplicationService;
    
    @Autowired
    private TaskSchedulerService taskSchedulerService;
    
    /**
     * 获取调度中心状态
     */
    @GetMapping("/status")
    public ApiResponse<Map<String, Object>> getScheduleStatus() {
        Map<String, Object> status = new HashMap<>();
        
        // 获取待执行任务
        List<Task> pendingTasks = taskApplicationService.getTasksByStatus(com.aixone.eventcenter.schedule.domain.TaskStatus.PENDING);
        status.put("pendingTasks", pendingTasks.size());
        
        // 获取执行中任务
        List<Task> runningTasks = taskApplicationService.getTasksByStatus(com.aixone.eventcenter.schedule.domain.TaskStatus.RUNNING);
        status.put("runningTasks", runningTasks.size());
        
        // 获取成功任务
        List<Task> successTasks = taskApplicationService.getTasksByStatus(com.aixone.eventcenter.schedule.domain.TaskStatus.SUCCESS);
        status.put("successTasks", successTasks.size());
        
        // 获取失败任务
        List<Task> failTasks = taskApplicationService.getTasksByStatus(com.aixone.eventcenter.schedule.domain.TaskStatus.FAILED);
        status.put("failTasks", failTasks.size());
        
        // 获取暂停任务
        List<Task> pausedTasks = taskApplicationService.getTasksByStatus(com.aixone.eventcenter.schedule.domain.TaskStatus.PAUSED);
        status.put("pausedTasks", pausedTasks.size());
        
        // 获取总任务数
        long totalTasks = pendingTasks.size() + runningTasks.size() + successTasks.size() + failTasks.size() + pausedTasks.size();
        status.put("totalTasks", totalTasks);
        
        // 计算成功率
        long completedTasks = successTasks.size() + failTasks.size();
        double successRate = completedTasks > 0 ? (double) successTasks.size() / completedTasks * 100 : 0.0;
        status.put("successRate", String.format("%.2f%%", successRate));
        
        return ApiResponse.success(status);
    }
    
    /**
     * 获取待执行任务列表
     */
    @GetMapping("/pending-tasks")
    public ApiResponse<List<Task>> getPendingTasks() {
        List<Task> pendingTasks = taskSchedulerService.getPendingTasks();
        return ApiResponse.success(pendingTasks);
    }
    
    /**
     * 获取调度中心健康状态
     */
    @GetMapping("/health")
    public ApiResponse<Map<String, String>> getHealth() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("scheduler", "RUNNING");
        health.put("timestamp", java.time.Instant.now().toString());
        return ApiResponse.success(health);
    }
}

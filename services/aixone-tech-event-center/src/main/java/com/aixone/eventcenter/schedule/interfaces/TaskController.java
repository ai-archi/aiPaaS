package com.aixone.eventcenter.schedule.interfaces;

import com.aixone.common.api.ApiResponse;
import com.aixone.eventcenter.schedule.application.*;
import com.aixone.eventcenter.schedule.domain.Task;
import com.aixone.eventcenter.schedule.domain.TaskLog;
import com.aixone.eventcenter.schedule.domain.TaskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 任务管理REST控制器
 */
@RestController
@RequestMapping("/api/v1/schedule/tasks")
public class TaskController {
    
    @Autowired
    private TaskApplicationService taskApplicationService;
    
    /**
     * 创建任务
     */
    @PostMapping
    public ApiResponse<Task> createTask(@RequestBody CreateTaskCommand command) {
        Task task = taskApplicationService.createTask(command);
        return ApiResponse.success(task);
    }
    
    /**
     * 更新任务
     */
    @PutMapping("/{taskId}")
    public ApiResponse<Task> updateTask(@PathVariable Long taskId, @RequestBody UpdateTaskCommand command) {
        taskApplicationService.updateTask(taskId, command);
        return ApiResponse.success();
    }
    
    /**
     * 删除任务
     */
    @DeleteMapping("/{taskId}")
    public ApiResponse<Void> deleteTask(@PathVariable Long taskId) {
        taskApplicationService.deleteTask(taskId);
        return ApiResponse.success();
    }
    
    /**
     * 暂停任务
     */
    @PostMapping("/{taskId}/pause")
    public ApiResponse<Void> pauseTask(@PathVariable Long taskId) {
        taskApplicationService.pauseTask(taskId);
        return ApiResponse.success();
    }
    
    /**
     * 恢复任务
     */
    @PostMapping("/{taskId}/resume")
    public ApiResponse<Void> resumeTask(@PathVariable Long taskId) {
        taskApplicationService.resumeTask(taskId);
        return ApiResponse.success();
    }
    
    /**
     * 取消任务
     */
    @PostMapping("/{taskId}/cancel")
    public ApiResponse<Void> cancelTask(@PathVariable Long taskId) {
        taskApplicationService.cancelTask(taskId);
        return ApiResponse.success();
    }
    
    /**
     * 立即执行任务
     */
    @PostMapping("/{taskId}/execute")
    public ApiResponse<Void> executeTask(@PathVariable Long taskId) {
        taskApplicationService.executeTask(taskId);
        return ApiResponse.success();
    }
    
    /**
     * 根据ID查询任务
     */
    @GetMapping("/{taskId}")
    public ApiResponse<Task> getTaskById(@PathVariable Long taskId) {
        Optional<Task> task = taskApplicationService.getTaskById(taskId);
        return task.map(ApiResponse::success).orElse(ApiResponse.notFound("任务不存在"));
    }
    
    /**
     * 分页查询任务
     */
    @GetMapping
    public ApiResponse<Page<Task>> getTasks(Pageable pageable) {
        Page<Task> tasks = taskApplicationService.getTasks(pageable);
        return ApiResponse.success(tasks);
    }
    
    /**
     * 根据状态查询任务
     */
    @GetMapping("/status/{status}")
    public ApiResponse<List<Task>> getTasksByStatus(@PathVariable TaskStatus status) {
        List<Task> tasks = taskApplicationService.getTasksByStatus(status);
        return ApiResponse.success(tasks);
    }
    
    /**
     * 根据执行微服务查询任务
     */
    @GetMapping("/executor/{executorService}")
    public ApiResponse<List<Task>> getTasksByExecutorService(@PathVariable String executorService) {
        List<Task> tasks = taskApplicationService.getTasksByExecutorService(executorService);
        return ApiResponse.success(tasks);
    }
    
    /**
     * 获取任务执行记录
     */
    @GetMapping("/{taskId}/logs")
    public ApiResponse<Page<TaskLog>> getTaskLogs(@PathVariable Long taskId, Pageable pageable) {
        Page<TaskLog> logs = taskApplicationService.getTaskLogs(taskId, pageable);
        return ApiResponse.success(logs);
    }
    
    /**
     * 获取任务统计信息
     */
    @GetMapping("/{taskId}/statistics")
    public ApiResponse<TaskStatistics> getTaskStatistics(@PathVariable Long taskId) {
        TaskStatistics statistics = taskApplicationService.getTaskStatistics(taskId);
        return ApiResponse.success(statistics);
    }
}

package com.aixone.eventcenter.schedule.interfaces;

import com.aixone.common.api.ApiResponse;
import com.aixone.eventcenter.schedule.application.TaskApplicationService;
import com.aixone.eventcenter.schedule.domain.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 任务管理接口控制器（管理员接口，支持跨租户操作）
 * /api/v1/admin/schedule/tasks
 */
@RestController
@RequestMapping("/api/v1/admin/schedule/tasks")
public class TaskAdminController {
    private static final Logger logger = LoggerFactory.getLogger(TaskAdminController.class);
    
    @Autowired
    private TaskApplicationService taskApplicationService;

    /**
     * 管理员查询任务列表（可跨租户）
     */
    @GetMapping
    public ApiResponse<List<Task>> getTasks(
            @RequestParam(required = false) String tenantId,
            @RequestParam(required = false) String taskName,
            @RequestParam(required = false) String status) {
        
        if (!StringUtils.hasText(tenantId)) {
            return ApiResponse.error(40001, "tenantId参数不能为空");
        }
        
        logger.info("管理员查询任务列表: tenantId={}, taskName={}, status={}", tenantId, taskName, status);
        
        // TODO: 实现跨租户查询逻辑
        List<Task> tasks = taskApplicationService.getAllTasks();
        
        return ApiResponse.success(tasks);
    }

    /**
     * 管理员查询任务详情（可跨租户）
     */
    @GetMapping("/{taskId}")
    public ApiResponse<Task> getTaskById(
            @PathVariable Long taskId,
            @RequestParam(required = false) String tenantId) {
        
        if (!StringUtils.hasText(tenantId)) {
            return ApiResponse.error(40001, "tenantId参数不能为空");
        }
        
        logger.info("管理员查询任务详情: taskId={}, tenantId={}", taskId, tenantId);
        
        // TODO: 实现跨租户查询逻辑
        return ApiResponse.error(501, "功能待实现");
    }

    /**
     * 管理员删除任务
     */
    @DeleteMapping("/{taskId}")
    public ApiResponse<Void> deleteTask(
            @PathVariable Long taskId,
            @RequestParam(required = false) String tenantId) {
        
        if (!StringUtils.hasText(tenantId)) {
            return ApiResponse.error(40001, "tenantId参数不能为空");
        }
        
        logger.info("管理员删除任务: taskId={}, tenantId={}", taskId, tenantId);
        
        taskApplicationService.deleteTask(taskId);
        return ApiResponse.success(null);
    }
}


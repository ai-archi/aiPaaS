package com.aixone.eventcenter.schedule;

import com.aixone.eventcenter.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 调度任务接口
 * /api/schedule/tasks
 */
@RestController
@RequestMapping("/api/schedule/tasks")
public class ScheduleTaskController {
    @Autowired
    private ScheduleTaskService scheduleTaskService;

    private static final String TENANT_HEADER = "X-Tenant-Id";

    /**
     * 注册新任务
     */
    @PostMapping
    public ApiResponse<ScheduleTask> registerTask(@RequestHeader(value = TENANT_HEADER, required = false) String tenantId, @RequestBody ScheduleTask task) {
        if (tenantId == null || tenantId.isEmpty()) {
            return ApiResponse.error(40001, "缺少租户ID");
        }
        return ApiResponse.success(scheduleTaskService.registerTask(task, tenantId));
    }

    /**
     * 查询所有任务
     */
    @GetMapping
    public ApiResponse<List<ScheduleTask>> getAllTasks(@RequestHeader(value = TENANT_HEADER, required = false) String tenantId) {
        if (tenantId == null || tenantId.isEmpty()) {
            return ApiResponse.error(40001, "缺少租户ID");
        }
        return ApiResponse.success(scheduleTaskService.getAllTasks(tenantId));
    }

    /**
     * 按ID查询任务
     */
    @GetMapping("/{id}")
    public ApiResponse<ScheduleTask> getTaskById(@RequestHeader(value = TENANT_HEADER, required = false) String tenantId, @PathVariable Long id) {
        if (tenantId == null || tenantId.isEmpty()) {
            return ApiResponse.error(40001, "缺少租户ID");
        }
        return scheduleTaskService.getTaskById(id, tenantId)
                .map(ApiResponse::success)
                .orElseGet(() -> ApiResponse.error(40401, "任务不存在"));
    }

    /**
     * 修改任务
     */
    @PutMapping("/{id}")
    public ApiResponse<ScheduleTask> updateTask(@RequestHeader(value = TENANT_HEADER, required = false) String tenantId, @PathVariable Long id, @RequestBody ScheduleTask updated) {
        if (tenantId == null || tenantId.isEmpty()) {
            return ApiResponse.error(40001, "缺少租户ID");
        }
        return ApiResponse.success(scheduleTaskService.updateTask(id, updated, tenantId));
    }

    /**
     * 删除任务
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTask(@RequestHeader(value = TENANT_HEADER, required = false) String tenantId, @PathVariable Long id) {
        if (tenantId == null || tenantId.isEmpty()) {
            return ApiResponse.error(40001, "缺少租户ID");
        }
        scheduleTaskService.deleteTask(id, tenantId);
        return ApiResponse.success(null);
    }
} 
package com.aixone.eventcenter.schedule;

import com.aixone.eventcenter.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 任务执行日志接口
 * /api/schedule/job-logs
 */
@RestController
@RequestMapping("/api/schedule/job-logs")
public class JobLogController {
    @Autowired
    private JobLogService jobLogService;

    /**
     * 查询所有任务日志
     */
    @GetMapping
    public ApiResponse<List<JobLog>> getAllLogs() {
        return ApiResponse.success(jobLogService.getAllLogs());
    }

    /**
     * 按ID查询日志
     */
    @GetMapping("/{id}")
    public ApiResponse<JobLog> getLogById(@PathVariable Long id) {
        return jobLogService.getLogById(id)
                .map(ApiResponse::success)
                .orElseGet(() -> ApiResponse.error(40401, "日志不存在"));
    }
} 
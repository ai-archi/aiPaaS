package com.aixone.eventcenter.monitor;

import com.aixone.eventcenter.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 监控与告警接口
 * /api/monitor
 */
@RestController
@RequestMapping("/api/monitor")
public class MonitorController {
    @Autowired
    private MonitorService monitorService;

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.success("ok");
    }

    /**
     * 占位：未来可扩展监控、告警等接口
     */
    @GetMapping("/status")
    public ApiResponse<String> status() {
        return ApiResponse.success("monitor status: running");
    }

    /**
     * 运行时核心指标
     */
    @GetMapping("/metrics")
    public ApiResponse<Object> metrics() {
        return ApiResponse.success(new java.util.HashMap<String, Object>() {{
            put("eventCount", monitorService.getEventCount());
            put("eventErrorCount", monitorService.getEventErrorCount());
            put("taskCount", monitorService.getTaskCount());
            put("taskErrorCount", monitorService.getTaskErrorCount());
        }});
    }

    /**
     * 告警日志接口（占位，实际可对接日志/邮件/Webhook等）
     */
    @GetMapping("/alerts")
    public ApiResponse<String> alerts() {
        // 实际可集成日志、邮件、Webhook等
        return ApiResponse.success("暂无告警，后续可扩展");
    }
} 
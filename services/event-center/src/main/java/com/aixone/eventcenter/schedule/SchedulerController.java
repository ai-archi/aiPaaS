package com.aixone.eventcenter.schedule;

import com.aixone.eventcenter.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 调度节点接口
 * /api/schedule/nodes
 */
@RestController
@RequestMapping("/api/schedule/nodes")
public class SchedulerController {
    @Autowired
    private SchedulerService schedulerService;

    /**
     * 查询所有调度节点
     */
    @GetMapping
    public ApiResponse<List<Scheduler>> getAllNodes() {
        return ApiResponse.success(schedulerService.getAllNodes());
    }
} 
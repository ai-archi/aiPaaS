package com.aixone.workbench.dashboard.interfaces.rest;

import com.aixone.common.api.ApiResponse;
import com.aixone.workbench.dashboard.application.dto.DashboardDTO;
import com.aixone.workbench.dashboard.domain.service.DashboardAggregationService;
import com.aixone.workbench.dashboard.domain.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 仪表盘控制器
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/workbench/dashboard")
@Slf4j
@RequiredArgsConstructor
public class DashboardController {
    
    private final DashboardService dashboardService;
    private final DashboardAggregationService dashboardAggregationService;
    
    /**
     * 获取用户仪表盘数据（带统计和欢迎语）
     * 对应前端：/admin/Dashboard/index
     */
    @GetMapping("/index")
    public ResponseEntity<ApiResponse<DashboardDTO>> getDashboardIndex(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) UUID tenantId) {
        
        log.info("获取用户仪表盘数据: userId={}, tenantId={}", userId, tenantId);
        
        // 如果没有传userId和tenantId，返回默认数据（模拟）
        if (userId == null || tenantId == null) {
            DashboardDTO defaultData = DashboardDTO.builder()
                    .remark("欢迎使用AixOne工作台")
                    .build();
            return ResponseEntity.ok(ApiResponse.success(defaultData));
        }
        
        DashboardDTO dashboard = dashboardAggregationService.getDashboardData(userId, tenantId);
        
        return ResponseEntity.ok(ApiResponse.success(dashboard));
    }
    
    /**
     * 获取用户仪表盘配置
     */
    @GetMapping
    public ResponseEntity<ApiResponse<DashboardDTO>> getDashboard(
            @RequestParam UUID userId,
            @RequestParam UUID tenantId) {
        
        log.info("获取用户仪表盘配置: userId={}, tenantId={}", userId, tenantId);
        
        DashboardDTO dashboard = dashboardService.getDashboard(userId, tenantId);
        
        return ResponseEntity.ok(ApiResponse.success(dashboard));
    }
    
    /**
     * 保存用户仪表盘配置
     */
    @PutMapping
    public ResponseEntity<ApiResponse<Void>> saveDashboard(
            @RequestParam UUID userId,
            @RequestParam UUID tenantId,
            @RequestBody DashboardDTO dashboardDTO) {
        
        log.info("保存用户仪表盘配置: userId={}, tenantId={}", userId, tenantId);
        
        dashboardService.saveDashboard(userId, tenantId, dashboardDTO);
        
        return ResponseEntity.ok(ApiResponse.success());
    }
}


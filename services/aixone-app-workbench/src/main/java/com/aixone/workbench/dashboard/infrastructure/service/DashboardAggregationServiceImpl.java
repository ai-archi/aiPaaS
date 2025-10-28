package com.aixone.workbench.dashboard.infrastructure.service;

import com.aixone.workbench.dashboard.application.dto.DashboardDTO;
import com.aixone.workbench.dashboard.application.dto.StatisticsDTO;
import com.aixone.workbench.dashboard.domain.service.DashboardAggregationService;
import com.aixone.workbench.dashboard.domain.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * 仪表盘聚合服务实现
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardAggregationServiceImpl implements DashboardAggregationService {
    
    private final DashboardService dashboardService;
    
    @Override
    public DashboardDTO getDashboardData(UUID userId, UUID tenantId) {
        log.info("聚合用户仪表盘数据: userId={}, tenantId={}", userId, tenantId);
        
        // 获取仪表盘基础数据
        DashboardDTO dashboard = dashboardService.getDashboard(userId, tenantId);
        
        // 生成欢迎语
        String remark = generateRemark();
        
        // 获取统计数据（这里模拟，实际应该从其他服务聚合）
        StatisticsDTO statistics = getStatistics(userId, tenantId);
        
        return DashboardDTO.builder()
                .id(dashboard.getId())
                .userId(dashboard.getUserId())
                .tenantId(dashboard.getTenantId())
                .name(dashboard.getName())
                .layout(dashboard.getLayout())
                .components(dashboard.getComponents() != null ? dashboard.getComponents() : List.of())
                .config(dashboard.getConfig())
                .remark(remark)
                .statistics(statistics)
                .build();
    }
    
    /**
     * 生成欢迎语
     * 根据时间生成不同的问候语
     */
    private String generateRemark() {
        int hour = LocalTime.now().getHour();
        
        if (hour >= 5 && hour < 9) {
            return "早上好！新的一天，新的开始。";
        } else if (hour >= 9 && hour < 12) {
            return "上午好！今天也要加油哦。";
        } else if (hour >= 12 && hour < 14) {
            return "中午好！记得吃好饭，休息一下。";
        } else if (hour >= 14 && hour < 18) {
            return "下午好！继续努力工作。";
        } else if (hour >= 18 && hour < 22) {
            return "晚上好！今天辛苦了。";
        } else {
            return "夜深了，注意休息。";
        }
    }
    
    /**
     * 获取统计数据
     * TODO: 实际应该从用户服务、文件服务、插件服务等聚合数据
     * 当前返回模拟数据
     */
    private StatisticsDTO getStatistics(UUID userId, UUID tenantId) {
        // TODO: 从directory服务获取实际统计数据
        // 当前返回模拟数据
        return StatisticsDTO.builder()
                .userRegNumber(0L)  // 待实现：从用户服务获取
                .fileNumber(0L)      // 待实现：从文件服务获取
                .usersNumber(0L)     // 待实现：从目录服务获取
                .addonsNumber(0L)    // 待实现：从插件服务获取
                .build();
    }
}


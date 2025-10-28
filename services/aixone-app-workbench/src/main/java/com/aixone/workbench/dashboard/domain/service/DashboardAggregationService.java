package com.aixone.workbench.dashboard.domain.service;

import com.aixone.workbench.dashboard.application.dto.DashboardDTO;

import java.util.UUID;

/**
 * 仪表盘聚合服务
 * 负责聚合仪表盘数据和统计数据
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
public interface DashboardAggregationService {
    
    /**
     * 获取用户仪表盘数据（包含欢迎语和统计数据）
     * 
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @return 仪表盘数据
     */
    DashboardDTO getDashboardData(UUID userId, UUID tenantId);
}


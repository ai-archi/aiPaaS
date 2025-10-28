package com.aixone.workbench.dashboard.domain.service;

import com.aixone.workbench.dashboard.application.dto.DashboardDTO;

import java.util.UUID;

/**
 * 仪表盘领域服务
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
public interface DashboardService {
    
    DashboardDTO getDashboard(UUID userId, UUID tenantId);
    
    void saveDashboard(UUID userId, UUID tenantId, DashboardDTO dashboardDTO);
}


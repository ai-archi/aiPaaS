package com.aixone.workbench.dashboard.infrastructure.service;

import com.aixone.workbench.dashboard.application.dto.DashboardDTO;
import com.aixone.workbench.dashboard.domain.model.Dashboard;
import com.aixone.workbench.dashboard.domain.repository.DashboardRepository;
import com.aixone.workbench.dashboard.domain.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 仪表盘服务实现
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    
    private final DashboardRepository dashboardRepository;
    
    @Override
    @Cacheable(value = "dashboardCache", key = "'dashboard:user:' + #userId + ':tenant:' + #tenantId")
    public DashboardDTO getDashboard(UUID userId, UUID tenantId) {
        log.info("获取用户仪表盘: userId={}, tenantId={}", userId, tenantId);
        
        Optional<Dashboard> dashboard = dashboardRepository
                .findByUserIdAndTenantId(userId, tenantId)
                .stream()
                .findFirst();
        
        if (dashboard.isPresent()) {
            return toDTO(dashboard.get());
        }
        
        return DashboardDTO.builder()
                .userId(userId)
                .tenantId(tenantId)
                .name("默认仪表盘")
                .layout("{}")
                .components(java.util.Collections.emptyList())
                .config("{}")
                .build();
    }
    
    @Override
    @Transactional
    public void saveDashboard(UUID userId, UUID tenantId, DashboardDTO dashboardDTO) {
        log.info("保存用户仪表盘: userId={}, tenantId={}", userId, tenantId);
        
        Optional<Dashboard> existing = dashboardRepository
                .findByUserIdAndTenantId(userId, tenantId)
                .stream()
                .findFirst();
        
        if (existing.isPresent()) {
            Dashboard dashboard = existing.get();
            // 更新仪表盘配置
            if (dashboardDTO.getName() != null) {
                dashboard.setName(dashboardDTO.getName());
            }
            if (dashboardDTO.getLayout() != null) {
                dashboard.setLayout(dashboardDTO.getLayout());
            }
            if (dashboardDTO.getConfig() != null) {
                dashboard.setConfig(dashboardDTO.getConfig());
            }
            dashboardRepository.save(dashboard);
        } else {
            Dashboard dashboard = Dashboard.builder()
                    .userId(userId)
                    .tenantId(tenantId)
                    .name(dashboardDTO.getName() != null ? dashboardDTO.getName() : "默认仪表盘")
                    .layout(dashboardDTO.getLayout() != null ? dashboardDTO.getLayout() : "{}")
                    .components("[]")
                    .config(dashboardDTO.getConfig() != null ? dashboardDTO.getConfig() : "{}")
                    .build();
            dashboardRepository.save(dashboard);
        }
    }
    
    private DashboardDTO toDTO(Dashboard dashboard) {
        return DashboardDTO.builder()
                .id(dashboard.getId())
                .userId(dashboard.getUserId())
                .tenantId(dashboard.getTenantId())
                .name(dashboard.getName())
                .layout(dashboard.getLayout())
                .components(null) // Dashboard实体使用String存储，不直接映射
                .config(dashboard.getConfig())
                .build();
    }
}


package com.aixone.workbench.dashboard.infrastructure.service;

import com.aixone.workbench.dashboard.application.dto.DashboardDTO;
import com.aixone.workbench.dashboard.application.dto.StatisticsDTO;
import com.aixone.workbench.dashboard.domain.model.Dashboard;
import com.aixone.workbench.dashboard.domain.repository.DashboardRepository;
import com.aixone.workbench.dashboard.domain.service.DashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 仪表盘聚合服务测试
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("仪表盘聚合服务测试")
class DashboardAggregationServiceImplTest {
    
    @Mock
    private DashboardService dashboardService;
    
    @InjectMocks
    private DashboardAggregationServiceImpl dashboardAggregationService;
    
    private UUID userId;
    private UUID tenantId;
    
    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        tenantId = UUID.randomUUID();
    }
    
    @Test
    @DisplayName("聚合仪表盘数据-成功")
    void testGetDashboardData_Success() {
        // Given
        DashboardDTO dashboardDTO = DashboardDTO.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .tenantId(tenantId)
                .name("测试仪表盘")
                .layout("{}")
                .components(List.of())
                .config("{}")
                .build();
        
        when(dashboardService.getDashboard(userId, tenantId))
                .thenReturn(dashboardDTO);
        
        // When
        DashboardDTO result = dashboardAggregationService.getDashboardData(userId, tenantId);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRemark()).isNotEmpty();
        assertThat(result.getStatistics()).isNotNull();
        assertThat(result.getStatistics().getUserRegNumber()).isNotNull();
        assertThat(result.getStatistics().getFileNumber()).isNotNull();
        
        verify(dashboardService, times(1)).getDashboard(userId, tenantId);
    }
    
    @Test
    @DisplayName("聚合仪表盘数据-包含统计数据")
    void testGetDashboardData_WithStatistics() {
        // Given
        DashboardDTO dashboardDTO = DashboardDTO.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .tenantId(tenantId)
                .name("测试仪表盘")
                .layout("{}")
                .components(List.of())
                .config("{}")
                .build();
        
        when(dashboardService.getDashboard(userId, tenantId))
                .thenReturn(dashboardDTO);
        
        // When
        DashboardDTO result = dashboardAggregationService.getDashboardData(userId, tenantId);
        
        // Then
        assertThat(result.getStatistics()).isNotNull();
        assertThat(result.getStatistics()).isInstanceOf(StatisticsDTO.class);
    }
    
    @Test
    @DisplayName("聚合仪表盘数据-包含欢迎语")
    void testGetDashboardData_WithRemark() {
        // Given
        DashboardDTO dashboardDTO = DashboardDTO.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .tenantId(tenantId)
                .name("测试仪表盘")
                .layout("{}")
                .components(List.of())
                .config("{}")
                .build();
        
        when(dashboardService.getDashboard(userId, tenantId))
                .thenReturn(dashboardDTO);
        
        // When
        DashboardDTO result = dashboardAggregationService.getDashboardData(userId, tenantId);
        
        // Then
        assertThat(result.getRemark()).isNotNull();
        assertThat(result.getRemark()).isNotEmpty();
    }
    
    @Test
    @DisplayName("聚合仪表盘数据-null components处理")
    void testGetDashboardData_WithNullComponents() {
        // Given
        DashboardDTO dashboardDTO = DashboardDTO.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .tenantId(tenantId)
                .name("测试仪表盘")
                .layout("{}")
                .components(null)  // null components
                .config("{}")
                .build();
        
        when(dashboardService.getDashboard(userId, tenantId))
                .thenReturn(dashboardDTO);
        
        // When
        DashboardDTO result = dashboardAggregationService.getDashboardData(userId, tenantId);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getComponents()).isNotNull();
        assertThat(result.getComponents()).isEmpty();
    }
    
    @Test
    @DisplayName("聚合仪表盘数据-验证统计数据字段完整性")
    void testGetDashboardData_VerifyStatisticsFields() {
        // Given
        DashboardDTO dashboardDTO = DashboardDTO.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .tenantId(tenantId)
                .name("测试仪表盘")
                .layout("{}")
                .components(List.of())
                .config("{}")
                .build();
        
        when(dashboardService.getDashboard(userId, tenantId))
                .thenReturn(dashboardDTO);
        
        // When
        DashboardDTO result = dashboardAggregationService.getDashboardData(userId, tenantId);
        
        // Then
        StatisticsDTO statistics = result.getStatistics();
        assertThat(statistics).isNotNull();
        assertThat(statistics.getUserRegNumber()).isNotNull();
        assertThat(statistics.getFileNumber()).isNotNull();
        assertThat(statistics.getUsersNumber()).isNotNull();
        assertThat(statistics.getAddonsNumber()).isNotNull();
    }
}


package com.aixone.workbench.dashboard.infrastructure.service;

import com.aixone.workbench.dashboard.application.dto.DashboardDTO;
import com.aixone.workbench.dashboard.domain.model.Dashboard;
import com.aixone.workbench.dashboard.domain.repository.DashboardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * 仪表盘服务测试
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("仪表盘服务测试")
class DashboardServiceImplTest {
    
    @Mock
    private DashboardRepository dashboardRepository;
    
    @InjectMocks
    private DashboardServiceImpl dashboardService;
    
    private UUID userId;
    private UUID tenantId;
    
    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        tenantId = UUID.randomUUID();
    }
    
    @Test
    @DisplayName("测试获取用户仪表盘 - 有数据")
    void testGetDashboard_WithData() {
        // Given
        Dashboard dashboard = Dashboard.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .tenantId(tenantId)
                .name("测试仪表盘")
                .layout("{}")
                .config("{}")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        when(dashboardRepository.findByUserIdAndTenantId(userId, tenantId))
                .thenReturn(List.of(dashboard));
        
        // When
        DashboardDTO result = dashboardService.getDashboard(userId, tenantId);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("测试仪表盘");
        verify(dashboardRepository).findByUserIdAndTenantId(userId, tenantId);
    }
    
    @Test
    @DisplayName("测试获取用户仪表盘 - 无数据")
    void testGetDashboard_NoData() {
        // Given
        when(dashboardRepository.findByUserIdAndTenantId(userId, tenantId))
                .thenReturn(List.of());
        
        // When
        DashboardDTO result = dashboardService.getDashboard(userId, tenantId);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("默认仪表盘");
    }
    
    @Test
    @DisplayName("测试保存用户仪表盘 - 新配置")
    void testSaveDashboard_NewConfig() {
        // Given
        DashboardDTO dashboardDTO = DashboardDTO.builder()
                .name("新仪表盘")
                .layout("{}")
                .config("{}")
                .build();
        
        when(dashboardRepository.findByUserIdAndTenantId(userId, tenantId))
                .thenReturn(List.of());
        
        // When
        dashboardService.saveDashboard(userId, tenantId, dashboardDTO);
        
        // Then
        verify(dashboardRepository).save(any(Dashboard.class));
    }
    
    @Test
    @DisplayName("测试保存用户仪表盘 - 更新配置")
    void testSaveDashboard_UpdateConfig() {
        // Given
        Dashboard existing = Dashboard.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .tenantId(tenantId)
                .name("旧仪表盘")
                .layout("{}")
                .config("{}")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        DashboardDTO dashboardDTO = DashboardDTO.builder()
                .name("新仪表盘")
                .layout("{\"columns\": 2}")
                .config("{\"theme\": \"dark\"}")
                .build();
        
        when(dashboardRepository.findByUserIdAndTenantId(userId, tenantId))
                .thenReturn(List.of(existing));
        
        // When
        dashboardService.saveDashboard(userId, tenantId, dashboardDTO);
        
        // Then
        assertThat(existing.getName()).isEqualTo("新仪表盘");
        verify(dashboardRepository).save(existing);
    }
    
    @Test
    @DisplayName("测试保存用户仪表盘 - null值")
    void testSaveDashboard_WithNullValues() {
        // Given
        DashboardDTO dashboardDTO = DashboardDTO.builder()
                .name(null)
                .layout(null)
                .config(null)
                .build();
        
        when(dashboardRepository.findByUserIdAndTenantId(userId, tenantId))
                .thenReturn(List.of());
        
        // When
        dashboardService.saveDashboard(userId, tenantId, dashboardDTO);
        
        // Then
        verify(dashboardRepository).save(argThat(dashboard ->
                dashboard.getName().equals("默认仪表盘") &&
                dashboard.getLayout().equals("{}")
        ));
    }
    
    @Test
    @DisplayName("测试获取用户仪表盘 - 多个仪表盘")
    void testGetDashboard_MultipleDashboards() {
        // Given
        Dashboard dashboard1 = Dashboard.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .tenantId(tenantId)
                .name("仪表盘1")
                .build();
        
        Dashboard dashboard2 = Dashboard.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .tenantId(tenantId)
                .name("仪表盘2")
                .build();
        
        when(dashboardRepository.findByUserIdAndTenantId(userId, tenantId))
                .thenReturn(List.of(dashboard1, dashboard2));
        
        // When
        DashboardDTO result = dashboardService.getDashboard(userId, tenantId);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("仪表盘1");
    }
    
    @Test
    @DisplayName("测试获取用户仪表盘 - 异常处理")
    void testGetDashboard_Exception() {
        // Given
        when(dashboardRepository.findByUserIdAndTenantId(userId, tenantId))
                .thenThrow(new RuntimeException("Database error"));
        
        // When & Then
        assertThrows(RuntimeException.class, () ->
                dashboardService.getDashboard(userId, tenantId));
    }
}


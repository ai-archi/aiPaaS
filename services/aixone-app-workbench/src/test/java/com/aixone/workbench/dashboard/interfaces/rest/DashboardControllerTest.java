package com.aixone.workbench.dashboard.interfaces.rest;

import com.aixone.workbench.dashboard.application.dto.DashboardDTO;
import com.aixone.workbench.dashboard.application.dto.StatisticsDTO;
import com.aixone.workbench.dashboard.domain.service.DashboardAggregationService;
import com.aixone.workbench.dashboard.domain.service.DashboardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 仪表盘控制器测试
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@WebMvcTest(DashboardController.class)
@Import(com.aixone.workbench.config.TestSecurityConfig.class)
@DisplayName("仪表盘控制器测试")
class DashboardControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private DashboardService dashboardService;
    
    @MockBean
    private DashboardAggregationService dashboardAggregationService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private UUID userId;
    private UUID tenantId;
    
    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        tenantId = UUID.randomUUID();
    }
    
    @Test
    @DisplayName("测试获取仪表盘index-成功")
    void testGetDashboardIndex_Success() throws Exception {
        // Given
        DashboardDTO dashboardDTO = DashboardDTO.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .tenantId(tenantId)
                .name("测试仪表盘")
                .remark("欢迎使用")
                .statistics(StatisticsDTO.builder()
                        .userRegNumber(100L)
                        .fileNumber(200L)
                        .usersNumber(300L)
                        .addonsNumber(50L)
                        .build())
                .build();
        
        when(dashboardAggregationService.getDashboardData(eq(userId), eq(tenantId)))
                .thenReturn(dashboardDTO);
        
        // When & Then
        mockMvc.perform(get("/workbench/dashboard/index")
                        .param("userId", userId.toString())
                        .param("tenantId", tenantId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.remark").isNotEmpty())
                .andExpect(jsonPath("$.statistics").exists())
                .andExpect(jsonPath("$.statistics.userRegNumber").value(100));
    }
    
    @Test
    @DisplayName("测试获取仪表盘index-默认数据")
    void testGetDashboardIndex_DefaultData() throws Exception {
        // When & Then
        mockMvc.perform(get("/workbench/dashboard/index")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.remark").isNotEmpty());
    }
    
    @Test
    @DisplayName("测试获取仪表盘配置-成功")
    void testGetDashboard_Success() throws Exception {
        // Given
        DashboardDTO dashboardDTO = DashboardDTO.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .tenantId(tenantId)
                .name("测试仪表盘")
                .layout("{}")
                .config("{}")
                .build();
        
        when(dashboardService.getDashboard(eq(userId), eq(tenantId)))
                .thenReturn(dashboardDTO);
        
        // When & Then
        mockMvc.perform(get("/workbench/dashboard")
                        .param("userId", userId.toString())
                        .param("tenantId", tenantId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("测试仪表盘"));
    }
    
    @Test
    @DisplayName("测试保存仪表盘配置-成功")
    void testSaveDashboard_Success() throws Exception {
        // Given
        DashboardDTO dashboardDTO = DashboardDTO.builder()
                .name("新仪表盘")
                .layout("{\"columns\": 2}")
                .config("{\"theme\": \"dark\"}")
                .build();
        
        // When & Then
        mockMvc.perform(put("/workbench/dashboard")
                        .param("userId", userId.toString())
                        .param("tenantId", tenantId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dashboardDTO)))
                .andExpect(status().isOk());
    }
    
    @Test
    @DisplayName("测试获取仪表盘index-未传参返回默认数据")
    void testGetDashboardIndex_NoParams() throws Exception {
        // When & Then
        mockMvc.perform(get("/workbench/dashboard/index")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.remark").value("欢迎使用AixOne工作台"));
    }
    
    @Test
    @DisplayName("测试获取仪表盘index-仅传userId")
    void testGetDashboardIndex_OnlyUserId() throws Exception {
        // When & Then
        mockMvc.perform(get("/workbench/dashboard/index")
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.remark").value("欢迎使用AixOne工作台"));
    }
}


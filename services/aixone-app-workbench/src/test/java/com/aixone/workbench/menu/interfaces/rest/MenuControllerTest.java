package com.aixone.workbench.menu.interfaces.rest;

import com.aixone.workbench.config.TestSecurityConfig;
import com.aixone.workbench.menu.application.dto.MenuDTO;
import com.aixone.workbench.menu.application.dto.UserMenuCustomDTO;
import com.aixone.workbench.menu.domain.service.MenuAggregationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 菜单控制器测试
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@WebMvcTest(MenuController.class)
@Import(TestSecurityConfig.class)
@DisplayName("菜单控制器测试")
class MenuControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private MenuAggregationService menuAggregationService;
    
    private UUID userId;
    private UUID tenantId;
    private UUID menuId;
    
    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        tenantId = UUID.randomUUID();
        menuId = UUID.randomUUID();
    }
    
    @Test
    @DisplayName("测试获取用户可见菜单")
    void testGetVisibleMenus() throws Exception {
        // Given
        List<MenuDTO> menus = List.of(
                MenuDTO.builder()
                        .id(UUID.randomUUID().toString())
                        .name("测试菜单")
                        .type("MENU")
                        .build()
        );
        when(menuAggregationService.aggregateVisibleMenus(eq(userId), eq(tenantId), anyList()))
                .thenReturn(menus);
        
        // When & Then
        mockMvc.perform(get("/workbench/menus")
                        .param("userId", userId.toString())
                        .param("tenantId", tenantId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("测试菜单"));
    }
    
    @Test
    @DisplayName("测试获取用户菜单个性化配置")
    void testGetUserMenuCustom() throws Exception {
        // Given
        Map<String, UserMenuCustomDTO> configs = Map.of(
                menuId.toString(), UserMenuCustomDTO.builder()
                        .id(UUID.randomUUID())
                        .menuId(menuId)
                        .config("{}")
                        .build()
        );
        when(menuAggregationService.getUserMenuCustomConfig(eq(userId), eq(tenantId)))
                .thenReturn(configs);
        
        // When & Then
        mockMvc.perform(get("/workbench/menus/custom")
                        .param("userId", userId.toString())
                        .param("tenantId", tenantId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap());
    }
    
    @Test
    @DisplayName("测试保存用户菜单个性化配置")
    void testSaveUserMenuCustom() throws Exception {
        // Given
        String config = "{\"isQuickEntry\": true}";
        
        // When & Then
        mockMvc.perform(put("/workbench/menus/custom")
                        .param("userId", userId.toString())
                        .param("tenantId", tenantId.toString())
                        .param("menuId", menuId.toString())
                        .content(config)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        
        verify(menuAggregationService).saveUserMenuCustom(eq(userId), eq(tenantId), eq(menuId), eq(config));
    }
}

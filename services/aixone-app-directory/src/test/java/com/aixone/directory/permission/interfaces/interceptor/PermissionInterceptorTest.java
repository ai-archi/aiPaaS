package com.aixone.directory.permission.interfaces.interceptor;

import com.aixone.common.session.SessionContext;
import com.aixone.directory.permission.application.PermissionRuleApplicationService;
import com.aixone.directory.permission.application.PermissionRuleDto;
import com.aixone.directory.permission.domain.service.PermissionDecisionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 权限拦截器单元测试
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("权限拦截器测试")
class PermissionInterceptorTest {

    @Mock
    private PermissionRuleApplicationService permissionRuleApplicationService;

    @Mock
    private PermissionDecisionService permissionDecisionService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Object handler;

    @InjectMocks
    private PermissionInterceptor permissionInterceptor;

    private String tenantId;
    private String userId;

    @BeforeEach
    void setUp() {
        tenantId = "tenant-" + UUID.randomUUID().toString();
        userId = "user-" + UUID.randomUUID().toString();
    }

    @Test
    @DisplayName("拦截非管理接口 - 允许通过")
    void testPreHandle_NonAdminPath_ReturnsTrue() throws Exception {
        // Given
        when(request.getRequestURI()).thenReturn("/api/v1/public/test");
        when(request.getMethod()).thenReturn("GET");

        // When
        boolean result = permissionInterceptor.preHandle(request, response, handler);

        // Then
        assertTrue(result);
        verify(permissionRuleApplicationService, never()).findPermissionRulesByPathAndMethod(anyString(), anyString(), anyString());
        verify(permissionDecisionService, never()).checkPermissionByIdentifier(anyString(), anyString(), anyString(), any());
    }

    @Test
    @DisplayName("拦截管理接口 - 未找到权限规则，允许通过")
    void testPreHandle_AdminPath_NoRules_ReturnsTrue() throws Exception {
        // Given
        when(request.getRequestURI()).thenReturn("/api/v1/admin/test");
        when(request.getMethod()).thenReturn("GET");

        try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
            mockedSessionContext.when(SessionContext::getTenantId).thenReturn(tenantId);
            mockedSessionContext.when(SessionContext::getUserId).thenReturn(userId);

            when(permissionRuleApplicationService.findPermissionRulesByPathAndMethod(tenantId, "/api/v1/admin/test", "GET"))
                    .thenReturn(new ArrayList<>());

            // When
            boolean result = permissionInterceptor.preHandle(request, response, handler);

            // Then
            assertTrue(result);
            verify(permissionRuleApplicationService, times(1)).findPermissionRulesByPathAndMethod(tenantId, "/api/v1/admin/test", "GET");
            verify(permissionDecisionService, never()).checkPermissionByIdentifier(anyString(), anyString(), anyString(), any());
        }
    }

    @Test
    @DisplayName("拦截管理接口 - 有权限规则，权限验证通过")
    void testPreHandle_AdminPath_WithRules_PermissionGranted_ReturnsTrue() throws Exception {
        // Given
        String path = "/api/v1/admin/test";
        String method = "GET";
        String permission = "test:read";

        PermissionRuleDto.PermissionRuleView rule = PermissionRuleDto.PermissionRuleView.builder()
                .permission(permission)
                .priority(1)
                .build();
        List<PermissionRuleDto.PermissionRuleView> rules = List.of(rule);

        when(request.getRequestURI()).thenReturn(path);
        when(request.getMethod()).thenReturn(method);

        try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
            mockedSessionContext.when(SessionContext::getTenantId).thenReturn(tenantId);
            mockedSessionContext.when(SessionContext::getUserId).thenReturn(userId);

            when(permissionRuleApplicationService.findPermissionRulesByPathAndMethod(tenantId, path, method))
                    .thenReturn(rules);
            when(permissionDecisionService.checkPermissionByIdentifier(userId, tenantId, permission, null))
                    .thenReturn(true);

            // When
            boolean result = permissionInterceptor.preHandle(request, response, handler);

            // Then
            assertTrue(result);
            verify(permissionRuleApplicationService, times(1)).findPermissionRulesByPathAndMethod(tenantId, path, method);
            verify(permissionDecisionService, times(1)).checkPermissionByIdentifier(userId, tenantId, permission, null);
            verify(response, never()).setStatus(anyInt());
        }
    }

    @Test
    @DisplayName("拦截管理接口 - 有权限规则，权限验证失败")
    void testPreHandle_AdminPath_WithRules_PermissionDenied_ReturnsFalse() throws Exception {
        // Given
        String path = "/api/v1/admin/test";
        String method = "GET";
        String permission = "test:read";

        PermissionRuleDto.PermissionRuleView rule = PermissionRuleDto.PermissionRuleView.builder()
                .permission(permission)
                .priority(1)
                .build();
        List<PermissionRuleDto.PermissionRuleView> rules = List.of(rule);

        when(request.getRequestURI()).thenReturn(path);
        when(request.getMethod()).thenReturn(method);

        try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
            mockedSessionContext.when(SessionContext::getTenantId).thenReturn(tenantId);
            mockedSessionContext.when(SessionContext::getUserId).thenReturn(userId);

            when(permissionRuleApplicationService.findPermissionRulesByPathAndMethod(tenantId, path, method))
                    .thenReturn(rules);
            when(permissionDecisionService.checkPermissionByIdentifier(userId, tenantId, permission, null))
                    .thenReturn(false);

            // When
            boolean result = permissionInterceptor.preHandle(request, response, handler);

            // Then
            assertFalse(result);
            verify(permissionRuleApplicationService, times(1)).findPermissionRulesByPathAndMethod(tenantId, path, method);
            verify(permissionDecisionService, times(1)).checkPermissionByIdentifier(userId, tenantId, permission, null);
            verify(response, times(1)).setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    @Test
    @DisplayName("拦截管理接口 - 未提供租户信息")
    void testPreHandle_AdminPath_NoTenantId_ReturnsFalse() throws Exception {
        // Given
        String path = "/api/v1/admin/test";
        String method = "GET";

        when(request.getRequestURI()).thenReturn(path);
        when(request.getMethod()).thenReturn(method);

        try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
            mockedSessionContext.when(SessionContext::getTenantId).thenReturn(null);
            mockedSessionContext.when(SessionContext::getUserId).thenReturn(userId);

            // When
            boolean result = permissionInterceptor.preHandle(request, response, handler);

            // Then
            assertFalse(result);
            verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            verify(permissionRuleApplicationService, never()).findPermissionRulesByPathAndMethod(anyString(), anyString(), anyString());
        }
    }

    @Test
    @DisplayName("拦截管理接口 - 未提供用户信息")
    void testPreHandle_AdminPath_NoUserId_ReturnsFalse() throws Exception {
        // Given
        String path = "/api/v1/admin/test";
        String method = "GET";

        when(request.getRequestURI()).thenReturn(path);
        when(request.getMethod()).thenReturn(method);

        try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
            mockedSessionContext.when(SessionContext::getTenantId).thenReturn(tenantId);
            mockedSessionContext.when(SessionContext::getUserId).thenReturn(null);

            // When
            boolean result = permissionInterceptor.preHandle(request, response, handler);

            // Then
            assertFalse(result);
            verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            verify(permissionRuleApplicationService, never()).findPermissionRulesByPathAndMethod(anyString(), anyString(), anyString());
        }
    }

    @Test
    @DisplayName("拦截管理接口 - 权限规则带admin:前缀")
    void testPreHandle_AdminPath_WithAdminPrefix_ReturnsTrue() throws Exception {
        // Given
        String path = "/api/v1/admin/test";
        String method = "GET";
        String permission = "admin:test:read";

        PermissionRuleDto.PermissionRuleView rule = PermissionRuleDto.PermissionRuleView.builder()
                .permission(permission)
                .priority(1)
                .build();
        List<PermissionRuleDto.PermissionRuleView> rules = List.of(rule);

        when(request.getRequestURI()).thenReturn(path);
        when(request.getMethod()).thenReturn(method);

        try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
            mockedSessionContext.when(SessionContext::getTenantId).thenReturn(tenantId);
            mockedSessionContext.when(SessionContext::getUserId).thenReturn(userId);

            when(permissionRuleApplicationService.findPermissionRulesByPathAndMethod(tenantId, path, method))
                    .thenReturn(rules);
            when(permissionDecisionService.checkPermissionByIdentifier(userId, tenantId, "test:read", null))
                    .thenReturn(true);

            // When
            boolean result = permissionInterceptor.preHandle(request, response, handler);

            // Then
            assertTrue(result);
            verify(permissionDecisionService, times(1)).checkPermissionByIdentifier(userId, tenantId, "test:read", null);
        }
    }

    @Test
    @DisplayName("拦截管理接口 - 多个权限规则，使用优先级最高的")
    void testPreHandle_AdminPath_MultipleRules_UsesHighestPriority() throws Exception {
        // Given
        String path = "/api/v1/admin/test";
        String method = "GET";

        PermissionRuleDto.PermissionRuleView rule1 = PermissionRuleDto.PermissionRuleView.builder()
                .permission("test:read")
                .priority(2)
                .build();
        PermissionRuleDto.PermissionRuleView rule2 = PermissionRuleDto.PermissionRuleView.builder()
                .permission("test:write")
                .priority(1)
                .build();
        List<PermissionRuleDto.PermissionRuleView> rules = List.of(rule1, rule2); // 已按优先级排序

        when(request.getRequestURI()).thenReturn(path);
        when(request.getMethod()).thenReturn(method);

        try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
            mockedSessionContext.when(SessionContext::getTenantId).thenReturn(tenantId);
            mockedSessionContext.when(SessionContext::getUserId).thenReturn(userId);

            when(permissionRuleApplicationService.findPermissionRulesByPathAndMethod(tenantId, path, method))
                    .thenReturn(rules);
            when(permissionDecisionService.checkPermissionByIdentifier(userId, tenantId, "test:read", null))
                    .thenReturn(true);

            // When
            boolean result = permissionInterceptor.preHandle(request, response, handler);

            // Then
            assertTrue(result);
            // 应该使用第一个规则（优先级最高）
            verify(permissionDecisionService, times(1)).checkPermissionByIdentifier(userId, tenantId, "test:read", null);
            verify(permissionDecisionService, never()).checkPermissionByIdentifier(userId, tenantId, "test:write", null);
        }
    }
}


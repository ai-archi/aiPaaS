package com.aixone.tech.auth.authorization.application.service;

import com.aixone.tech.auth.authorization.application.dto.CheckPermissionRequest;
import com.aixone.tech.auth.authorization.application.dto.CheckPermissionResponse;
import com.aixone.tech.auth.authorization.domain.service.PermissionDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 授权应用服务测试
 */
@ExtendWith(MockitoExtension.class)
class AuthorizationApplicationServiceTest {
    
    @Mock
    private PermissionDomainService permissionDomainService;
    
    private AuthorizationApplicationService authorizationApplicationService;
    
    @BeforeEach
    void setUp() {
        authorizationApplicationService = new AuthorizationApplicationService(permissionDomainService);
    }
    
    @Test
    void testCheckPermission_RBACAndABACBothPass_ReturnsTrue() {
        // Given
        CheckPermissionRequest request = new CheckPermissionRequest();
        request.setTenantId("default");
        request.setUserId("admin");
        request.setResource("user:profile");
        request.setAction("read");
        
        Map<String, Object> context = new HashMap<>();
        context.put("department", "IT");
        request.setContext(context);
        
        when(permissionDomainService.hasPermission("default", "admin", "user:profile", "read"))
            .thenReturn(true);
        when(permissionDomainService.checkAbacPolicy("default", "admin", "user:profile", "read", context))
            .thenReturn(true);
        
        // When
        CheckPermissionResponse response = authorizationApplicationService.checkPermission(request);
        
        // Then
        assertTrue(response.isAllowed());
        assertEquals("权限校验通过", response.getMessage());
        verify(permissionDomainService).hasPermission("default", "admin", "user:profile", "read");
        verify(permissionDomainService).checkAbacPolicy("default", "admin", "user:profile", "read", context);
    }
    
    @Test
    void testCheckPermission_RBACFails_ReturnsFalse() {
        // Given
        CheckPermissionRequest request = new CheckPermissionRequest();
        request.setTenantId("default");
        request.setUserId("guest");
        request.setResource("user:profile");
        request.setAction("write");
        
        Map<String, Object> context = new HashMap<>();
        request.setContext(context);
        
        when(permissionDomainService.hasPermission("default", "guest", "user:profile", "write"))
            .thenReturn(false);
        
        // When
        CheckPermissionResponse response = authorizationApplicationService.checkPermission(request);
        
        // Then
        assertFalse(response.isAllowed());
        assertEquals("用户没有访问该资源的权限", response.getMessage());
        verify(permissionDomainService).hasPermission("default", "guest", "user:profile", "write");
        verify(permissionDomainService, never()).checkAbacPolicy(any(), any(), any(), any(), any());
    }
    
    @Test
    void testCheckPermission_RBACPassesButABACFails_ReturnsFalse() {
        // Given
        CheckPermissionRequest request = new CheckPermissionRequest();
        request.setTenantId("default");
        request.setUserId("admin");
        request.setResource("user:profile");
        request.setAction("read");
        
        Map<String, Object> context = new HashMap<>();
        context.put("department", "HR");
        request.setContext(context);
        
        when(permissionDomainService.hasPermission("default", "admin", "user:profile", "read"))
            .thenReturn(true);
        when(permissionDomainService.checkAbacPolicy("default", "admin", "user:profile", "read", context))
            .thenReturn(false);
        
        // When
        CheckPermissionResponse response = authorizationApplicationService.checkPermission(request);
        
        // Then
        assertFalse(response.isAllowed());
        assertEquals("用户不满足访问该资源的条件", response.getMessage());
        verify(permissionDomainService).hasPermission("default", "admin", "user:profile", "read");
        verify(permissionDomainService).checkAbacPolicy("default", "admin", "user:profile", "read", context);
    }
    
    @Test
    void testCheckPermission_NoContext_OnlyRBACCheck() {
        // Given
        CheckPermissionRequest request = new CheckPermissionRequest();
        request.setTenantId("default");
        request.setUserId("admin");
        request.setResource("user:profile");
        request.setAction("read");
        request.setContext(null);
        
        when(permissionDomainService.hasPermission("default", "admin", "user:profile", "read"))
            .thenReturn(true);
        
        // When
        CheckPermissionResponse response = authorizationApplicationService.checkPermission(request);
        
        // Then
        assertTrue(response.isAllowed());
        assertEquals("权限校验通过", response.getMessage());
        verify(permissionDomainService).hasPermission("default", "admin", "user:profile", "read");
        verify(permissionDomainService, never()).checkAbacPolicy(any(), any(), any(), any(), any());
    }
    
    @Test
    void testCheckPermission_EmptyContext_OnlyRBACCheck() {
        // Given
        CheckPermissionRequest request = new CheckPermissionRequest();
        request.setTenantId("default");
        request.setUserId("admin");
        request.setResource("user:profile");
        request.setAction("read");
        request.setContext(new HashMap<>());
        
        when(permissionDomainService.hasPermission("default", "admin", "user:profile", "read"))
            .thenReturn(true);
        
        // When
        CheckPermissionResponse response = authorizationApplicationService.checkPermission(request);
        
        // Then
        assertTrue(response.isAllowed());
        assertEquals("权限校验通过", response.getMessage());
        verify(permissionDomainService).hasPermission("default", "admin", "user:profile", "read");
        verify(permissionDomainService, never()).checkAbacPolicy(any(), any(), any(), any(), any());
    }
    
    @Test
    void testCheckPermission_SystemAdminAccess_ReturnsTrue() {
        // Given
        CheckPermissionRequest request = new CheckPermissionRequest();
        request.setTenantId("default");
        request.setUserId("admin");
        request.setResource("system:config");
        request.setAction("write");
        
        Map<String, Object> context = new HashMap<>();
        context.put("level", "5");
        request.setContext(context);
        
        when(permissionDomainService.hasPermission("default", "admin", "system:config", "write"))
            .thenReturn(true);
        when(permissionDomainService.checkAbacPolicy("default", "admin", "system:config", "write", context))
            .thenReturn(true);
        
        // When
        CheckPermissionResponse response = authorizationApplicationService.checkPermission(request);
        
        // Then
        assertTrue(response.isAllowed());
        assertEquals("权限校验通过", response.getMessage());
    }
    
    @Test
    void testCheckPermission_CrossTenantAccess_ReturnsFalse() {
        // Given
        CheckPermissionRequest request = new CheckPermissionRequest();
        request.setTenantId("tenant1");
        request.setUserId("admin");
        request.setResource("user:profile");
        request.setAction("read");
        
        Map<String, Object> context = new HashMap<>();
        request.setContext(context);
        
        when(permissionDomainService.hasPermission("tenant1", "admin", "user:profile", "read"))
            .thenReturn(false);
        
        // When
        CheckPermissionResponse response = authorizationApplicationService.checkPermission(request);
        
        // Then
        assertFalse(response.isAllowed());
        assertEquals("用户没有访问该资源的权限", response.getMessage());
    }
}
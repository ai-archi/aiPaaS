package com.aixone.tech.auth.authorization.interfaces.rest;

import com.aixone.tech.auth.authorization.application.dto.CheckPermissionRequest;
import com.aixone.tech.auth.authorization.domain.model.Permission;
import com.aixone.tech.auth.authorization.domain.model.Role;
import com.aixone.tech.auth.authorization.domain.model.UserRole;
import com.aixone.tech.auth.authorization.domain.model.AbacPolicy;
import com.aixone.tech.auth.authorization.domain.repository.PermissionRepository;
import com.aixone.tech.auth.authorization.domain.repository.RoleRepository;
import com.aixone.tech.auth.authorization.domain.repository.UserRoleRepository;
import com.aixone.tech.auth.authorization.domain.repository.AbacPolicyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 授权控制器集成测试
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
class AuthorizationControllerIntegrationTest {
    
    @MockBean
    private PermissionRepository permissionRepository;
    
    @MockBean
    private RoleRepository roleRepository;
    
    @MockBean
    private UserRoleRepository userRoleRepository;
    
    @MockBean
    private AbacPolicyRepository abacPolicyRepository;
    
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private MockMvc mockMvc;
    
    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    
    @Test
    void testCheckPermission_AdminUserWithValidPermission_ReturnsTrue() throws Exception {
        // Given
        CheckPermissionRequest request = new CheckPermissionRequest();
        request.setTenantId("default");
        request.setUserId("admin");
        request.setResource("user:profile");
        request.setAction("read");
        
        Map<String, Object> context = new HashMap<>();
        context.put("department", "IT");
        request.setContext(context);
        
        // Mock 用户角色
        UserRole userRole = new UserRole("ur_admin_001", "default", "admin", "role_admin");
        when(userRoleRepository.findByTenantIdAndUserId("default", "admin"))
            .thenReturn(List.of(userRole));
        
        // Mock 角色权限
        Role role = new Role("role_admin", "default", "系统管理员", "拥有所有权限", 
            List.of("perm_user_read", "perm_user_write"));
        when(roleRepository.findByTenantIdAndRoleIdIn("default", List.of("role_admin")))
            .thenReturn(List.of(role));
        
        // Mock 权限
        Permission permission = new Permission("perm_user_read", "default", "用户查看", "user:profile", "read", "查看用户基本信息");
        when(permissionRepository.findByTenantIdAndPermissionIdIn("default", List.of("perm_user_read", "perm_user_write")))
            .thenReturn(List.of(permission));
        
        // Mock ABAC 策略
        when(abacPolicyRepository.findByTenantIdAndResourceAndAction("default", "user:profile", "read"))
            .thenReturn(Collections.emptyList());
        
        // When & Then
        mockMvc.perform(post("/api/v1/auth/check-permission")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.allowed").value(true))
                .andExpect(jsonPath("$.message").value("权限校验通过"));
    }
    
    @Test
    void testCheckPermission_GuestUserWithInvalidPermission_ReturnsFalse() throws Exception {
        // Given
        CheckPermissionRequest request = new CheckPermissionRequest();
        request.setTenantId("default");
        request.setUserId("guest");
        request.setResource("system:config");
        request.setAction("write");
        
        Map<String, Object> context = new HashMap<>();
        request.setContext(context);
        
        // Mock 用户没有角色
        when(userRoleRepository.findByTenantIdAndUserId("default", "guest"))
            .thenReturn(Collections.emptyList());
        
        // When & Then
        mockMvc.perform(post("/api/v1/auth/check-permission")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.allowed").value(false))
                .andExpect(jsonPath("$.message").value("用户没有访问该资源的权限"));
    }
}
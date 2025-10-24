package com.aixone.tech.auth.authorization.domain.service;

import com.aixone.tech.auth.authorization.domain.model.Permission;
import com.aixone.tech.auth.authorization.domain.model.Role;
import com.aixone.tech.auth.authorization.domain.model.UserRole;
import com.aixone.tech.auth.authorization.domain.model.AbacPolicy;
import com.aixone.tech.auth.authorization.domain.repository.PermissionRepository;
import com.aixone.tech.auth.authorization.domain.repository.RoleRepository;
import com.aixone.tech.auth.authorization.domain.repository.UserRoleRepository;
import com.aixone.tech.auth.authorization.domain.repository.AbacPolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 权限领域服务测试
 */
@ExtendWith(MockitoExtension.class)
class PermissionDomainServiceTest {
    
    @Mock
    private PermissionRepository permissionRepository;
    
    @Mock
    private RoleRepository roleRepository;
    
    @Mock
    private UserRoleRepository userRoleRepository;
    
    @Mock
    private AbacPolicyRepository abacPolicyRepository;
    
    private PermissionDomainService permissionDomainService;
    
    @BeforeEach
    void setUp() {
        permissionDomainService = new PermissionDomainService(
            permissionRepository, roleRepository, userRoleRepository, abacPolicyRepository
        );
    }
    
    @Test
    void testHasPermission_UserHasPermission_ReturnsTrue() {
        // Given
        String tenantId = "default";
        String userId = "admin";
        String resource = "user:profile";
        String action = "read";
        
        // 模拟用户角色
        UserRole userRole = new UserRole("ur_admin_001", tenantId, userId, "role_admin");
        when(userRoleRepository.findByTenantIdAndUserId(tenantId, userId))
            .thenReturn(List.of(userRole));
        
        // 模拟角色权限
        Role role = new Role("role_admin", tenantId, "系统管理员", "拥有所有权限", 
            List.of("perm_user_read", "perm_user_write"));
        when(roleRepository.findByTenantIdAndRoleIdIn(tenantId, List.of("role_admin")))
            .thenReturn(List.of(role));
        
        // 模拟权限
        Permission permission = new Permission("perm_user_read", tenantId, "用户查看", resource, action, "查看用户基本信息");
        when(permissionRepository.findByTenantIdAndPermissionIdIn(tenantId, List.of("perm_user_read", "perm_user_write")))
            .thenReturn(List.of(permission));
        
        // When
        boolean result = permissionDomainService.hasPermission(tenantId, userId, resource, action);
        
        // Then
        assertTrue(result);
        verify(userRoleRepository).findByTenantIdAndUserId(tenantId, userId);
        verify(roleRepository).findByTenantIdAndRoleIdIn(tenantId, List.of("role_admin"));
        verify(permissionRepository).findByTenantIdAndPermissionIdIn(tenantId, List.of("perm_user_read", "perm_user_write"));
    }
    
    @Test
    void testHasPermission_UserHasNoRoles_ReturnsFalse() {
        // Given
        String tenantId = "default";
        String userId = "guest";
        String resource = "user:profile";
        String action = "read";
        
        when(userRoleRepository.findByTenantIdAndUserId(tenantId, userId))
            .thenReturn(Collections.emptyList());
        
        // When
        boolean result = permissionDomainService.hasPermission(tenantId, userId, resource, action);
        
        // Then
        assertFalse(result);
        verify(userRoleRepository).findByTenantIdAndUserId(tenantId, userId);
        verifyNoInteractions(roleRepository);
        verifyNoInteractions(permissionRepository);
    }
    
    @Test
    void testHasPermission_UserHasNoMatchingPermission_ReturnsFalse() {
        // Given
        String tenantId = "default";
        String userId = "guest";
        String resource = "user:profile";
        String action = "write";
        
        UserRole userRole = new UserRole("ur_guest_001", tenantId, userId, "role_guest");
        when(userRoleRepository.findByTenantIdAndUserId(tenantId, userId))
            .thenReturn(List.of(userRole));
        
        Role role = new Role("role_guest", tenantId, "访客", "访客角色", List.of("perm_user_read"));
        when(roleRepository.findByTenantIdAndRoleIdIn(tenantId, List.of("role_guest")))
            .thenReturn(List.of(role));
        
        Permission permission = new Permission("perm_user_read", tenantId, "用户查看", "user:profile", "read", "查看用户基本信息");
        when(permissionRepository.findByTenantIdAndPermissionIdIn(tenantId, List.of("perm_user_read")))
            .thenReturn(List.of(permission));
        
        // When
        boolean result = permissionDomainService.hasPermission(tenantId, userId, resource, action);
        
        // Then
        assertFalse(result);
    }
    
    @Test
    void testCheckAbacPolicy_NoPolicies_ReturnsTrue() {
        // Given
        String tenantId = "default";
        String userId = "admin";
        String resource = "user:profile";
        String action = "read";
        Map<String, Object> context = new HashMap<>();
        
        when(abacPolicyRepository.findByTenantIdAndResourceAndAction(tenantId, resource, action))
            .thenReturn(Collections.emptyList());
        
        // When
        boolean result = permissionDomainService.checkAbacPolicy(tenantId, userId, resource, action, context);
        
        // Then
        assertTrue(result);
        verify(abacPolicyRepository).findByTenantIdAndResourceAndAction(tenantId, resource, action);
    }
    
    @Test
    void testCheckAbacPolicy_PolicyPasses_ReturnsTrue() {
        // Given
        String tenantId = "default";
        String userId = "admin";
        String resource = "user:profile";
        String action = "read";
        Map<String, Object> context = new HashMap<>();
        context.put("department", "IT");
        context.put("level", "3");
        
        Map<String, Object> policyAttributes = new HashMap<>();
        policyAttributes.put("department", "IT");
        policyAttributes.put("level", "3");
        
        AbacPolicy policy = new AbacPolicy("policy_test", tenantId, "测试策略", "测试策略描述", 
            resource, action, "user.department == resource.department", policyAttributes);
        
        when(abacPolicyRepository.findByTenantIdAndResourceAndAction(tenantId, resource, action))
            .thenReturn(List.of(policy));
        
        // When
        boolean result = permissionDomainService.checkAbacPolicy(tenantId, userId, resource, action, context);
        
        // Then
        assertTrue(result);
    }
    
    @Test
    void testCheckAbacPolicy_PolicyFails_ReturnsFalse() {
        // Given
        String tenantId = "default";
        String userId = "admin";
        String resource = "user:profile";
        String action = "read";
        Map<String, Object> context = new HashMap<>();
        context.put("department", "HR");
        context.put("level", "2");
        
        Map<String, Object> policyAttributes = new HashMap<>();
        policyAttributes.put("department", "IT");
        policyAttributes.put("level", "3");
        
        AbacPolicy policy = new AbacPolicy("policy_test", tenantId, "测试策略", "测试策略描述", 
            resource, action, "user.department == resource.department", policyAttributes);
        
        when(abacPolicyRepository.findByTenantIdAndResourceAndAction(tenantId, resource, action))
            .thenReturn(List.of(policy));
        
        // When
        boolean result = permissionDomainService.checkAbacPolicy(tenantId, userId, resource, action, context);
        
        // Then
        assertFalse(result);
    }
    
    @Test
    void testGetUserPermissions_ReturnsUserPermissions() {
        // Given
        String tenantId = "default";
        String userId = "admin";
        
        UserRole userRole = new UserRole("ur_admin_001", tenantId, userId, "role_admin");
        when(userRoleRepository.findByTenantIdAndUserId(tenantId, userId))
            .thenReturn(List.of(userRole));
        
        Role role = new Role("role_admin", tenantId, "系统管理员", "拥有所有权限", 
            List.of("perm_user_read", "perm_user_write"));
        when(roleRepository.findByTenantIdAndRoleIdIn(tenantId, List.of("role_admin")))
            .thenReturn(List.of(role));
        
        Permission permission1 = new Permission("perm_user_read", tenantId, "用户查看", "user:profile", "read", "查看用户基本信息");
        Permission permission2 = new Permission("perm_user_write", tenantId, "用户编辑", "user:profile", "write", "编辑用户基本信息");
        when(permissionRepository.findByTenantIdAndPermissionIdIn(tenantId, List.of("perm_user_read", "perm_user_write")))
            .thenReturn(List.of(permission1, permission2));
        
        // When
        List<Permission> result = permissionDomainService.getUserPermissions(tenantId, userId);
        
        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(p -> p.getPermissionId().equals("perm_user_read")));
        assertTrue(result.stream().anyMatch(p -> p.getPermissionId().equals("perm_user_write")));
    }
    
    @Test
    void testGetUserPermissions_UserHasNoRoles_ReturnsEmptyList() {
        // Given
        String tenantId = "default";
        String userId = "guest";
        
        when(userRoleRepository.findByTenantIdAndUserId(tenantId, userId))
            .thenReturn(Collections.emptyList());
        
        // When
        List<Permission> result = permissionDomainService.getUserPermissions(tenantId, userId);
        
        // Then
        assertTrue(result.isEmpty());
    }
}

package com.aixone.tech.auth.authorization.application.service;

import com.aixone.tech.auth.authorization.application.dto.CreateRoleRequest;
import com.aixone.tech.auth.authorization.application.dto.RoleResponse;
import com.aixone.tech.auth.authorization.application.dto.UpdateRoleRequest;
import com.aixone.tech.auth.authorization.domain.model.Permission;
import com.aixone.tech.auth.authorization.domain.model.Role;
import com.aixone.tech.auth.authorization.domain.repository.PermissionRepository;
import com.aixone.tech.auth.authorization.domain.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 角色管理应用服务测试
 */
@ExtendWith(MockitoExtension.class)
class RoleManagementApplicationServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private RoleManagementApplicationService roleManagementApplicationService;

    private String tenantId;
    private String roleId;
    private Role testRole;

    @BeforeEach
    void setUp() {
        tenantId = "test-tenant";
        roleId = "role-123";
        
        testRole = new Role(
            roleId,
            tenantId,
            "Test Role",
            "Test role description",
            Arrays.asList("perm-1", "perm-2")
        );
    }

    @Test
    void testCreateRole_Success() {
        // Arrange
        CreateRoleRequest request = new CreateRoleRequest(
            tenantId,
            "New Role",
            "New role description",
            Arrays.asList("perm-1", "perm-2")
        );

        when(roleRepository.existsByTenantIdAndName(tenantId, "New Role")).thenReturn(false);
        when(permissionRepository.findByTenantIdAndPermissionIdIn(eq(tenantId), any()))
            .thenReturn(Arrays.asList(new Permission(), new Permission()));
        when(roleRepository.save(any(Role.class))).thenReturn(testRole);

        // Act
        RoleResponse response = roleManagementApplicationService.createRole(request);

        // Assert
        assertNotNull(response);
        assertEquals(roleId, response.getRoleId());
        assertEquals(tenantId, response.getTenantId());
        verify(roleRepository).existsByTenantIdAndName(tenantId, "New Role");
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void testCreateRole_DuplicateName_ThrowsException() {
        // Arrange
        CreateRoleRequest request = new CreateRoleRequest(
            tenantId,
            "Existing Role",
            "Existing role description",
            Arrays.asList("perm-1")
        );

        when(roleRepository.existsByTenantIdAndName(tenantId, "Existing Role")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            roleManagementApplicationService.createRole(request);
        });

        verify(roleRepository).existsByTenantIdAndName(tenantId, "Existing Role");
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void testUpdateRole_Success() {
        // Arrange
        UpdateRoleRequest request = new UpdateRoleRequest(
            "Updated Role",
            "Updated description",
            Arrays.asList("perm-1", "perm-2", "perm-3")
        );

        when(roleRepository.findByTenantIdAndRoleId(tenantId, roleId)).thenReturn(testRole);
        when(roleRepository.existsByTenantIdAndName(tenantId, "Updated Role")).thenReturn(false);
        when(permissionRepository.findByTenantIdAndPermissionIdIn(eq(tenantId), any()))
            .thenReturn(Arrays.asList(new Permission(), new Permission(), new Permission()));
        when(roleRepository.save(any(Role.class))).thenReturn(testRole);

        // Act
        RoleResponse response = roleManagementApplicationService.updateRole(tenantId, roleId, request);

        // Assert
        assertNotNull(response);
        verify(roleRepository).findByTenantIdAndRoleId(tenantId, roleId);
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void testUpdateRole_NotFound_ThrowsException() {
        // Arrange
        UpdateRoleRequest request = new UpdateRoleRequest(
            "Updated Role",
            "Updated description",
            Arrays.asList("perm-1")
        );

        when(roleRepository.findByTenantIdAndRoleId(tenantId, roleId)).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            roleManagementApplicationService.updateRole(tenantId, roleId, request);
        });

        verify(roleRepository).findByTenantIdAndRoleId(tenantId, roleId);
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void testUpdateRole_DuplicateName_ThrowsException() {
        // Arrange
        UpdateRoleRequest request = new UpdateRoleRequest(
            "Duplicate Role",
            "Updated description",
            Arrays.asList("perm-1")
        );

        when(roleRepository.findByTenantIdAndRoleId(tenantId, roleId)).thenReturn(testRole);
        when(roleRepository.existsByTenantIdAndName(tenantId, "Duplicate Role")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            roleManagementApplicationService.updateRole(tenantId, roleId, request);
        });

        verify(roleRepository).findByTenantIdAndRoleId(tenantId, roleId);
        verify(roleRepository).existsByTenantIdAndName(tenantId, "Duplicate Role");
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void testDeleteRole_Success() {
        // Arrange
        when(roleRepository.findByTenantIdAndRoleId(tenantId, roleId)).thenReturn(testRole);
        
        // Act
        roleManagementApplicationService.deleteRole(tenantId, roleId);

        // Assert
        verify(roleRepository).findByTenantIdAndRoleId(tenantId, roleId);
        verify(roleRepository).deleteByTenantIdAndRoleId(tenantId, roleId);
    }

    @Test
    void testGetRoleById_Success() {
        // Arrange
        when(roleRepository.findByTenantIdAndRoleId(tenantId, roleId)).thenReturn(testRole);

        // Act
        RoleResponse response = roleManagementApplicationService.getRole(tenantId, roleId);

        // Assert
        assertNotNull(response);
        assertEquals(roleId, response.getRoleId());
        assertEquals(tenantId, response.getTenantId());
        verify(roleRepository).findByTenantIdAndRoleId(tenantId, roleId);
    }

    @Test
    void testGetRoleById_NotFound_ThrowsException() {
        // Arrange
        when(roleRepository.findByTenantIdAndRoleId(tenantId, roleId)).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            roleManagementApplicationService.getRole(tenantId, roleId);
        });

        verify(roleRepository).findByTenantIdAndRoleId(tenantId, roleId);
    }

    @Test
    void testGetAllRoles_Success() {
        // Arrange
        Role role1 = new Role("role-1", tenantId, "Role 1", "Description 1", Arrays.asList("perm-1"));
        Role role2 = new Role("role-2", tenantId, "Role 2", "Description 2", Arrays.asList("perm-2"));
        List<Role> roles = Arrays.asList(role1, role2);

        when(roleRepository.findByTenantId(tenantId)).thenReturn(roles);

        // Act
        List<RoleResponse> responses = roleManagementApplicationService.getRoles(tenantId);

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        verify(roleRepository).findByTenantId(tenantId);
    }

    @Test
    void testGetAllRoles_Empty() {
        // Arrange
        when(roleRepository.findByTenantId(tenantId)).thenReturn(Arrays.asList());

        // Act
        List<RoleResponse> responses = roleManagementApplicationService.getRoles(tenantId);

        // Assert
        assertNotNull(responses);
        assertEquals(0, responses.size());
        verify(roleRepository).findByTenantId(tenantId);
    }
}


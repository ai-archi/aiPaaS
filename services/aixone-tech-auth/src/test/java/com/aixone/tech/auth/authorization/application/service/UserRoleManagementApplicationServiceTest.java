package com.aixone.tech.auth.authorization.application.service;

import com.aixone.tech.auth.authorization.application.command.AssignUserRoleCommand;
import com.aixone.tech.auth.authorization.application.dto.UserRoleResponse;
import com.aixone.tech.auth.authorization.domain.model.Role;
import com.aixone.tech.auth.authorization.domain.model.UserRole;
import com.aixone.tech.auth.authorization.domain.repository.RoleRepository;
import com.aixone.tech.auth.authorization.domain.repository.UserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * 用户角色管理应用服务测试
 */
@ExtendWith(MockitoExtension.class)
class UserRoleManagementApplicationServiceTest {

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserRoleManagementApplicationService userRoleManagementApplicationService;

    private String tenantId;
    private String userId;
    private String roleId;
    private String userRoleId;
    private Role testRole;
    private UserRole testUserRole;

    @BeforeEach
    void setUp() {
        tenantId = "test-tenant";
        userId = "test-user";
        roleId = "test-role";
        userRoleId = UUID.randomUUID().toString();

        testRole = new Role(
            roleId,
            tenantId,
            "Test Role",
            "Test role description",
            Arrays.asList("permission1", "permission2")
        );
        testRole.setCreatedAt(LocalDateTime.now());
        testRole.setUpdatedAt(LocalDateTime.now());

        testUserRole = new UserRole(
            userRoleId,
            tenantId,
            userId,
            roleId,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

    @Test
    void testAssignUserRole_Success() {
        // Arrange
        AssignUserRoleCommand command = new AssignUserRoleCommand(tenantId, userId, roleId);

        when(roleRepository.findByTenantIdAndRoleId(tenantId, roleId)).thenReturn(testRole);
        when(userRoleRepository.existsByTenantIdAndUserIdAndRoleId(tenantId, userId, roleId)).thenReturn(false);
        when(userRoleRepository.save(any(UserRole.class))).thenReturn(testUserRole);

        // Act
        UserRoleResponse response = userRoleManagementApplicationService.assignUserRole(command);

        // Assert
        assertNotNull(response);
        assertEquals(userRoleId, response.getUserRoleId());
        assertEquals(tenantId, response.getTenantId());
        assertEquals(userId, response.getUserId());
        assertEquals(roleId, response.getRoleId());
        assertEquals("Test Role", response.getRoleName());
        assertEquals("Test role description", response.getRoleDescription());

        verify(roleRepository).findByTenantIdAndRoleId(tenantId, roleId);
        verify(userRoleRepository).existsByTenantIdAndUserIdAndRoleId(tenantId, userId, roleId);
        verify(userRoleRepository).save(any(UserRole.class));
    }

    @Test
    void testAssignUserRole_RoleNotFound_ThrowsException() {
        // Arrange
        AssignUserRoleCommand command = new AssignUserRoleCommand(tenantId, userId, roleId);

        when(roleRepository.findByTenantIdAndRoleId(tenantId, roleId)).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userRoleManagementApplicationService.assignUserRole(command);
        });

        verify(roleRepository).findByTenantIdAndRoleId(tenantId, roleId);
        verify(userRoleRepository, never()).existsByTenantIdAndUserIdAndRoleId(anyString(), anyString(), anyString());
        verify(userRoleRepository, never()).save(any(UserRole.class));
    }

    @Test
    void testAssignUserRole_UserAlreadyHasRole_ThrowsException() {
        // Arrange
        AssignUserRoleCommand command = new AssignUserRoleCommand(tenantId, userId, roleId);

        when(roleRepository.findByTenantIdAndRoleId(tenantId, roleId)).thenReturn(testRole);
        when(userRoleRepository.existsByTenantIdAndUserIdAndRoleId(tenantId, userId, roleId)).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userRoleManagementApplicationService.assignUserRole(command);
        });

        verify(roleRepository).findByTenantIdAndRoleId(tenantId, roleId);
        verify(userRoleRepository).existsByTenantIdAndUserIdAndRoleId(tenantId, userId, roleId);
        verify(userRoleRepository, never()).save(any(UserRole.class));
    }

    @Test
    void testRemoveUserRole_Success() {
        // Arrange
        when(userRoleRepository.existsByTenantIdAndUserIdAndRoleId(tenantId, userId, roleId)).thenReturn(true);

        // Act
        userRoleManagementApplicationService.removeUserRole(tenantId, userId, roleId);

        // Assert
        verify(userRoleRepository).existsByTenantIdAndUserIdAndRoleId(tenantId, userId, roleId);
        verify(userRoleRepository).deleteByTenantIdAndUserIdAndRoleId(tenantId, userId, roleId);
    }

    @Test
    void testRemoveUserRole_UserRoleNotFound_ThrowsException() {
        // Arrange
        when(userRoleRepository.existsByTenantIdAndUserIdAndRoleId(tenantId, userId, roleId)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userRoleManagementApplicationService.removeUserRole(tenantId, userId, roleId);
        });

        verify(userRoleRepository).existsByTenantIdAndUserIdAndRoleId(tenantId, userId, roleId);
        verify(userRoleRepository, never()).deleteByTenantIdAndUserIdAndRoleId(anyString(), anyString(), anyString());
    }

    @Test
    void testGetUserRoles_Success() {
        // Arrange
        UserRole userRole1 = new UserRole(
            UUID.randomUUID().toString(),
            tenantId,
            userId,
            "role1",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        UserRole userRole2 = new UserRole(
            UUID.randomUUID().toString(),
            tenantId,
            userId,
            "role2",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        List<UserRole> userRoles = Arrays.asList(userRole1, userRole2);

        Role role1 = new Role("role1", tenantId, "Role 1", "Description 1", Arrays.asList("perm1"));
        Role role2 = new Role("role2", tenantId, "Role 2", "Description 2", Arrays.asList("perm2"));

        when(userRoleRepository.findByTenantIdAndUserId(tenantId, userId)).thenReturn(userRoles);
        when(roleRepository.findByTenantIdAndRoleId(tenantId, "role1")).thenReturn(role1);
        when(roleRepository.findByTenantIdAndRoleId(tenantId, "role2")).thenReturn(role2);

        // Act
        List<UserRoleResponse> responses = userRoleManagementApplicationService.getUserRoles(tenantId, userId);

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Role 1", responses.get(0).getRoleName());
        assertEquals("Role 2", responses.get(1).getRoleName());

        verify(userRoleRepository).findByTenantIdAndUserId(tenantId, userId);
        verify(roleRepository).findByTenantIdAndRoleId(tenantId, "role1");
        verify(roleRepository).findByTenantIdAndRoleId(tenantId, "role2");
    }

    @Test
    void testGetUserRoles_WithNullRole_HandlesGracefully() {
        // Arrange
        UserRole userRole = new UserRole(
            UUID.randomUUID().toString(),
            tenantId,
            userId,
            "unknown-role",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        List<UserRole> userRoles = Arrays.asList(userRole);

        when(userRoleRepository.findByTenantIdAndUserId(tenantId, userId)).thenReturn(userRoles);
        when(roleRepository.findByTenantIdAndRoleId(tenantId, "unknown-role")).thenReturn(null);

        // Act
        List<UserRoleResponse> responses = userRoleManagementApplicationService.getUserRoles(tenantId, userId);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("未知角色", responses.get(0).getRoleName());
        assertEquals("", responses.get(0).getRoleDescription());

        verify(userRoleRepository).findByTenantIdAndUserId(tenantId, userId);
        verify(roleRepository).findByTenantIdAndRoleId(tenantId, "unknown-role");
    }

    @Test
    void testHasRole_ReturnsTrue() {
        // Arrange
        when(userRoleRepository.existsByTenantIdAndUserIdAndRoleId(tenantId, userId, roleId)).thenReturn(true);

        // Act
        boolean hasRole = userRoleManagementApplicationService.hasRole(tenantId, userId, roleId);

        // Assert
        assertTrue(hasRole);
        verify(userRoleRepository).existsByTenantIdAndUserIdAndRoleId(tenantId, userId, roleId);
    }

    @Test
    void testHasRole_ReturnsFalse() {
        // Arrange
        when(userRoleRepository.existsByTenantIdAndUserIdAndRoleId(tenantId, userId, roleId)).thenReturn(false);

        // Act
        boolean hasRole = userRoleManagementApplicationService.hasRole(tenantId, userId, roleId);

        // Assert
        assertFalse(hasRole);
        verify(userRoleRepository).existsByTenantIdAndUserIdAndRoleId(tenantId, userId, roleId);
    }
}

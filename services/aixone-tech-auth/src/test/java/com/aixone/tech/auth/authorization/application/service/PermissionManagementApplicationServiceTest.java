package com.aixone.tech.auth.authorization.application.service;

import com.aixone.tech.auth.authorization.application.dto.CreatePermissionRequest;
import com.aixone.tech.auth.authorization.application.dto.PermissionResponse;
import com.aixone.tech.auth.authorization.application.dto.UpdatePermissionRequest;
import com.aixone.tech.auth.authorization.domain.model.Permission;
import com.aixone.tech.auth.authorization.domain.repository.PermissionRepository;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * 权限管理应用服务测试
 */
@ExtendWith(MockitoExtension.class)
class PermissionManagementApplicationServiceTest {

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private PermissionManagementApplicationService permissionManagementApplicationService;

    private String tenantId;
    private String permissionId;
    private Permission testPermission;

    @BeforeEach
    void setUp() {
        tenantId = "test-tenant";
        permissionId = "perm-123";
        
        testPermission = new Permission(
            permissionId,
            tenantId,
            "Test Permission",
            "test:resource",
            "read",
            "Test permission description"
        );
    }

    @Test
    void testCreatePermission_Success() {
        // Arrange
        CreatePermissionRequest request = new CreatePermissionRequest(
            tenantId,
            "New Permission",
            "new:resource",
            "write",
            "New permission description"
        );

        when(permissionRepository.existsByTenantIdAndName(tenantId, "New Permission")).thenReturn(false);
        when(permissionRepository.save(any(Permission.class))).thenReturn(testPermission);

        // Act
        PermissionResponse response = permissionManagementApplicationService.createPermission(request);

        // Assert
        assertNotNull(response);
        assertEquals(permissionId, response.getPermissionId());
        assertEquals(tenantId, response.getTenantId());
        verify(permissionRepository).existsByTenantIdAndName(tenantId, "New Permission");
        verify(permissionRepository).save(any(Permission.class));
    }

    @Test
    void testCreatePermission_DuplicateName_ThrowsException() {
        // Arrange
        CreatePermissionRequest request = new CreatePermissionRequest(
            tenantId,
            "Existing Permission",
            "existing:resource",
            "read",
            "Existing permission description"
        );

        when(permissionRepository.existsByTenantIdAndName(tenantId, "Existing Permission")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            permissionManagementApplicationService.createPermission(request);
        });

        verify(permissionRepository).existsByTenantIdAndName(tenantId, "Existing Permission");
        verify(permissionRepository, never()).save(any(Permission.class));
    }

    @Test
    void testUpdatePermission_Success() {
        // Arrange
        UpdatePermissionRequest request = new UpdatePermissionRequest(
            tenantId,
            "Updated Permission",
            "updated:resource",
            "write",
            "Updated description"
        );

        when(permissionRepository.findByTenantIdAndPermissionId(tenantId, permissionId)).thenReturn(testPermission);
        when(permissionRepository.existsByTenantIdAndName(tenantId, "Updated Permission")).thenReturn(false);
        when(permissionRepository.save(any(Permission.class))).thenReturn(testPermission);

        // Act
        PermissionResponse response = permissionManagementApplicationService.updatePermission(permissionId, request);

        // Assert
        assertNotNull(response);
        verify(permissionRepository).findByTenantIdAndPermissionId(tenantId, permissionId);
        verify(permissionRepository).save(any(Permission.class));
    }

    @Test
    void testUpdatePermission_NotFound_ThrowsException() {
        // Arrange
        UpdatePermissionRequest request = new UpdatePermissionRequest(
            tenantId,
            "Updated Permission",
            "updated:resource",
            "write",
            "Updated description"
        );

        when(permissionRepository.findByTenantIdAndPermissionId(tenantId, permissionId)).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            permissionManagementApplicationService.updatePermission(permissionId, request);
        });

        verify(permissionRepository).findByTenantIdAndPermissionId(tenantId, permissionId);
        verify(permissionRepository, never()).save(any(Permission.class));
    }

    @Test
    void testUpdatePermission_DuplicateName_ThrowsException() {
        // Arrange
        UpdatePermissionRequest request = new UpdatePermissionRequest(
            tenantId,
            "Duplicate Permission",
            "updated:resource",
            "write",
            "Updated description"
        );

        when(permissionRepository.findByTenantIdAndPermissionId(tenantId, permissionId)).thenReturn(testPermission);
        when(permissionRepository.existsByTenantIdAndName(tenantId, "Duplicate Permission")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            permissionManagementApplicationService.updatePermission(permissionId, request);
        });

        verify(permissionRepository).findByTenantIdAndPermissionId(tenantId, permissionId);
        verify(permissionRepository).existsByTenantIdAndName(tenantId, "Duplicate Permission");
        verify(permissionRepository, never()).save(any(Permission.class));
    }

    @Test
    void testDeletePermission_Success() {
        // Act
        permissionManagementApplicationService.deletePermission(tenantId, permissionId);

        // Assert
        verify(permissionRepository).deleteByTenantIdAndPermissionId(tenantId, permissionId);
    }

    @Test
    void testGetPermissionById_Success() {
        // Arrange
        when(permissionRepository.findByTenantIdAndPermissionId(tenantId, permissionId)).thenReturn(testPermission);

        // Act
        PermissionResponse response = permissionManagementApplicationService.getPermissionById(tenantId, permissionId);

        // Assert
        assertNotNull(response);
        assertEquals(permissionId, response.getPermissionId());
        assertEquals(tenantId, response.getTenantId());
        verify(permissionRepository).findByTenantIdAndPermissionId(tenantId, permissionId);
    }

    @Test
    void testGetPermissionById_NotFound_ThrowsException() {
        // Arrange
        when(permissionRepository.findByTenantIdAndPermissionId(tenantId, permissionId)).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            permissionManagementApplicationService.getPermissionById(tenantId, permissionId);
        });

        verify(permissionRepository).findByTenantIdAndPermissionId(tenantId, permissionId);
    }

    @Test
    void testGetAllPermissions_Success() {
        // Arrange
        Permission permission1 = new Permission("perm-1", tenantId, "Permission 1", "resource1", "read", "Description 1");
        Permission permission2 = new Permission("perm-2", tenantId, "Permission 2", "resource2", "write", "Description 2");
        List<Permission> permissions = Arrays.asList(permission1, permission2);

        when(permissionRepository.findByTenantId(tenantId)).thenReturn(permissions);

        // Act
        List<PermissionResponse> responses = permissionManagementApplicationService.getAllPermissions(tenantId);

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        verify(permissionRepository).findByTenantId(tenantId);
    }

    @Test
    void testGetAllPermissions_Empty() {
        // Arrange
        when(permissionRepository.findByTenantId(tenantId)).thenReturn(Arrays.asList());

        // Act
        List<PermissionResponse> responses = permissionManagementApplicationService.getAllPermissions(tenantId);

        // Assert
        assertNotNull(responses);
        assertEquals(0, responses.size());
        verify(permissionRepository).findByTenantId(tenantId);
    }
}


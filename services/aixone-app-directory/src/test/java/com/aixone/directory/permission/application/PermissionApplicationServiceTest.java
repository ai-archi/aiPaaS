package com.aixone.directory.permission.application;

import com.aixone.common.api.PageRequest;
import com.aixone.common.api.PageResult;
import com.aixone.directory.permission.domain.aggregate.Permission;
import com.aixone.directory.permission.domain.repository.PermissionRepository;
import com.aixone.directory.permission.domain.repository.RolePermissionRepository;
import com.aixone.directory.permission.infrastructure.persistence.PermissionJpaRepository;
import com.aixone.directory.permission.infrastructure.persistence.dbo.PermissionDbo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 权限应用服务单元测试
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("权限应用服务测试")
class PermissionApplicationServiceTest {

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private RolePermissionRepository rolePermissionRepository;

    @Mock
    private PermissionJpaRepository permissionJpaRepository;

    @InjectMocks
    private PermissionApplicationService permissionApplicationService;

    private String tenantId;
    private String permissionId;
    private Permission testPermission;

    @BeforeEach
    void setUp() {
        tenantId = "tenant-" + UUID.randomUUID().toString();
        permissionId = "permission-" + UUID.randomUUID().toString();

        testPermission = Permission.builder()
                .permissionId(permissionId)
                .tenantId(tenantId)
                .name("测试权限")
                .code("test:read")
                .resource("test")
                .action("read")
                .type(Permission.PermissionType.FUNCTIONAL)
                .description("测试权限描述")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("创建权限 - 成功")
    void testCreatePermission_Success() {
        // Given
        PermissionDto.CreatePermissionCommand command = PermissionDto.CreatePermissionCommand.builder()
                .tenantId(tenantId)
                .name("新权限")
                .code("new:read")
                .resource("new")
                .action("read")
                .type(Permission.PermissionType.FUNCTIONAL)
                .description("新权限描述")
                .build();

        when(permissionRepository.existsByTenantIdAndCode(tenantId, command.getCode())).thenReturn(false);
        when(permissionRepository.save(any(Permission.class))).thenAnswer(invocation -> {
            Permission permission = invocation.getArgument(0);
            permission.setPermissionId(permissionId);
            return permission;
        });

        // When
        PermissionDto.PermissionView result = permissionApplicationService.createPermission(command);

        // Then
        assertNotNull(result);
        assertNotNull(result.getPermissionId());
        assertEquals("新权限", result.getName());
        assertEquals("new:read", result.getCode());
        assertEquals("new", result.getResource());
        assertEquals("read", result.getAction());
        assertEquals(Permission.PermissionType.FUNCTIONAL, result.getType());
        assertEquals("新权限描述", result.getDescription());
        assertEquals(tenantId, result.getTenantId());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        verify(permissionRepository, times(1)).existsByTenantIdAndCode(tenantId, command.getCode());
        verify(permissionRepository, times(1)).save(any(Permission.class));
    }

    @Test
    @DisplayName("创建权限 - 权限编码已存在")
    void testCreatePermission_DuplicateCode_ThrowsException() {
        // Given
        PermissionDto.CreatePermissionCommand command = PermissionDto.CreatePermissionCommand.builder()
                .tenantId(tenantId)
                .name("新权限")
                .code("test:read")
                .resource("test")
                .action("read")
                .build();

        when(permissionRepository.existsByTenantIdAndCode(tenantId, command.getCode())).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            permissionApplicationService.createPermission(command);
        });

        assertEquals("权限编码已存在: test:read", exception.getMessage());
        verify(permissionRepository, times(1)).existsByTenantIdAndCode(tenantId, command.getCode());
        verify(permissionRepository, never()).save(any(Permission.class));
    }

    @Test
    @DisplayName("创建权限 - 名称为空")
    void testCreatePermission_EmptyName_ThrowsException() {
        // Given
        PermissionDto.CreatePermissionCommand command = PermissionDto.CreatePermissionCommand.builder()
                .tenantId(tenantId)
                .name("")
                .code("test:read")
                .resource("test")
                .action("read")
                .build();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            permissionApplicationService.createPermission(command);
        });

        verify(permissionRepository, never()).save(any(Permission.class));
    }

    @Test
    @DisplayName("创建权限 - 带ABAC条件")
    void testCreatePermission_WithAbacConditions_Success() {
        // Given
        Map<String, Object> abacConditions = new HashMap<>();
        abacConditions.put("user.department", "IT");

        PermissionDto.CreatePermissionCommand command = PermissionDto.CreatePermissionCommand.builder()
                .tenantId(tenantId)
                .name("新权限")
                .code("new:read")
                .resource("new")
                .action("read")
                .abacConditions(abacConditions)
                .build();

        when(permissionRepository.existsByTenantIdAndCode(tenantId, command.getCode())).thenReturn(false);
        when(permissionRepository.save(any(Permission.class))).thenAnswer(invocation -> {
            Permission permission = invocation.getArgument(0);
            permission.setPermissionId(permissionId);
            return permission;
        });

        // When
        PermissionDto.PermissionView result = permissionApplicationService.createPermission(command);

        // Then
        assertNotNull(result);
        assertNotNull(result.getAbacConditions());
        assertEquals(abacConditions, result.getAbacConditions());
    }

    @Test
    @DisplayName("更新权限 - 成功")
    void testUpdatePermission_Success() {
        // Given
        PermissionDto.UpdatePermissionCommand command = PermissionDto.UpdatePermissionCommand.builder()
                .name("更新后的权限")
                .code("updated:read")
                .resource("updated")
                .action("read")
                .type(Permission.PermissionType.DATA)
                .description("更新后的描述")
                .build();

        when(permissionRepository.findById(permissionId)).thenReturn(Optional.of(testPermission));
        when(permissionRepository.save(any(Permission.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        PermissionDto.PermissionView result = permissionApplicationService.updatePermission(permissionId, command);

        // Then
        assertNotNull(result);
        assertEquals("更新后的权限", result.getName());
        assertEquals("updated:read", result.getCode());
        assertEquals("updated", result.getResource());
        assertEquals("read", result.getAction());
        assertEquals(Permission.PermissionType.DATA, result.getType());
        assertEquals("更新后的描述", result.getDescription());

        verify(permissionRepository, times(1)).findById(permissionId);
        verify(permissionRepository, times(1)).save(any(Permission.class));
    }

    @Test
    @DisplayName("更新权限 - 权限不存在")
    void testUpdatePermission_NotFound_ThrowsException() {
        // Given
        PermissionDto.UpdatePermissionCommand command = PermissionDto.UpdatePermissionCommand.builder()
                .name("更新后的权限")
                .code("updated:read")
                .resource("updated")
                .action("read")
                .build();

        when(permissionRepository.findById(permissionId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            permissionApplicationService.updatePermission(permissionId, command);
        });

        assertEquals("权限不存在: " + permissionId, exception.getMessage());
        verify(permissionRepository, times(1)).findById(permissionId);
        verify(permissionRepository, never()).save(any(Permission.class));
    }

    @Test
    @DisplayName("删除权限 - 成功")
    void testDeletePermission_Success() {
        // Given
        when(permissionRepository.existsById(permissionId)).thenReturn(true);
        doNothing().when(permissionRepository).delete(permissionId);

        // When
        permissionApplicationService.deletePermission(permissionId);

        // Then
        verify(permissionRepository, times(1)).existsById(permissionId);
        verify(permissionRepository, times(1)).delete(permissionId);
    }

    @Test
    @DisplayName("删除权限 - 权限不存在")
    void testDeletePermission_NotFound_ThrowsException() {
        // Given
        when(permissionRepository.existsById(permissionId)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            permissionApplicationService.deletePermission(permissionId);
        });

        assertEquals("权限不存在: " + permissionId, exception.getMessage());
        verify(permissionRepository, times(1)).existsById(permissionId);
        verify(permissionRepository, never()).delete(anyString());
    }

    @Test
    @DisplayName("根据ID查找权限 - 成功")
    void testFindPermissionById_Success() {
        // Given
        when(permissionRepository.findById(permissionId)).thenReturn(Optional.of(testPermission));

        // When
        Optional<PermissionDto.PermissionView> result = permissionApplicationService.findPermissionById(permissionId, tenantId);

        // Then
        assertTrue(result.isPresent());
        PermissionDto.PermissionView view = result.get();
        assertEquals(permissionId, view.getPermissionId());
        assertEquals(tenantId, view.getTenantId());
        assertEquals("测试权限", view.getName());

        verify(permissionRepository, times(1)).findById(permissionId);
    }

    @Test
    @DisplayName("根据ID查找权限 - 权限不存在")
    void testFindPermissionById_NotFound_ReturnsEmpty() {
        // Given
        when(permissionRepository.findById(permissionId)).thenReturn(Optional.empty());

        // When
        Optional<PermissionDto.PermissionView> result = permissionApplicationService.findPermissionById(permissionId, tenantId);

        // Then
        assertTrue(result.isEmpty());
        verify(permissionRepository, times(1)).findById(permissionId);
    }

    @Test
    @DisplayName("根据ID查找权限 - 不属于当前租户")
    void testFindPermissionById_DifferentTenant_ReturnsEmpty() {
        // Given
        String otherTenantId = "other-tenant-" + UUID.randomUUID().toString();
        when(permissionRepository.findById(permissionId)).thenReturn(Optional.of(testPermission));

        // When
        Optional<PermissionDto.PermissionView> result = permissionApplicationService.findPermissionById(permissionId, otherTenantId);

        // Then
        assertTrue(result.isEmpty());
        verify(permissionRepository, times(1)).findById(permissionId);
    }

    @Test
    @DisplayName("分页查询权限 - 成功")
    void testFindPermissions_Success() {
        // Given
        PageRequest pageRequest = new PageRequest(1, 20);
        PermissionDbo dbo1 = createPermissionDbo("permission-1", tenantId, "test", "read");
        PermissionDbo dbo2 = createPermissionDbo("permission-2", tenantId, "test", "write");
        List<PermissionDbo> content = List.of(dbo1, dbo2);
        Page<PermissionDbo> page = new PageImpl<>(content, org.springframework.data.domain.PageRequest.of(0, 20), 2);

        when(permissionJpaRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        // When
        PageResult<PermissionDto.PermissionView> result = permissionApplicationService.findPermissions(
                pageRequest, tenantId, null, null);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotal());
        assertEquals(2, result.getList().size());
        verify(permissionJpaRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("分页查询权限 - 按资源过滤")
    void testFindPermissions_WithResourceFilter() {
        // Given
        PageRequest pageRequest = new PageRequest(1, 20);
        PermissionDbo dbo = createPermissionDbo("permission-1", tenantId, "test", "read");
        Page<PermissionDbo> page = new PageImpl<>(List.of(dbo), org.springframework.data.domain.PageRequest.of(0, 20), 1);

        when(permissionJpaRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        // When
        PageResult<PermissionDto.PermissionView> result = permissionApplicationService.findPermissions(
                pageRequest, tenantId, "test", null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals("test", result.getList().get(0).getResource());
    }

    @Test
    @DisplayName("分页查询权限 - 按操作过滤")
    void testFindPermissions_WithActionFilter() {
        // Given
        PageRequest pageRequest = new PageRequest(1, 20);
        PermissionDbo dbo = createPermissionDbo("permission-1", tenantId, "test", "read");
        Page<PermissionDbo> page = new PageImpl<>(List.of(dbo), org.springframework.data.domain.PageRequest.of(0, 20), 1);

        when(permissionJpaRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        // When
        PageResult<PermissionDto.PermissionView> result = permissionApplicationService.findPermissions(
                pageRequest, tenantId, null, "read");

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals("read", result.getList().get(0).getAction());
    }

    @Test
    @DisplayName("分配权限给角色 - 成功")
    void testAssignRolePermission_Success() {
        // Given
        String roleId = "role-" + UUID.randomUUID().toString();

        when(permissionRepository.findById(permissionId)).thenReturn(Optional.of(testPermission));
        doNothing().when(rolePermissionRepository).assignPermission(roleId, permissionId, tenantId);

        // When
        permissionApplicationService.assignRolePermission(roleId, permissionId, tenantId);

        // Then
        verify(permissionRepository, times(1)).findById(permissionId);
        verify(rolePermissionRepository, times(1)).assignPermission(roleId, permissionId, tenantId);
    }

    @Test
    @DisplayName("分配权限给角色 - 权限不存在")
    void testAssignRolePermission_PermissionNotFound_ThrowsException() {
        // Given
        String roleId = "role-" + UUID.randomUUID().toString();

        when(permissionRepository.findById(permissionId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            permissionApplicationService.assignRolePermission(roleId, permissionId, tenantId);
        });

        assertEquals("权限不存在: " + permissionId, exception.getMessage());
        verify(permissionRepository, times(1)).findById(permissionId);
        verify(rolePermissionRepository, never()).assignPermission(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("分配权限给角色 - 权限不属于当前租户")
    void testAssignRolePermission_DifferentTenant_ThrowsException() {
        // Given
        String roleId = "role-" + UUID.randomUUID().toString();
        String otherTenantId = "other-tenant-" + UUID.randomUUID().toString();

        when(permissionRepository.findById(permissionId)).thenReturn(Optional.of(testPermission));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            permissionApplicationService.assignRolePermission(roleId, permissionId, otherTenantId);
        });

        assertEquals("权限不属于当前租户", exception.getMessage());
        verify(permissionRepository, times(1)).findById(permissionId);
        verify(rolePermissionRepository, never()).assignPermission(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("移除角色权限 - 成功")
    void testRemoveRolePermission_Success() {
        // Given
        String roleId = "role-" + UUID.randomUUID().toString();
        doNothing().when(rolePermissionRepository).removePermission(roleId, permissionId);

        // When
        permissionApplicationService.removeRolePermission(roleId, permissionId);

        // Then
        verify(rolePermissionRepository, times(1)).removePermission(roleId, permissionId);
    }

    @Test
    @DisplayName("获取角色的权限列表 - 成功")
    void testGetRolePermissions_Success() {
        // Given
        String roleId = "role-" + UUID.randomUUID().toString();
        List<Permission> permissions = List.of(testPermission);

        when(rolePermissionRepository.findPermissionsByRoleId(roleId, tenantId)).thenReturn(permissions);

        // When
        List<PermissionDto.PermissionView> result = permissionApplicationService.getRolePermissions(roleId, tenantId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(permissionId, result.get(0).getPermissionId());
        verify(rolePermissionRepository, times(1)).findPermissionsByRoleId(roleId, tenantId);
    }

    /**
     * 创建测试用的PermissionDbo对象
     */
    private PermissionDbo createPermissionDbo(String id, String tenantId, String resource, String action) {
        return PermissionDbo.builder()
                .permissionId(id)
                .tenantId(tenantId)
                .name("测试权限")
                .code(resource + ":" + action)
                .resource(resource)
                .action(action)
                .type("FUNCTIONAL")
                .description("测试权限描述")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}


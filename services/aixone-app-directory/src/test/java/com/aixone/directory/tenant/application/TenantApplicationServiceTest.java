package com.aixone.directory.tenant.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aixone.directory.tenant.domain.aggregate.Tenant;
import com.aixone.directory.tenant.domain.aggregate.TenantStatus;
import com.aixone.directory.tenant.domain.repository.TenantRepository;

/**
 * 租户应用服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("租户应用服务测试")
class TenantApplicationServiceTest {

    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private TenantApplicationService tenantApplicationService;

    @BeforeEach
    void setUp() {
        // 设置在每个测试前执行的初始化代码
    }

    @Test
    @DisplayName("创建租户 - 成功")
    void testCreateTenant_Success() {
        // Given
        String tenantName = "测试租户";
        String groupId = "group-123";
        
        TenantDto.CreateTenantCommand command = TenantDto.CreateTenantCommand.builder()
                .name(tenantName)
                .groupId(groupId)
                .build();

        // Mock tenant repository save behavior
        doNothing().when(tenantRepository).save(any(Tenant.class));

        // When
        TenantDto.TenantView result = tenantApplicationService.createTenant(command);

        // Then
        assertNotNull(result);
        assertEquals(tenantName, result.getName());
        assertEquals(groupId, result.getGroupId());
        assertEquals(TenantStatus.ACTIVE.name(), result.getStatus());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getId());
        
        verify(tenantRepository, times(1)).save(any(Tenant.class));
    }

    @Test
    @DisplayName("创建租户 - 名称为空")
    void testCreateTenant_EmptyName_ThrowsException() {
        // Given
        TenantDto.CreateTenantCommand command = TenantDto.CreateTenantCommand.builder()
                .name("")
                .groupId("group-123")
                .build();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            tenantApplicationService.createTenant(command);
        });
    }

    @Test
    @DisplayName("创建租户 - 名称为null")
    void testCreateTenant_NullName_ThrowsException() {
        // Given
        TenantDto.CreateTenantCommand command = TenantDto.CreateTenantCommand.builder()
                .name(null)
                .groupId("group-123")
                .build();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            tenantApplicationService.createTenant(command);
        });
    }

    @Test
    @DisplayName("创建租户 - 命令为null")
    void testCreateTenant_NullCommand_ThrowsException() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            tenantApplicationService.createTenant(null);
        });
    }

    @Test
    @DisplayName("根据ID查找租户 - 成功")
    void testFindTenantById_Success() {
        // Given
        String tenantId = "tenant-123";
        String tenantName = "测试租户";
        String groupId = "group-123";
        
        Tenant mockTenant = mock(Tenant.class);
        when(mockTenant.getId()).thenReturn(tenantId);
        when(mockTenant.getName()).thenReturn(tenantName);
        when(mockTenant.getGroupId()).thenReturn(groupId);
        when(mockTenant.getStatus()).thenReturn(TenantStatus.ACTIVE);
        when(mockTenant.getCreatedAt()).thenReturn(LocalDateTime.now());

        when(tenantRepository.findById(tenantId)).thenReturn(Optional.of(mockTenant));

        // When
        Optional<TenantDto.TenantView> result = tenantApplicationService.findTenantById(tenantId);

        // Then
        assertTrue(result.isPresent());
        TenantDto.TenantView view = result.get();
        assertEquals(tenantId, view.getId());
        assertEquals(tenantName, view.getName());
        assertEquals(groupId, view.getGroupId());
        assertEquals(TenantStatus.ACTIVE.name(), view.getStatus());
    }

    @Test
    @DisplayName("根据ID查找租户 - 不存在")
    void testFindTenantById_NotFound() {
        // Given
        String tenantId = "non-existent-tenant";
        when(tenantRepository.findById(tenantId)).thenReturn(Optional.empty());

        // When
        Optional<TenantDto.TenantView> result = tenantApplicationService.findTenantById(tenantId);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("创建租户 - 无groupId")
    void testCreateTenant_WithoutGroupId() {
        // Given
        String tenantName = "测试租户";
        TenantDto.CreateTenantCommand command = TenantDto.CreateTenantCommand.builder()
                .name(tenantName)
                .groupId(null)
                .build();

        doNothing().when(tenantRepository).save(any(Tenant.class));

        // When
        TenantDto.TenantView result = tenantApplicationService.createTenant(command);

        // Then
        assertNotNull(result);
        assertEquals(tenantName, result.getName());
        assertNull(result.getGroupId());
        verify(tenantRepository, times(1)).save(any(Tenant.class));
    }

    @Test
    @DisplayName("创建多个租户 - 验证隔离性")
    void testCreateMultipleTenants_VerifiesIsolation() {
        // Given
        String tenantName1 = "租户1";
        String tenantName2 = "租户2";

        TenantDto.CreateTenantCommand command1 = TenantDto.CreateTenantCommand.builder()
                .name(tenantName1)
                .groupId("group-1")
                .build();

        TenantDto.CreateTenantCommand command2 = TenantDto.CreateTenantCommand.builder()
                .name(tenantName2)
                .groupId("group-2")
                .build();

        doNothing().when(tenantRepository).save(any(Tenant.class));

        // When
        TenantDto.TenantView result1 = tenantApplicationService.createTenant(command1);
        TenantDto.TenantView result2 = tenantApplicationService.createTenant(command2);

        // Then
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(tenantName1, result1.getName());
        assertEquals(tenantName2, result2.getName());
        assertNotEquals(result1.getId(), result2.getId());
    }
}


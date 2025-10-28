package com.aixone.workbench.quickentry.infrastructure.service;

import com.aixone.workbench.quickentry.application.dto.QuickEntryDTO;
import com.aixone.workbench.quickentry.domain.model.QuickEntry;
import com.aixone.workbench.quickentry.domain.repository.QuickEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 快捷入口服务测试
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("快捷入口服务测试")
class QuickEntryServiceImplTest {
    
    @Mock
    private QuickEntryRepository quickEntryRepository;
    
    @InjectMocks
    private QuickEntryServiceImpl quickEntryService;
    
    private UUID userId;
    private UUID tenantId;
    
    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        tenantId = UUID.randomUUID();
    }
    
    @Test
    @DisplayName("测试获取用户快捷入口")
    void testGetQuickEntries() {
        // Given
        QuickEntry entry = QuickEntry.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .tenantId(tenantId)
                .entryId(UUID.randomUUID())
                .name("测试入口")
                .displayOrder(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        when(quickEntryRepository.findByUserIdAndTenantIdOrderByDisplayOrderAsc(userId, tenantId))
                .thenReturn(List.of(entry));
        
        // When
        List<QuickEntryDTO> result = quickEntryService.getQuickEntries(userId, tenantId);
        
        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        verify(quickEntryRepository).findByUserIdAndTenantIdOrderByDisplayOrderAsc(userId, tenantId);
    }
    
    @Test
    @DisplayName("测试保存用户快捷入口")
    void testSaveQuickEntries() {
        // Given
        QuickEntryDTO entry = QuickEntryDTO.builder()
                .entryId(UUID.randomUUID())
                .name("新入口")
                .displayOrder(1)
                .build();
        
        // When
        quickEntryService.saveQuickEntries(userId, tenantId, List.of(entry));
        
        // Then
        verify(quickEntryRepository).deleteByUserIdAndTenantId(userId, tenantId);
        verify(quickEntryRepository).saveAll(anyList());
    }
    
    @Test
    @DisplayName("测试保存用户快捷入口 - 空列表")
    void testSaveQuickEntries_EmptyList() {
        // Given
        List<QuickEntryDTO> entries = List.of();
        
        // When
        quickEntryService.saveQuickEntries(userId, tenantId, entries);
        
        // Then
        verify(quickEntryRepository).deleteByUserIdAndTenantId(userId, tenantId);
        verify(quickEntryRepository).saveAll(argThat(collection -> {
            int count = 0;
            for (QuickEntry entry : collection) {
                count++;
            }
            return count == 0;
        }));
    }
    
    @Test
    @DisplayName("测试保存用户快捷入口 - 多个入口")
    void testSaveQuickEntries_MultipleEntries() {
        // Given
        List<QuickEntryDTO> entries = List.of(
                QuickEntryDTO.builder()
                        .entryId(UUID.randomUUID())
                        .name("入口1")
                        .displayOrder(1)
                        .build(),
                QuickEntryDTO.builder()
                        .entryId(UUID.randomUUID())
                        .name("入口2")
                        .displayOrder(2)
                        .build()
        );
        
        // When
        quickEntryService.saveQuickEntries(userId, tenantId, entries);
        
        // Then
        verify(quickEntryRepository).saveAll(argThat(collection -> {
            int count = 0;
            for (QuickEntry entry : collection) {
                count++;
            }
            return count == 2;
        }));
    }
    
    @Test
    @DisplayName("测试获取用户快捷入口 - 空入口")
    void testGetQuickEntries_Empty() {
        // Given
        when(quickEntryRepository.findByUserIdAndTenantIdOrderByDisplayOrderAsc(userId, tenantId))
                .thenReturn(List.of());
        
        // When
        List<QuickEntryDTO> result = quickEntryService.getQuickEntries(userId, tenantId);
        
        // Then
        assertThat(result).isEmpty();
    }
    
    @Test
    @DisplayName("测试获取用户快捷入口 - 排序")
    void testGetQuickEntries_Ordering() {
        // Given
        QuickEntry entry1 = QuickEntry.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .tenantId(tenantId)
                .entryId(UUID.randomUUID())
                .name("入口2")
                .displayOrder(2)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        QuickEntry entry2 = QuickEntry.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .tenantId(tenantId)
                .entryId(UUID.randomUUID())
                .name("入口1")
                .displayOrder(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        when(quickEntryRepository.findByUserIdAndTenantIdOrderByDisplayOrderAsc(userId, tenantId))
                .thenReturn(List.of(entry2, entry1));
        
        // When
        List<QuickEntryDTO> result = quickEntryService.getQuickEntries(userId, tenantId);
        
        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDisplayOrder()).isEqualTo(1);
    }
    
    @Test
    @DisplayName("测试获取用户快捷入口 - 异常处理")
    void testGetQuickEntries_Exception() {
        // Given
        when(quickEntryRepository.findByUserIdAndTenantIdOrderByDisplayOrderAsc(userId, tenantId))
                .thenThrow(new RuntimeException("Database error"));
        
        // When & Then
        assertThrows(RuntimeException.class, () ->
                quickEntryService.getQuickEntries(userId, tenantId));
    }
}


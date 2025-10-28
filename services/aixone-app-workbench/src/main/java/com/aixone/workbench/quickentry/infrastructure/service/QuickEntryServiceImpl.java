package com.aixone.workbench.quickentry.infrastructure.service;

import com.aixone.workbench.quickentry.application.dto.QuickEntryDTO;
import com.aixone.workbench.quickentry.domain.model.QuickEntry;
import com.aixone.workbench.quickentry.domain.repository.QuickEntryRepository;
import com.aixone.workbench.quickentry.domain.service.QuickEntryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 快捷入口服务实现
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class QuickEntryServiceImpl implements QuickEntryService {
    
    private final QuickEntryRepository quickEntryRepository;
    
    @Override
    @Cacheable(value = "quickEntryCache", key = "'quickentry:user:' + #userId + ':tenant:' + #tenantId")
    public List<QuickEntryDTO> getQuickEntries(UUID userId, UUID tenantId) {
        log.info("获取用户快捷入口: userId={}, tenantId={}", userId, tenantId);
        
        List<QuickEntry> entries = quickEntryRepository.findByUserIdAndTenantIdOrderByDisplayOrderAsc(userId, tenantId);
        
        return entries.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void saveQuickEntries(UUID userId, UUID tenantId, List<QuickEntryDTO> entries) {
        log.info("保存用户快捷入口: userId={}, tenantId={}", userId, tenantId);
        
        // 删除旧的快捷入口
        quickEntryRepository.deleteByUserIdAndTenantId(userId, tenantId);
        
        // 保存新的快捷入口
        List<QuickEntry> quickEntries = entries.stream()
                .map(dto -> toEntity(dto, userId, tenantId))
                .collect(Collectors.toList());
        
        quickEntryRepository.saveAll(quickEntries);
    }
    
    private QuickEntryDTO toDTO(QuickEntry entry) {
        return QuickEntryDTO.builder()
                .id(entry.getId())
                .userId(entry.getUserId())
                .tenantId(entry.getTenantId())
                .entryId(entry.getEntryId())
                .menuId(entry.getMenuId())
                .name(entry.getName())
                .icon(entry.getIcon())
                .displayOrder(entry.getDisplayOrder())
                .config(entry.getConfig())
                .build();
    }
    
    private QuickEntry toEntity(QuickEntryDTO dto, UUID userId, UUID tenantId) {
        return QuickEntry.builder()
                .userId(userId)
                .tenantId(tenantId)
                .entryId(dto.getEntryId())
                .menuId(dto.getMenuId())
                .name(dto.getName())
                .icon(dto.getIcon())
                .displayOrder(dto.getDisplayOrder())
                .config(dto.getConfig())
                .build();
    }
}


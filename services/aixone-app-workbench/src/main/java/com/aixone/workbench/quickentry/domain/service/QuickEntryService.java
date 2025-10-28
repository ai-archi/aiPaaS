package com.aixone.workbench.quickentry.domain.service;

import com.aixone.workbench.quickentry.application.dto.QuickEntryDTO;

import java.util.List;
import java.util.UUID;

/**
 * 快捷入口领域服务
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
public interface QuickEntryService {
    
    List<QuickEntryDTO> getQuickEntries(UUID userId, UUID tenantId);
    
    void saveQuickEntries(UUID userId, UUID tenantId, List<QuickEntryDTO> entries);
}

